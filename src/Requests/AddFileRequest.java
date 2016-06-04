package Requests;

import java.io.Serializable;

/**
 * Dodanie nowego pliku - komunikacja z klientem
 * @author Mateusz
 *
 */
public class AddFileRequest implements Serializable{

	/**
	 * Generated
	 */
	private static final long serialVersionUID = -6508437274615080155L;

	private static final String ADD_FILE = "add_file";
	
	public String getMessage()
	{
		return ADD_FILE;
	}
}
