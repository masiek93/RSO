package pl.edu.pw.elka.rso.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.fileServer.DeleteFileMessage;
import pl.edu.pw.elka.rso.fileServer.DownloadFileMessage;
import pl.edu.pw.elka.rso.fileServer.FileServer;
import pl.edu.pw.elka.rso.fileServer.UploadFileMessage;
import pl.edu.pw.elka.rso.ssl.SSocketFactory;
import pl.edu.pw.elka.rso.util.Streams;

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

        try (Socket socket = SSocketFactory.createSocket(ipAddress, port);
             OutputStream fos = new FileOutputStream(localPath)) {

            ObjectInputStream iis = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            // try making dirs            
            if(localPath.contains("/")) {
                if (localPath.lastIndexOf("/") == localPath.length() - 1) {
                    localPath = localPath.substring(0, localPath.length() - 1);
                }
                new File(localPath.substring(0, localPath.lastIndexOf("/"))).mkdirs();
            }

            LOGGER.info("trying to download file {} from {}:{}", localPath, ipAddress, port);


            oos.writeObject(new DownloadFileMessage(serverPath));

            // read size;
            long fileSize = iis.readLong();

            long downloadedSize = Streams.copy(iis, fos, fileSize);
            if(downloadedSize != fileSize) {
                LOGGER.error("ATTENTION: DIFFERENCE BETWEEN REAL FILE SIZE & DOWNLOADED SIZE");
            }


            LOGGER.info("saved to {}({}) from {}:{}", localPath, downloadedSize, ipAddress, port);

        } catch (IOException e) {
            LOGGER.error("error while downloading file {} from {}:{}", serverPath, ipAddress, port, e);
            throw e;
        }

    }

    public void uplodaFile(String localPath, String serverPath) throws IOException {

        try (Socket socket = SSocketFactory.createSocket(ipAddress, port);
             InputStream fis = new FileInputStream(localPath)) {


            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            long size = new File(localPath).length();


            LOGGER.info("trying to upload file {}({}) to {}:{}", localPath, size, ipAddress, port);


            oos.writeObject(new UploadFileMessage(serverPath, size));

            long uploadedBytes = Streams.copy(fis, oos, size);

            if(uploadedBytes != size) {
                LOGGER.error("ATTENTION: DIFFERENCE BETWEEN REAL FILE SIZE & DOWNLOADED SIZE");
            }



            LOGGER.info("sucess while uploading file {}({}) to {}:{}", localPath, uploadedBytes, ipAddress, port);

        } catch (IOException e) {
            LOGGER.error("error while uploading file {} to {}:{}", localPath, ipAddress, port, e);
            throw e;
        }

    }

    public void deleteFile(String filePath) throws IOException {
        try (Socket socket = SSocketFactory.createSocket(ipAddress, port)) {

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream iis = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(new DeleteFileMessage(filePath));


            LOGGER.info("deleted message from {}:{}", ipAddress, port);

        } catch (IOException e) {
            LOGGER.error("error while deleting file {}", filePath, e);
            throw e;
        }
    }


    public boolean test()  {
//        try (Socket socket = SSocketFactory.createSocket(ipAddress, port)) {
//           return true;
//        } catch (IOException e) {
//            LOGGER.error("file server {}:{} is not responsing", ipAddress, port);
//            return false;
//        }
        return true;
    }

    public String getAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }
}
