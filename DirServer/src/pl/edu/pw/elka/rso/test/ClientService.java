package pl.edu.pw.elka.rso.test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.fileServer.UploadFileMessage;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.message.Code;
import pl.edu.pw.elka.rso.message.Message;
import pl.edu.pw.elka.rso.repo.domain.FileDTO;
import pl.edu.pw.elka.rso.ssl.SServerSocketFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ClientService extends Thread {

    static Logger LOGGER = LoggerFactory.getLogger(ClientService.class);

    private int port;
    private ServerSocket serverSocket;
    private Controller controller;

    public ClientService(int port) {
        this.port = port;
        controller = Controller.getInstance();
    }

    class ClientHandler extends Thread {

        Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            LOGGER.info("new client connected = {}:{}", socket.getInetAddress(), socket.getPort());
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                Message req = (Message) ois.readObject();
                Message response = new Message();

                try {
                    response.setCode(Code.OK);

                    switch (req.getType()) {
                        case GET_FILE:
                            String filename = (String) req.getData();
                            FileDTO fdt = controller.getFile(filename);
                            response.setData(fdt);
                            break;
                        case GET_FILE_LIST:
                            List<FileDTO> fileList = controller.getFileList();
                            response.setData(fileList);
                            break;
                        case ADD_FILE:
                            UploadFileMessage ufm = (UploadFileMessage) req.getData();
                            List<Node> nodes = controller.addFile(ufm.getId(), ufm.getSizeInBytes());
                            response.setData(nodes);
                            break;
                        case DELETE_FILE:
                            String fileToDelete = (String) req.getData();
                            List<Node> nodesToDeleteFrom = controller.deleteFile(fileToDelete);
                            response.setData(nodesToDeleteFrom);
                            break;
                        default:
                            break;
                    }
                }
                catch (SystemException e) {
                    response.setCode(Code.ERROR);
                    response.setData(e.errorCode);
                }

                oos.writeObject(response);

            } catch (IOException | ClassNotFoundException e) {

                LOGGER.error("error while communicating with client {}:{}", socket.getInetAddress(), socket.getPort(), e);

            }


            LOGGER.info("client disconnected = {}:{}", socket.getInetAddress(), socket.getPort());
        }

    }

    @Override
    public void run() {
        try {
            serverSocket = SServerSocketFactory.createServerSocket(this.port);
            LOGGER.info("client service started on port {}", this.port);
            while(true) {
                new ClientHandler(serverSocket.accept()).start();
            }

        } catch (IOException e) {
            LOGGER.error("error ", e);
        }
    }

}
