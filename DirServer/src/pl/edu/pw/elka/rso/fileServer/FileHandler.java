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


	/**
	 * Download file from server.
	 * @param filename
     */
	public void downloadFile(String filename, ObjectInputStream iis, ObjectOutputStream oos) throws IOException {

		try{
				byte[] fileContent = Files.readAllBytes(Paths.get(filename));

				oos.writeObject(fileContent);

				LOGGER.info("Sending to client " + filename + "(" + fileContent.length + " bytes)");
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
		try{

			//read from input stream
			byte[] content = (byte[]) iis.readObject();

			// mkdirs if there is not any
			new File(baseName(filename)).mkdirs();
			// delete previous files
			Files.deleteIfExists(Paths.get(filename));

			Files.write(Paths.get(filename), content, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			LOGGER.info("new file is persisted in {}({})", filename, size);

	    }catch (IOException e) {
			LOGGER.error("erorr while uploading file {} to fileserver: ", filename,  e);
			throw e;
		} catch (ClassNotFoundException e) {
			LOGGER.error("erorr while uploading file {} to fileserver: ", filename,  e);
			throw new IOException(e.getMessage());
		}
	}

}
