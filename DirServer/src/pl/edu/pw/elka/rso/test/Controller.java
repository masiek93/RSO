package pl.edu.pw.elka.rso.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.repo.MetaDataRWRespository;
import pl.edu.pw.elka.rso.repo.MetaDataRepository;
import pl.edu.pw.elka.rso.repo.MetaDataRepositoryException;
import pl.edu.pw.elka.rso.repo.domain.FileDTO;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * przyklady uzycia metdatarepository. Ta klasa zostala stworzona na potrzeby testow MetaDataRepository.
 *
 * MetaDataRepository nie jest odpowiedzialna za sprawdzenia czy wezel jest dostepny albo czy ma wystarczajaco duzo miejsca zeby zapisac dany plik.
 *
 *
 */
public class Controller {



    static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    // serwis bazy danych
    MetaDataRepository repository;
    // serwis - rejestr wezlow
    NodeRegister nodeRegister = NodeRegister.getInstance();;


    private static Controller instance;

    public static Controller getInstance() {
        if(instance == null)
            instance = new Controller();
        return instance;
    }

    public void initService() {
        NodeRegister nodeRegister = NodeRegister.getInstance();
        // register stub nodes

        try {
            repository = MetaDataRWRespository.getInstance();
        } catch (IOException | JAXBException | MetaDataRepositoryException e) {
            System.out.println("Cannot initialized repository");
            System.exit(2);
        }
    }

    private Controller() {
        initService();
    }

    enum ErrorCode {
          NO_AVAILABLE_NODES_TO_WRITE,
          FILE_ALREADY_EXISTS,
        FILE_DOESNT_EXIST, INTERNAL_ERROR, NOT_ENOUGH_SPACE_TO_WRITE
    }

    class SystemException extends Exception{
        ErrorCode errorCode;
        String message;

        public SystemException(ErrorCode errorCode, String message) {
            this.errorCode = errorCode;
            this.message = message;
        }

        public SystemException(ErrorCode errorCode) {
            this.errorCode = errorCode;
        }
    }


    public List<Node> pickTwoNodes(List<Node> nodes) {
        if(nodes.size() <= 2) {
            return nodes;
        }
        Random random = new Random();
        int fst = random.nextInt(nodes.size());
        int snd = random.nextInt(nodes.size());
        while(snd == fst)
            snd = random.nextInt(nodes.size());
        return Arrays.asList(nodes.get(fst), nodes.get(snd));
    }


    private void preExec() {
        repository.updateNodesTable(nodeRegister.getAliveFileNodes());
    }

    private void postExec() {
        // nothing
    }


    /**
     * Uwaga: dodawania to nie to samo co nadpisanie pliku.
     */
    public List<Node> addFile(String fileName, long size) throws SystemException {

        preExec();

        // sprawdzamy czy istniej plik o takiej nazwie

        if(repository.fileExists(fileName)) {
            // rzuc wyjatkiem zeb zasygnalizowac klientowi ze istnieje juz taki plik
            throw new SystemException(ErrorCode.FILE_ALREADY_EXISTS);
        }

        List<Node> aliveFileNodes = getAliveNodes();
        List<Node> nodesToAddFile = getNodesWithEnoughSpace(size, aliveFileNodes);
        // rownowazenie obciazen
        List<Node> pickedNodes = pickTwoNodes(nodesToAddFile);

        postExec();

        return pickedNodes;
    }

    public List<FileDTO> getFileList() {

        return repository.getFileList();
    }


    public boolean deleteFile(String filename) throws SystemException {
        preExec();
        if(!repository.fileExists(filename)) {
            // zwracmy ok, chociaz plik nie istniej
            return true;
        }
        try {
            repository.deleteFile(filename);
        } catch (MetaDataRepositoryException e) {
            throw new SystemException(ErrorCode.FILE_DOESNT_EXIST);
        }
        postExec();
        return true;
    }

    public FileDTO getFile(String filename) throws SystemException {
        preExec();
        if(!repository.fileExists(filename)) {
            throw new SystemException(ErrorCode.FILE_DOESNT_EXIST);
        }

        try {
            return repository.getFile(filename);
        } catch (MetaDataRepositoryException e) {
           throw new SystemException(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }


    }



    private List<Node> getNodesWithEnoughSpace(long size, List<Node> aliveFileNodes) throws SystemException {
        // sprawdzamy ilosc wolnego miejsca na kazdym z wezlow
        // uwaga: nie mozna pobrac ilosc wolnego miejsca od nodeRegister. Nalezy to zrobic wylacznie od MetaRepository

        List<Node> nodesToAddFile = new ArrayList<>();
        for(Node node: aliveFileNodes) {
            if(repository.getAvailableSize(node.getId()) > size) {
                nodesToAddFile.add(node);
            }
        }

        // tylko wtedy gdy liczba wezlow jest >= 2 to mozemy zapisac
        if(nodesToAddFile.size() < 2) {
            throw new SystemException(ErrorCode.NOT_ENOUGH_SPACE_TO_WRITE);
        }

        return nodesToAddFile;
    }

    private List<Node> getAliveNodes() throws SystemException {
        // sprawdzamy czy sa jakies dostepne wezly

        List<Node> aliveFileNodes = nodeRegister.getAliveFileNodes();
        if(aliveFileNodes.size() < 2) {
            // zwracamy blad do klienta
            throw new SystemException(ErrorCode.NO_AVAILABLE_NODES_TO_WRITE);
        }
        return aliveFileNodes;
    }

}
