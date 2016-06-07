import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import Requests.ConnectAccept;
import Requests.ConnectRequest;

public class ConnectToServer {
	
	private boolean isConnected = false;
	private Socket s;
	private int portNumber;
	
	public ConnectToServer(int _portNumber)
	{
		portNumber = _portNumber;
	}
	
	public boolean isConnected()
	{
		return isConnected;
	}
	
	public Socket connect()
	{
		try {
            s = new Socket("localhost", portNumber);
            
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            
            ConnectRequest conRequest = new ConnectRequest(8); //TODO Przekazanie id klienta?
        	oos.writeObject(conRequest);
        	
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            
            Object obj;
            while(!((obj = ois.readObject()) instanceof ConnectAccept))
            {
            	oos.writeObject(conRequest);
            }
            ConnectAccept conAccept = (ConnectAccept) obj;
            System.out.println("client debug: " + conAccept.getMessage());
                              
            isConnected = true;
            
            return s;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
