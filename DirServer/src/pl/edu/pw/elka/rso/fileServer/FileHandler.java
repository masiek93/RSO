package pl.edu.pw.elka.rso.fileServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class FileHandler {


	static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);
	final static int FILEBUFFERSIZE = 1024;


	/**
	 * Download file from server.
	 * @param filename
     */
	public void downloadFile(String filename, ObjectInputStream iis, ObjectOutputStream oos) throws IOException {

		try (InputStream in = new FileInputStream(filename)){

				byte[] bytes = new byte[FILEBUFFERSIZE];

				long fileSize = Files.size(Paths.get(filename));

				oos.writeLong(fileSize);

				int bytesRead;
				while ((bytesRead = in.read(bytes)) != -1) {
					oos.write(bytes, 0, bytesRead);
				}

				LOGGER.info("Sending to client " + filename + "(" + fileSize + " bytes)");
		        oos.flush();

		}catch (IOException e) {
			LOGGER.error("error while downloading file", e);
			throw e;
		}

	}

	private String baseName(String filename) {
		int slash = filename.lastIndexOf('/');
		String base = (slash == -1) ? filename : filename.substring(0, slash);
		return base;
	}

	/**
	 * Upload file to server.
	 * @param filename
	 * @param size
     */
	public void uploadFile(String filename, int size, ObjectInputStream iis, ObjectOutputStream oos) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(filename)){


			int bytesRead;
			byte[] bytes = new byte[FILEBUFFERSIZE];

			while ((bytesRead = iis.read(bytes)) != -1) {
				fos.write(bytes, 0, bytesRead);
			}


			LOGGER.info("new file is persisted in {}({})", filename, size);

	    }catch (IOException e) {
			LOGGER.error("erorr while uploading file {} to fileserver: ", filename,  e);
			throw e;
		}
	}

}
