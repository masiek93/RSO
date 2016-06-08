package pl.edu.pw.elka.rso.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.config.Config;
import pl.edu.pw.elka.rso.config.DirectoryServerConf;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.repo.domain.FileDTO;
import pl.edu.pw.elka.rso.test.SystemException;
import pl.edu.pw.elka.rso.util.ScreenConsole;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class ClientConsole implements Runnable {


    Logger LOGGER = LoggerFactory.getLogger(ClientConsole.class);

    public ClientConsole() {

    }

    public static void printUsage() {
        System.out.printf("usage: command arguments\n");
        System.out.println("command: upload | download | delete | list | test | exit");
    }


    public DirServerClient pickDirServerClient() {
        try {
            List<DirectoryServerConf> servers = Config.getInstance().directoryServerList;
            for (DirectoryServerConf server : servers) {
                DirServerClient dsc = new DirServerClient(server.address, server.clientPort);
                if (dsc.test()) {
                    return dsc;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<FileServerClient> findAliveFileServers(List<Node> nodes) {
        List<FileServerClient> fileServerClients = new ArrayList<>();
        for (Node node : nodes) {
            if (node.isFileServer()) {
                FileServerClient fileServerClient = new FileServerClient(node.getAddress(), node.getPort());
                if (fileServerClient.test()) {
                    fileServerClients.add(fileServerClient);
                }
            }
        }
        return fileServerClients;
    }


    public void run() {

        ScreenConsole console = ScreenConsole.getInstance();


        printUsage();


        String line;
        while ((line = console.readLine()) != null) {

            if (line.trim().isEmpty()) {
                printUsage();
                continue;
            }

            String[] tokens = line.split("\\s+");
            String command = tokens[0].toLowerCase();

            DirServerClient dirServerClient = pickDirServerClient();
            if (dirServerClient == null) {
                console.printf("internal server error = could not find any alive directory node.\n");
                continue;
            }

            try {

                switch (command) {

                    case "upload":
                    {
                        //  String filePath = null;
                        if (tokens.length < 3) {
                            console.printf("error\nUsage: upload localPath serverPath");
                        } else {

                            String localPath = tokens[1];
                            String serverPath = tokens[2];


                            if (!Files.exists(Paths.get(localPath))) {
                                throw new SystemException(null, String.format("local file %s not found", localPath));

                            }


                            List<Node> nodes = dirServerClient.addFile(localPath, serverPath);
                            if (nodes != null && nodes.size() > 0) {

                                List<FileServerClient> clients = findAliveFileServers(nodes);
                                console.printf("uploading to file servers....\n");
                                int successNo = 0;

                                for (FileServerClient fsc : clients) {
                                    try {
                                        LOGGER.info("trying {}:{}", fsc.getAddress(), fsc.getPort());
                                        fsc.uplodaFile(localPath, serverPath);
                                        successNo++;
                                    } catch (IOException e) {
                                        LOGGER.error("error while uploading to {}:{}", fsc.getAddress(), fsc.getPort(), e);
                                    }
                                }

                                if (successNo == 0) {
                                    console.printf("uploading file %s failed.\n", serverPath);
                                }

                            }

                        }

                        break;
                    }
                    case "download":

                    {

                        if (tokens.length < 3) {

                            console.printf("error\nUsage: download serverPath localPath\n");

                        } else {

                            String serverPath = tokens[1];
                            String localPath = tokens[2];


                            FileDTO fileDTO = dirServerClient.getFile(serverPath);

                            if (fileDTO == null) {

                                console.printf("could not find file %s ", serverPath);

                            } else {

                                printFileInfo(console, fileDTO);
                                printFileInfo(console, fileDTO);

                                List<FileServerClient> clients = findAliveFileServers(fileDTO.getNodes());
                                boolean downloaded = false;

                                for (FileServerClient fileServerClient : clients) {
                                    try {
                                        fileServerClient.downloadFile(serverPath, localPath);
                                        console.printf("sucess: got %s\n", serverPath);
                                        downloaded = true;
                                        break;
                                    } catch (IOException e) {
                                        // console.printf("failur\n");
                                    }
                                }
                                if (!downloaded) {
                                    console.printf("could not download file %s", serverPath);
                                }

                            }

                        }


                    }
                    break;

                    case "list":
                        //fileServerClient = new FileServerClient("localhost", 13267);

                        if (tokens.length < 2) {
                            console.printf("error\nUsage: list local|global\n");

                        } else {

                            if (tokens[1].equalsIgnoreCase("local")) {
                                if (tokens.length > 2 && tokens[2] != null) {
                                    Files.list(Paths.get(tokens[2])).forEach(System.out::println);
                                } else {
                                    Files.list(Paths.get(".")).forEach(System.out::println);
                                }

                            } else {
                                List<FileDTO> files = dirServerClient.getFileList();
                                if (files != null) {
                                    for (FileDTO file : files) {
                                        printFileInfo(console, file);
                                    }

                                }

                            }


                        }

                        break;
                    case "test":

                        console.printf("not implemented yet!\n");
                        break;
                    case "delete":

                        if (tokens.length < 2) {
                            console.printf("error\nUsage: get serverPath\n");

                        } else {


                            String serverPath = tokens[1];

                            List<Node> nodes = dirServerClient.deleteFile(serverPath);
                            if (nodes == null || nodes.size() == 0) {
                                console.printf("could not find this file\n");
                            } else {
                                List<FileServerClient> clients = findAliveFileServers(nodes);

                                try {
                                    for (FileServerClient fileServerClient : clients) {
                                        fileServerClient.deleteFile(serverPath);
                                        console.printf("success. file deleted\n");
                                    }
                                } catch (IOException e) {
                                    console.printf("failur\n");
                                }

                            }


                        }

                        break;
                    case "q":
                    case "quit":
                    case "exit":
                    case "bye":
                        System.exit(0);
                    default:
                        System.out.println("command not recognized\n");
                        printUsage();
                        break;
                }


            } catch (SystemException | IOException e) {
                console.printf(e.getMessage() + "\n");
                if(e instanceof SystemException) {
                    console.printf("reason = %s\n", ((SystemException)e).getErrorCode());
                }
                LOGGER.error("error ", e);
            }
        }

    }

    private void printFileInfo(ScreenConsole console, FileDTO file) {
        console.printf("file info:\nname = %s\nsize= %s\nid=%d\nfileServers=[",
                file.getFileName(), file.getFileSize(), file.getFileId());
        for (Node node : file.getNodes()) {
            if (node != null && node.getId() != null)
                console.printf("%d, ", node.getId());
        }
        console.printf("]\n\n");
    }


    public static void main(String[] args) {
        new ClientConsole().run();

    }


}
