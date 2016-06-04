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

	public String getMessage()
	{
		return DOWNLOAD_FILE;
	}

}
