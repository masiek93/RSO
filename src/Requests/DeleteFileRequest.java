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
	
	public String getMessage()
	{
		return DELETE_FILE;
	}

}
