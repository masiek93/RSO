
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
	
	private int portNumber;
	
	private String originalFileName;
	
	private String generatedFileName;
	
	public String getMessage()
	{
		return ADD_FILE;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getGeneratedFileName() {
		return generatedFileName;
	}

	public void setGeneratedFileName(String generatedFileName) {
		this.generatedFileName = generatedFileName;
	}
	
	
}
