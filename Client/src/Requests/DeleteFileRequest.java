package Requests;

import java.io.Serializable;

/**
 * Usuniêcie pliku - komunikacja z klientem
 * @author Mateusz
 *
 */
public class DeleteFileRequest implements Serializable{

	/**
	 * Generated
	 */
	private static final long serialVersionUID = 866653955971449990L;

	private static final String DELETE_FILE = "delete_file";
	
	private int portNumber;
	
	private String fileName;
	
	public String getMessage()
	{
		return DELETE_FILE;
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
