package pl.edu.pw.elka.rso.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.manage.events.DatabaseModifiedEvent;
import pl.edu.pw.elka.rso.manage.events.EventBus;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.manage.node.NodeType;
import pl.edu.pw.elka.rso.repo.db.*;
import pl.edu.pw.elka.rso.repo.domain.FileDTO;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * NOTICE: Synchronized method cant call another synchronized method because of the possibility of
 * deadlocks.
 */
public class MetaDataRWRespository implements MetaDataRepository {

    // file servers accessed mainly by id
    private Map<Long, Node> fileNodes = new ConcurrentHashMap<>();
    // file info accessed mainly by id
    private Map<Long, FileDTO> files = new ConcurrentHashMap<>();
    private Map<String, Long> fileNameIndex = new ConcurrentHashMap<>();

    private Long fileSerialId = 0l;

    public static final long META_DATA_RW_REPOSITORY_ID = -3;
    public static final long SYNCHRONIZATION_EVENT_LISTENER = -1;

    private EventBus eventBus;

    static final Logger LOGGER = LoggerFactory.getLogger(MetaDataRWRespository.class);


    static MetaDataRepository metaDataRepository;

    public synchronized static MetaDataRepository getInstance() throws IOException, JAXBException, MetaDataRepositoryException {

        if(metaDataRepository == null) {
            DbContainer container = DBFacade.getInstance().backup();
            metaDataRepository = new MetaDataRWRespository(container);
        }

        return metaDataRepository;
    }

    public MetaDataRWRespository(DbContainer container) throws MetaDataRepositoryException{
        if(container == null) {
            throw new MetaDataRepositoryException("db_container cannot be null");
        }
        eventBus = EventBus.getInstance();
        init(container);
    }


    private void init(DbContainer container) {

        for(FileServer node: container.fileServerTable) {
            Node n = new Node();
            n.setAddress(node.get_ip_address());
            n.setId((long) node.get_server_id());
            n.setNodeType(NodeType.FILE_NODE);
            n.setSize((long) node.get_server_size());
            fileNodes.put(n.getId(), n);
        }

        for(FileInfoStorager fileInfoStorager: container.fileInfoStoragerTable) {
            FileDTO fileDTO = new FileDTO();
            fileDTO.setFileId(fileInfoStorager.get_file_id());

            fileDTO.setFileName(fileInfoStorager.get_name());
            // TODO: fix the type mismatch between FileDTO and file_info_storager
            //   fileDTO.setCreationDate(fileInfoStorager.get_creation_time());
            // TODO: there is a type mismatch between database (using int) and our application (using long)
            fileDTO.setFileSize((long) fileInfoStorager.get_size());


            for(FileOnFileServer file_on_serverfile: container.fileOnServerFileTable) {
                if(file_on_serverfile.get_file_id() == fileDTO.getFileId()) {
                    long serverId = file_on_serverfile.get_server_id();
                    Node node = fileNodes.get(serverId);

                    fileDTO.add(node);
                }
            }

            files.put((long) fileInfoStorager.get_file_id(), fileDTO);
            fileNameIndex.put(fileInfoStorager.get_name(), (long) fileInfoStorager.get_file_id());
        }

        fileSerialId = files.size() > 0 ? Collections.max(files.keySet())+1 : 0l;

        LOGGER.info("database has been read into memory. fileSerialId = {}", fileSerialId);
    }

    @Override
    public synchronized List<FileDTO> getFileList() {
        return new ArrayList<>(files.values());
    }


    @Override
    public synchronized FileDTO getFile(String fileName) throws MetaDataRepositoryException {

        FileDTO file = getFileIfExists(fileName);
        if (file != null) return file;
        throw new MetaDataRepositoryException("file Not found");
    }

    private FileDTO getFileIfExists(String fileName) {
        Long fileId = fileNameIndex.get(fileName);
        if(fileId != null) {
            return files.get(fileId);
        }
        return null;
    }

    @Override
    public synchronized FileDTO addFile(String fileName, long size, List<Node> nodes) throws MetaDataRepositoryException {

        Long fileId = fileNameIndex.get(fileName);
        if(fileId != null) {
            throw new MetaDataRepositoryException("file " + fileName + " already exists");
        }

        for(Node node: nodes) {
            addNode(node);
        }

        // stub method
        FileDTO newFile = new FileDTO();
        newFile.setFileId(fileSerialId);

        newFile.setFileName(fileName);
        newFile.setFileSize(size);
        newFile.setCreationDate(new Date());

        nodes.forEach(newFile::add);

        // bookeeping methods

        files.put(newFile.getFileId(), newFile);
        fileNameIndex.put(newFile.getFileName(), newFile.getFileId());
        fileSerialId++;


        LOGGER.info("File {} has been added to repository.", fileName);
        publishModification(DBFacade.insertFileInfoStmt(newFile));


        return newFile;
    }

