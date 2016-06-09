package pl.edu.pw.elka.rso.fileServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pw.elka.rso.util.Streams;

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


		try (InputStream fin = new FileInputStream(filename)){


					long fileSize = new File(filename).length();

					LOGGER.info("client is trying to download file {}({})", filename, fileSize);

					oos.writeLong(fileSize);

					long downloadSize = Streams.copy(fin, oos, fileSize);


					if(downloadSize != fileSize) {
						LOGGER.error("ATTENTION: DIFFERENCE BETWEEN REAL FILE SIZE & DOWNLOADED SIZE");
					}


					LOGGER.info("Sending to client {}({})", filename, downloadSize);

		}catch (IOException e) {
			LOGGER.error("error while sending client file {}", filename, e);
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

		File f = new File(filename);

		if(!f.isDirectory()) {
			File parent = f.getParentFile();
			parent.mkdirs();
		}

		try (FileOutputStream fos = new FileOutputStream(filename)){

			LOGGER.info("client is trying to upload file {}({})", filename, size);



			long uploadedSize = Streams.copy(iis, fos, size);

			if(uploadedSize != size) {
				LOGGER.error("ATTENTION: DIFFERENCE BETWEEN REAL FILE SIZE & DOWNLOADED SIZE");
			}



			LOGGER.info("new file is persisted in {}({})", filename, uploadedSize);

	    }catch (IOException e) {
			LOGGER.error("erorr while uploading file {} to fileserver: ", filename,  e);
			throw e;
		}
	}

}
