package clientConnection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Requests.ConnectAccept;
import Requests.ConnectRequest;

/**
 * W¹tek oczekuj¹cy na nadchodz¹ce po³¹czenie - klientów
 * @author Mateusz
 *
 */
public class WaitForClient extends Thread{
	
	protected int portNumber;
	
	public WaitForClient(int _portNumber) 
	{
		portNumber = _portNumber;
	}
	
	public void run() 
	{
		boolean listeningSocket = true;
		ServerSocket ss;
		try {
			ss = new ServerSocket(portNumber);

			while (listeningSocket) {
				Socket s = ss.accept();

				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

				Object obj;
				while (!((obj = ois.readObject()) instanceof ConnectRequest)) {

				}

				ConnectRequest conRequest = (ConnectRequest) obj;
				if (conRequest.connectionSuccesfull()) {
					System.out.println("Connection request: \"" + conRequest.getMessage() + "\" from client: "
							+ conRequest.getClientID());

					ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
					ConnectAccept conAccept = new ConnectAccept();
					oos.writeObject(conAccept);
					
					EchoThread newClient = new EchoThread(s);
					newClient.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


}
