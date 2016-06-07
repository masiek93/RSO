package Requests;

import java.io.Serializable;

/**
 * Usunięcie pliku - komunikacja z klientem
 * @author Mateusz
 *
 */
public class DeleteFileRequest implements Serializable{

	/**
	 * Generated
	 */
	private static final long serialVersionUID = 866653955971449990L;

	private static final String DELETE_FILE = "delete_file";
	
	private String fileName;
	
	public String getMessage()
	{
		return DELETE_FILE;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
}
