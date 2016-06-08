package pl.edu.pw.elka.rso.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.fileServer.UploadFileMessage;
import pl.edu.pw.elka.rso.manage.node.Node;
import pl.edu.pw.elka.rso.message.Code;
import pl.edu.pw.elka.rso.message.Message;
import pl.edu.pw.elka.rso.message.Type;
import pl.edu.pw.elka.rso.repo.domain.FileDTO;
import pl.edu.pw.elka.rso.ssl.SSocketFactory;
import pl.edu.pw.elka.rso.test.ErrorCode;
import pl.edu.pw.elka.rso.test.SystemException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DirServerClient {

    private String address;
    private int port;


    Logger LOGGER = LoggerFactory.getLogger(DirServerClient.class);


    public DirServerClient(String address, int port) {
        this.address = address;
        this.port = port;
    }


    public Message requestData(Message request) {
        Message ret = new Message();

        try(Socket socket = SSocketFactory.createSocket(address, port)) {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(request);
            ret = (Message) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("error while connecting with directory server at {}:{}", address, port, e);
            ret.setCode(Code.ERROR);
        }
        return ret;
    }

    public FileDTO getFile(String serverPath) throws SystemException {
        LOGGER.info("requesting file {} information from directory server", serverPath);
        Message response = requestData(new Message(Type.GET_FILE, serverPath));
        if(response.getCode() == Code.ERROR) {
            ErrorCode errorCode = (ErrorCode) response.getData();
            LOGGER.error("error while requesting file {} information {}", serverPath, errorCode);

            throw new SystemException(errorCode, String.format("error while requesting file %s information", serverPath));
        }
        LOGGER.info("got file {} information from directory server", serverPath);
        return (FileDTO) response.getData();
    }


    public List<FileDTO> getFileList() throws SystemException{
        LOGGER.info("requesting file list from directory server");
        Message response = requestData(new Message(Type.GET_FILE_LIST));
        if(response.getCode() == Code.ERROR) {
            ErrorCode errorCode = (ErrorCode) response.getData();
            LOGGER.error("error while requesting file list {}", errorCode);
            throw new SystemException(errorCode, "error while requesting file list");

        }
        LOGGER.info("got file list  from directory server");
        return (List<FileDTO>) response.getData();
    }


    public List<Node> deleteFile(String filename) throws SystemException {
        LOGGER.info("requesting nodes to delete file {}", filename);
        Message response = requestData(new Message(Type.DELETE_FILE, filename));
        if(response.getCode() == Code.ERROR) {
            ErrorCode errorCode = (ErrorCode) response.getData();
            LOGGER.error("error while requesting nodes to delete  file {} error = {}", filename, errorCode);
            throw new SystemException(errorCode, String.format("error while requesting nodes to delete file %s", filename));
        }
        LOGGER.info("got file of nodes  to delete file {}", filename);
        return (List<Node>) response.getData();
    }

    public List<Node> addFile(String localPath, String serverPath) throws SystemException, IOException {



        LOGGER.info("requesting nodes to add file {}", localPath);
        Message response = requestData(new Message(Type.ADD_FILE, new UploadFileMessage(serverPath, Files.size(Paths.get(localPath)))));
        if(response.getCode() == Code.ERROR) {
            ErrorCode errorCode = (ErrorCode) response.getData();
            LOGGER.error("error while requesting nodes to add  file {} error = {}", serverPath, errorCode);
            throw new SystemException(errorCode, String.format("error while requesting nodes to add  file %s", serverPath));
        }
        List<Node> nodes = (List<Node>) response.getData();
        LOGGER.info("got file of nodes  to a add {} = {}", serverPath, nodes);
        return nodes;
    }


    public boolean test() {
        try (Socket socket = SSocketFactory.createSocket(address, port)){
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
