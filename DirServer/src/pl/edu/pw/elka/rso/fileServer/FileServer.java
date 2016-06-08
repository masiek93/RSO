package pl.edu.pw.elka.rso.fileServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.manage.client.FileNodeListener;
import pl.edu.pw.elka.rso.manage.screen.FileNodeScreen;
import pl.edu.pw.elka.rso.manage.screen.FileNodeScreenDataProvider;
import pl.edu.pw.elka.rso.ssl.SServerSocketFactory;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;

public class FileServer {


    FileNodeListener fileNodeListener;


    String fileStoragePath;

    int port;

    static final Logger LOGGER = LoggerFactory.getLogger(FileServer.class);





    public FileServer(String fileStoragePath, String fileIdPath, Integer port) {
        this.fileStoragePath = fileStoragePath;

        long space = 500*1000; // defaultowo jest 500 MB

        try {
            if(!Files.exists(Paths.get(this.fileStoragePath))) {
                new File(this.fileStoragePath).mkdir();
            }
            space = Files.getFileStore(Paths.get(fileStoragePath)).getUsableSpace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileNodeListener = new FileNodeListener(fileIdPath, space, port);
        this.port = port;

        FileNodeScreen.start(new FileNodeScreenDataProvider(fileNodeListener));
        FileNodeScreen.setSilent(true);

        new Thread(fileNodeListener).start();

        LOGGER.info("FileServer was initialized successfully.");

    }

    void communicator(Socket socket) {
        Object object = null;
        boolean insecureMode;

        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;



        Socket comunicationSocket = null;
        ObjectOutputStream oos2 = null;
        ObjectInputStream ois2 = null;



        try {

            try {

                LOGGER.info("Server has accepted a new connetion with client = {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                FileHandler fh = new FileHandler();

                object = ois.readObject();

                if (object instanceof SystemMessage) {
                    SystemMessage systemMessage = (SystemMessage) object;
                    if (systemMessage.getOperation().equals(Operation.GET_FREE_SPACE)) {
                        File file = new File(fileStoragePath);
                        Long free_space = file.getUsableSpace();
                        oos.writeObject(free_space);
                    }
                    if (systemMessage.getOperation().equals(Operation.GET_FILE_LIST)) {
                        File folder = new File(fileStoragePath);
                        String[] listOfFiles = folder.list();
                        oos.writeObject(listOfFiles);
                    }
                }
                if (object instanceof DownloadFileMessage) {
                    LOGGER.info("Got DownloadFileMessage Request.");
                    DownloadFileMessage dgm = (DownloadFileMessage) object;
                    fh.downloadFile(fileStoragePath + "/" + dgm.getId(), ois, oos);
                    LOGGER.info("File {} downloaded successfully", dgm.getId());
                }
                if (object instanceof DeleteFileMessage) {
                    LOGGER.info("Got DeleteFileMessage Request.");

                    DeleteFileMessage dflm = (DeleteFileMessage) object;
                    String path_str = fileStoragePath + "/" + dflm.getId();
                    Path path = Paths.get(path_str);
                    try {
                        Files.delete(path);
                        LOGGER.info("File {} deleted", path);
                    } catch (NoSuchFileException x) {
                        LOGGER.error(String.format("%s: no such" + " file or directory%n", path));
                    } catch (DirectoryNotEmptyException x) {
                        LOGGER.error(String.format("%s not empty%n", path));
                    } catch (IOException x) {
                        // File permission problems are caught here.
                        LOGGER.error("unkown problem", x);
                    }
                }
                if (object instanceof UploadFileMessage) {
                    LOGGER.info("Got UploadFileMessage Request.");

                    UploadFileMessage ufm = (UploadFileMessage) object;
                    String path = fileStoragePath + "/" + ufm.getId();

                    fh.uploadFile(path, (int) ufm.getSizeInBytes(), ois, oos);
                    // send notification to directory server
                    //confirmationMessageToDirectoryServer(socketToDirectoryServer, Type.FILE_RECIVED, Status.SUCCESSFUL, ufm.getId(), getDigest(path), serverID);

                    LOGGER.info("File {} uploaded successfully", ufm.getId());

                }
                if (object instanceof ForwardFileMessage) {

                    ForwardFileMessage ffm = (ForwardFileMessage) object;
                    UploadFileMessage ufm = new UploadFileMessage();
                    ufm.setId(ffm.getId());
                    String path = fileStoragePath + "/" + ffm.getId();
                    File file = new File(path);
                    ufm.setSizeInBytes(file.length());

                    comunicationSocket = new Socket(ffm.getDestinationAddress(), ffm.getDestinationPort());

                    oos2 = new ObjectOutputStream(comunicationSocket.getOutputStream());
                    ois2 = new ObjectInputStream(comunicationSocket.getInputStream());

                    oos2.writeObject(ufm);

                    // uwaga: nazwa jest mylaca, ale nie mozna jej zmienic
                    // tutaj chodzi o to ze communicationSocket sciaga do siebie plik, a nie
                    // my sciagamy
                    fh.downloadFile(path, ois2, oos2);
                }

            } finally {
                if (ois != null) ois.close();
                if (oos != null) oos.close();
                if (socket != null) socket.close();

                if (ois2 != null) ois2.close();
                if (oos2 != null) oos2.close();
                if (comunicationSocket != null) comunicationSocket.close();
            }
        } catch (Exception e) {
            if(e.getMessage()!=null) {
                LOGGER.error("unkown error", e);
            }
        }
        LOGGER.info("closing connection with client {}:{}", socket.getInetAddress(), socket.getPort());
    }

//    void confirmationMessageToDirectoryServer(Type type, Status status, String id, byte[] hash, long serverID) {
//
//        ConfirmationMessage cm = new ConfirmationMessage();
//        cm.setStatus(status);
//        cm.setType(type);
//        cm.setId(id);
//        cm.setHash(hash);
//        cm.setServerID(serverID);
//
//        ObjectOutputStream oos_dirServer = null;
//        try {
//            try {
//                oos_dirServer = new ObjectOutputStream(socketToDirectoryServer.getOutputStream());
//                oos_dirServer.writeObject(cm);
//                //			moze sie zaciac bo nie ma ObjectInputStream
//            } finally {
//
//                if (oos_dirServer != null) oos_dirServer.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//    }
//
//    static byte[] getDigest(String filename) {
//        MessageDigest m = null;
//        String string = null;
//        try {
//            Path p1 = Paths.get(filename);
//            Charset charset = Charset.forName("utf-8");
//            string = Files.readAllLines(p1, charset).toString();
//            m = MessageDigest.getInstance("MD5");
//        } catch (NoSuchAlgorithmException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        m.reset();
//        m.update(string.getBytes());
//        byte[] digest = m.digest();
//        BigInteger bigInt = new BigInteger(1, digest);
//        System.out.println("hashed file " + filename + ": " + bigInt.toString(16));
//        return digest;
//    }
//
//    // nieuzywane
//    Socket getDirectoryServerSocket() {
//        Socket socket = null;
//        do {
//            try {
//                try {
//                    socket = SSocketFactory.createSocket(directoryServerAddress, directoryServerPort);//new Socket(directoryServerAddress, directoryServerPort);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    socket = SSocketFactory.createSocket(RedundantDirectoryServerAddress, RedundantDirectoryServerPort);//new Socket(RedundantDirectoryServerAddress, RedundantDirectoryServerPort);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e1) {
//                    // TODO Auto-generated catch block
//                    e1.printStackTrace();
//                }
//            }
//        } while (socket == null);
//        return socket;
//
//    }
//
//    // nie uzywane
//    void startServer(String fileStoragePath) throws IOException, JAXBException {
//
//        Config cnf = Config.getInstance();
//
//
//        directoryServerAddress = cnf.directoryServerList.get(0).address;
//        directoryServerPort = cnf.directoryServerList.get(0).nodesManagementPort;
//
//        RedundantDirectoryServerAddress = cnf.directoryServerList.get(1).address;
//        RedundantDirectoryServerPort = cnf.directoryServerList.get(1).nodesManagementPort;
//
//        //TODO Czytam konfiguracje i zapisuje adres i port  SK i  SKR
//        Socket socketToDirectoryServer = getDirectoryServerSocket();
//        MessageInputStream mis = null;
//        MessageOutputStream mos = null;
//        System.out.println("Diractory Server has accepted connetion" + socketToDirectoryServer);
//        try {
//            mis = new MessageInputStream(socketToDirectoryServer.getInputStream());
//            mos = new MessageOutputStream(socketToDirectoryServer.getOutputStream());
//            //Zarejestrowac się u SK  (pobrać ID,wyslac listę portow na ktorych słucham)
//            mos.writeMessage(Messages.fileSrvRegReqMsg(SOCKET_PORT, FILE_SOCKET_PORT));
//            serverID = (Long) mis.readMessage().getData();
//
//            //wysłać ilość wolnego miejsca
//            File file = new File(fileStoragePath);
//            Long freeSpace = file.getUsableSpace();
//            mos.writeMessage(Messages.freeSpaceMsg(freeSpace));
//
//            //wysłać listę plików
//            File folder = new File(fileStoragePath);
//            String[] listOfFiles = folder.list();
//            mos.writeMessage(Messages.fileListMsg(listOfFiles));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    static int serverPort;
    static String storageDirectory;
    static String idFilePath;

    public static void main(String[] args) {


        if(args.length < 3) {
            System.out.println("Error: bad arguments");
            System.out.println("Usage: FileServer storageDirectory idFilePath serverPort");
            System.out.println("Example: FileServer storage1 resources/gen/id.txt 3456");

            System.exit(1);
        } else {
            storageDirectory = args[0];
            idFilePath = args[1];
            serverPort = Integer.valueOf(args[2]);
        }

        FileServer fs = new FileServer(storageDirectory, idFilePath, serverPort);



         ServerSocket servsock = null;


        try {
            servsock = SServerSocketFactory.createServerSocket(fs.port);// new ServerSocket(SOCKET_PORT);
            //fileServsock = SServerSocketFactory.createServerSocket(FILE_SOCKET_PORT);// new ServerSocket(FILE_SOCKET_PORT);
            LOGGER.info("server socket is listening for new connections on {}", fs.port);
        } catch (Exception e) {
            LOGGER.error("unable to start serversocket", e);
            System.exit(2);
        }

        while (true) {

            try {
                final Socket socket = servsock.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        fs.communicator(socket);
                    }
                }).start();
            } catch (IOException e) {
                LOGGER.error("error while accepting a new connection", e);
            }

        }

    }

}
