package Requests;

import java.io.Serializable;

/**
 * Pobierz plik - komunikacja z klientem
 * @author Mateusz
 *
 */
public class DownloadFileRequest implements Serializable{

	/**
	 * Generated
	 */
	private static final long serialVersionUID = 4971039419900079386L;
	
	private static final String DOWNLOAD_FILE = "download_file";
	
	private int portNumber;
	
	private String fileName;

	public String getMessage()
	{
		return DOWNLOAD_FILE;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	

}
