package pl.edu.pw.elka.rso.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.fileServer.DeleteFileMessage;
import pl.edu.pw.elka.rso.fileServer.DownloadFileMessage;
import pl.edu.pw.elka.rso.fileServer.FileServer;
import pl.edu.pw.elka.rso.fileServer.UploadFileMessage;
import pl.edu.pw.elka.rso.ssl.SSocketFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileServerClient {

    private final String ipAddress;
    int port;

    Logger LOGGER = LoggerFactory.getLogger(FileServerClient.class);

    public FileServerClient(String ipAddress, int port) {
        this.port = port;
        this.ipAddress = ipAddress;
    }




    public void downloadFile(String serverPath, String localPath) throws IOException {

        try (Socket socket = SSocketFactory.createSocket(ipAddress, port)) {

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream iis = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(new DownloadFileMessage(serverPath));

            byte[] content = (byte[]) iis.readObject();
                // try making dirs

            LOGGER.info("downloaded successfully. Trying to save to file.");

            if(localPath.contains("/")) {
                if (localPath.lastIndexOf("/") == localPath.length() - 1) {
                    localPath = localPath.substring(0, localPath.length() - 1);
                }
                new File(localPath.substring(0, localPath.lastIndexOf("/"))).mkdirs();
            }

            Files.write(Paths.get(localPath), content, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            LOGGER.info("saved to {}", localPath);

        } catch (IOException e) {
            LOGGER.error("error while downloading file {}", serverPath, e);
            throw e;
        } catch (ClassNotFoundException e) {
            LOGGER.error("error while downloading file {}", serverPath, e);
            throw new IOException(e.getMessage());
        }

    }

    public void uplodaFile(String localPath, String serverPath) throws IOException {

        try (Socket socket = SSocketFactory.createSocket(ipAddress, port)) {

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());


            byte[] ret = Files.readAllBytes(Paths.get(localPath));
            oos.writeObject(new UploadFileMessage(serverPath, ret.length));
            oos.writeObject(ret);

            LOGGER.info("sucess while uploading file {}", localPath);

        } catch (IOException e) {
            LOGGER.error("error while uploading file {}", localPath, e);
            throw e;
        }

    }

    public void deleteFile(String filePath) throws IOException {
        try (Socket socket = SSocketFactory.createSocket(ipAddress, port)) {

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream iis = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(new DeleteFileMessage(filePath));

        } catch (IOException e) {
            LOGGER.error("error while deleting file {}", filePath, e);
            throw e;
        }
    }


    public boolean test()  {
        try (Socket socket = SSocketFactory.createSocket(ipAddress, port)) {
           return true;
        } catch (IOException e) {
            LOGGER.error("file server {}:{} is not responsing", ipAddress, port);
            return false;
        }

    }

    public String getAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }
}
