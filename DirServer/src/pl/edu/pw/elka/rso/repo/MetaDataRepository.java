package pl.edu.pw.elka.rso.repo;

import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.repo.domain.FileDTO;

import java.util.List;

/**
 * A service that is responsible for storing and managing metadata.
 */
public interface MetaDataRepository {


    /**
     *
     * @return List of files with their location on the file servers
     * @throws MetaDataRepositoryException in case of failure
     */
    List<FileDTO> getFileList();


    /**
     * Return FileDTO containing information about the file and its location in the system.
     * @param fileName the name of the file
     * @return FileDTO object with info about this file
     * @throws MetaDataRepositoryException if the file doesn't exist
     */
    FileDTO getFile(String fileName) throws MetaDataRepositoryException;

    /**
     * Add file to the system. Return info about the file.
     * @param fileName the name of the file to be added
     * @param size the size of the file to be added
     * @return information about the file
     * @throws MetaDataRepositoryException if there is already file with this name (name duplication)
     */
    FileDTO addFile(String fileName, long size, List<Node> nodes) throws MetaDataRepositoryException;


    FileDTO addFile(String fileName, long size) throws MetaDataRepositoryException;

    void addFileToFileServer(long fileId, Node node) throws MetaDataRepositoryException;

    /**
     * Delete a file with given name from the system.
     * @param fileName the name of the file to be deleted
     * @throws MetaDataRepositoryException if the file cannot be deleted
     */
    List<Node> deleteFile(String fileName) throws MetaDataRepositoryException;


    /**
     * Add node to this repository.
     * @param node
     */
    void addNode(Node node);


    void updateNodeSize(long nodeId, long updateValue);

    
    long getAvailableSize(long nodeId);
    
    void updateNodesTable(List<Node> nodes);
    
    

    Long getFileSerialId();

    boolean fileExists(String fileName);


    Long getFileId(String fileName);

    String getFileName(long fileId);

}
