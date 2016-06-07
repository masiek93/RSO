package Requests;
import java.io.Serializable;

/**
 * 
 * @author Mateusz
 * Checking new client request. If new client - allow connection
 */
public class ConnectRequest  implements Serializable{
    /**
	 * Generated
	 */
	private static final long serialVersionUID = 8660445533514453319L;
	private int clientID = 14;
    boolean connection = false;
    private static final String HELLO = "hello_dear_server";


    public int getClientID() {
        return clientID;
    }
    
    public ConnectRequest() {
    	
    }
    
    public ConnectRequest(int _clientID) {
    	clientID = _clientID;
    }
    
    
    /**
     * TODO Sprawdz czy nowy klient wita sie z serwerem
     * @return true jezeli nowy klient
     */
    public boolean connectionSuccesfull(){
    	connection = true;
    	return connection;
    }
    
    public String getMessage()
    {
    	return HELLO;
    }
}
