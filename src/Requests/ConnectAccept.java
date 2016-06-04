package Requests;
import java.io.Serializable;

/**
 * Akcpetacja po³¹czenia z klientem - shake hands
 * @author Mateusz
 *
 */
public class ConnectAccept implements Serializable{

	/**
	 * Generated
	 */
	private static final long serialVersionUID = -7830625038716504495L;
	private static final String ACK = "accept_client";
	
	public String getMessage()
	{
		return ACK;
	}

	
}
