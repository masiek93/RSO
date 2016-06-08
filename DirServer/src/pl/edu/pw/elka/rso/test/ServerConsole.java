package pl.edu.pw.elka.rso.test;


import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.manage.node.NodeRegister;
import pl.edu.pw.elka.rso.repo.MetaDataRepository;
import pl.edu.pw.elka.rso.util.ScreenConsole;
import pl.edu.pw.elka.rso.repo.MetaDataRWRespository;
import pl.edu.pw.elka.rso.repo.MetaDataRepositoryException;
import pl.edu.pw.elka.rso.repo.db.DBFacade;
import pl.edu.pw.elka.rso.repo.domain.FileDTO;

import javax.xml.bind.JAXBException;

import java.io.IOException;
import java.util.List;

/**
 * Uwaga: ta klasa jest do testow.
 */

public class ServerConsole implements Runnable{



    public ServerConsole() {

    }

    public static void printUsage() {
        System.out.printf("usage: command arguments\n");
        System.out.println("command: add | get | list | fileNodes [-A] | dirNodes [-A] | exit");
    }

    public void run() {

        ScreenConsole console = ScreenConsole.getInstance();
        Controller controller = Controller.getInstance();
        MetaDataRepository repository = null;
        NodeRegister nodeRegister = NodeRegister.getInstance();

        try {
            repository = MetaDataRWRespository.getInstance();
        } catch (IOException | JAXBException | MetaDataRepositoryException e) {
            e.printStackTrace();
        }

        printUsage();
        String line;
        while((line = console.readLine()) != null) {
            String [] tokens = line.split("\\s+");
            String command = tokens[0].toLowerCase();

            switch (command) {

                case "add":
                    if(tokens.length < 3)
                        console.printf("error\nUsage: add fileName fileSize");
                    else {
                        String fileName = tokens[1];
                        long fileSize = new Long(tokens[2]);
                        try {
                            List<Node> nodes = controller.addFile(fileName, fileSize);

                            repository.addFile(fileName, fileSize, nodes);
                            for(Node node: nodes) {
                                repository.updateNodeSize(node.getId(), -fileSize);
                            }

                        } catch (SystemException e) {
                           console.printf("cannot add file: %s\n", e.errorCode.toString());
                        } catch (MetaDataRepositoryException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "get":
                    if(tokens.length < 2) {
                        console.printf("error\nUsage: get filename\n");
                    } else {
                        String fileName = tokens[1];
                        try {
                            FileDTO file = controller.getFile(fileName);
                            console.printf("file info:\nname = %s\nsize= %s\nid=%d\nfileServers=[",
                                    file.getFileName(), file.getFileSize(), file.getFileId());
                            for(Node node: file.getNodes()) {
                                if(node != null && node.getId() != null)
                                    console.printf("%d, ", node.getId());
                            }
                            console.printf("]\n\n");
                        } catch (SystemException e) {
                            console.printf("cannot get file: %s \n", e.errorCode.toString());
                        }
                    }
                    break;
                case "list":
                    List<FileDTO> files = controller.getFileList();
                    for(FileDTO file: files) {
                        console.printf("file info:\nname = %s\nsize= %s\nid=%d\nfileServers=[",
                                file.getFileName(), file.getFileSize(), file.getFileId());
                        for(Node node: file.getNodes()) {
                            if(node != null && node.getId() != null)
                                console.printf("%d, ", node.getId());
                        }
                        console.printf("]\n\n");
                    }
                    break;
                case "delete":
                    if(tokens.length < 2) {
                        console.printf("error: usage: delete fileName");
                    } else {
                        try {
                            if(tokens[1].equals("*")) {
                                List<FileDTO> stars = controller.getFileList();
                                for(FileDTO star: stars) {
                                    controller.deleteFile(star.getFileName());
                                }
                            } else {
                                FileDTO fileToDelete = controller.getFile(tokens[1]);

                                if(fileToDelete != null) {
                                    for (Node node : fileToDelete.getNodes()) {
                                        if (node != null && node.getId() != null) {
                                            repository.updateNodeSize(node.getId(), +fileToDelete.getFileSize());
                                        }
                                    }
                                }
                                controller.deleteFile(tokens[1]);

                            }

                        } catch (SystemException e) {
                            System.out.println("error while deleting a file: " + e.errorCode.toString());
                            e.printStackTrace();
                        }
                    }

                    break;
                case "filenodes":
                    List<Node> fileNodes = null;
                    if(tokens.length < 2) {
                        console.printf("all file nodes: \n");
                        fileNodes = NodeRegister.getInstance().getFileNodes();
                    } else {
                        console.printf("active nodes: \n");
                        fileNodes = NodeRegister.getInstance().getAliveFileNodes();
                    }

                    if(fileNodes == null || fileNodes.isEmpty()) {
                        System.out.println("there is no file node");
                    }

                    for (Node node : fileNodes) {
                        System.out.println(node);
                    }
                    break;
                case "dirnodes":
                    List<Node> dirNodes = null;
                    if(tokens.length < 2) {
                        console.printf("all directory nodes: \n");
                        dirNodes = NodeRegister.getInstance().getDirectoryNodes();
                    } else {
                        console.printf("active nodes: \n");
                        dirNodes = NodeRegister.getInstance().getAliveDirectoryNodes();
                    }
                    if(dirNodes == null || dirNodes.isEmpty()) {
                        System.out.println("there is no dir node");
                    }
                    for (Node node : dirNodes) {
                        System.out.println(node);
                    }
                    break;

                case "q":case "quit":case "exit":case "bye":
                    DBFacade.getInstance().closeConnection();
                    System.exit(0);
                default:
                    System.out.println("command not recognized\n");
                    printUsage();
                    break;
            }
        }


    }

    public void runTest() {


        for(int i = 0; i < 10; ++i) {
            long nodeSize = 1000+i*100;
            boolean isAlive = i % 2 == 0;
            long nodeId = i;

            NodeRegister.getInstance().registerNode(Node.createFileNode("localhost", nodeId, isAlive, nodeSize));
        }
        run();
    }

    public static void main(String[] args) {
        new ServerConsole().runTest();

    }


}