    @Override
    public FileDTO addFile(String fileName, long size) throws MetaDataRepositoryException {
        Long fileId = fileNameIndex.get(fileName);
        if(fileId != null) {
            throw new MetaDataRepositoryException("file " + fileName + " already exists");
        }

        // stub method
        FileDTO newFile = new FileDTO();
        newFile.setFileId(fileSerialId);

        newFile.setFileName(fileName);
        newFile.setFileSize(size);
        newFile.setCreationDate(new Date());



        // bookeeping methods

        files.put(newFile.getFileId(), newFile);
        fileNameIndex.put(newFile.getFileName(), newFile.getFileId());

        fileSerialId++;

        LOGGER.info("File {} has been added to repository.", fileName);
        publishModification(DBFacade.insertFileInfoStmt(newFile));
        return newFile;
    }

    public synchronized void lockFile(long fileId) {
        files.get(fileId).lock();
        LOGGER.info("locking file with id = {}", fileId);
        publishModification(DBFacade.lockFileStmt(fileId));
    }

    public synchronized void unlockFile(long fileId) {

        files.get(fileId).unlock();
        LOGGER.info("unlocking file with id = {}", fileId);
        publishModification(DBFacade.unlockFileStmt(fileId));
    }

    public synchronized boolean isLocked(long fileId) {
        return files.get(fileId).isLocked();
    }

    @Override
    public synchronized void addFileToFileServer(long fileId, Node node) throws MetaDataRepositoryException {
        FileDTO fdto = files.get(fileId);
        if(fdto == null) {
            throw new MetaDataRepositoryException("there is no file with id = " + fileId);
        }
        if(node == null || node.getId() == null) {
            throw new MetaDataRepositoryException("node or node.id is null");
        }
        List<Node> ns = fdto.getNodes().stream().filter(n -> node.getId().equals(n.getId())).collect(Collectors.toList());
        if(ns.isEmpty()) {
            fdto.add(node);
            publishModification(DBFacade.addFileToFileServerStmt(fileId, node.getId()));
        }
    }


    @Override
    public synchronized List<Node> deleteFile(String fileName) throws MetaDataRepositoryException {

            FileDTO fdto = getFileIfExists(fileName);
            if(fdto == null) {
                throw new MetaDataRepositoryException("cannot delete file " + fileName + " because it doesnt exist");
            }
            List<Node> nodes = fdto.getNodes();

            files.remove(fdto.getFileId());
            fileNameIndex.remove(fdto.getFileName());

            LOGGER.info("{} is being removed from repository", fileName);
            // remove from db
            publishModification(DBFacade.removeFileStmt(fdto.getFileId()));

            return nodes;

    }

    private void publishModification(String sqlStmt) {
        LOGGER.info("a sql statement has been published: \n" + sqlStmt);

        eventBus.publish(new DatabaseModifiedEvent(sqlStmt,
                META_DATA_RW_REPOSITORY_ID, SYNCHRONIZATION_EVENT_LISTENER));
    }

    @Override
    public synchronized void addNode(Node node) {

        if( node != null && node.getId() != null && node.isFileServer()) {
            Node existing = fileNodes.get(node.getId());
            if(existing == null ) {
                LOGGER.info("adding a new node to repository: " + node);
                fileNodes.put(node.getId(), node);
                publishModification(DBFacade.insertFileServerStmt(node));
            } else {
                LOGGER.debug("updating node info " + node);
                existing.setSize(node.getSize());
                existing.setAddress(node.getAddress());
                existing.setAlive(node.isAlive());

                NodeRegister.getInstance().setNodeSize(node.getId(), fileNodes.get(node.getId()).getSize());

                publishModification(DBFacade.updateNodeSizeStmt(fileNodes.get(node.getId())));
            }
        }
    }

    @Override
    public synchronized void updateNodeSize(long nodeId, long updateValue) {
        if(fileNodes.get(nodeId) != null) {

            LOGGER.info("updating a node: " + fileNodes.get(nodeId));

            fileNodes.get(nodeId).updateSize(updateValue);
            NodeRegister.getInstance().setNodeSize(nodeId, fileNodes.get(nodeId).getSize());

            publishModification(DBFacade.updateNodeSizeStmt(fileNodes.get(nodeId)));
        }
    }

    @Override
    public synchronized long getAvailableSize(long nodeId) {
        Node fileNode = fileNodes.get(nodeId);
        return fileNode != null ? fileNode.getSize() : 0;
    }

    @Override
    public synchronized void updateNodesTable(List<Node> nodes) {
        nodes.forEach(n -> addNode(n));
    }

    @Override
    public synchronized Long getFileSerialId() {
        return fileSerialId;
    }

    @Override
    public synchronized boolean fileExists(String fileName) {
        return fileNameIndex.get(fileName) != null;
    }

    @Override
    public Long getFileId(String fileName) {
        return fileNameIndex.get(fileName);
    }

    @Override
    public String getFileName(long fileId) {
        return files.get(fileId).getFileName();
    }


}
