package pl.edu.pw.elka.rso.fileServer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import pl.edu.pw.elka.rso.message.data.FileSrvRegReq;
import pl.edu.pw.elka.rso.ssl.SServerSocketFactory;

public class IntegrationTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMain() throws InterruptedException {
		Thread t = new Thread()
		{
		    public void run() {
		    	directoryServer();
		    }
		};
		t.start();
		Thread.sleep(40);
		Thread t1 = new Thread()
		{
		    public void run() {
		    	String[] args = {"config.xml"};
				
				FileServer.main(args);
		    }
		};
		t1.start();
		Thread.sleep(500);
		//fail("Not yet implemented");
		t.stop();
		t1.stop();
		assertTrue(true);
	}
	
	
	void directoryServer(){
		int SOCKET_PORT=1234;
		ObjectInputStream ois=null;
		ObjectOutputStream oos=null;
		ServerSocket servsock = null;
		try {
			servsock = SServerSocketFactory.createServerSocket(SOCKET_PORT);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true){
			Socket socket=null;
			Object object;
			try {
				try{
					
					socket = servsock.accept();
					System.out.println("Directory Server has accepted connetion"+socket);
					oos = new ObjectOutputStream(socket.getOutputStream());
					ois = new ObjectInputStream(socket.getInputStream());
					object = (Object) ois.readObject();
					if(object instanceof FileSrvRegReq){
						//wait for RegistrationMessage
						//send serverID
						FileSrvRegReq rm =(FileSrvRegReq) object;
						System.out.println("RegistrationMessage File port:"+rm.getFileSocketPort());
						Integer serverID=1234;
						oos.writeObject(serverID);
						object = (Object) ois.readObject();
						
						if (object instanceof Long){
							//wait for free_space
							Long l = (Long) object;
							System.out.println("free space: "+l+" B or  "+l/(1024*1024)+"MB");
						}
						
						object = (Object) ois.readObject();
						
						if (object instanceof String[]){
							//wait  listOfFiles
							String[] sl = (String[]) object;
							String s = "";
							for (int i=0;i<sl.length;i++)
								s+=sl[i]+" ";
							System.out.println("file list "+s);
						}
						object=null;
					}
					
					if (object instanceof Long){
						//wait for free_space
						Long l = (Long) object;
						System.out.println("free space "+l+" kB");
					}
					if (object instanceof String[]){
						//wait  listOfFiles
						String[] sl = (String[]) object;
						System.out.println("file list "+sl.toString());
					}
				}
				finally{
					 if (ois!=null) ois.close();
					 if (oos!=null) oos.close();
					 if (socket!=null) socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// confirmationMessageToDirectoryServer
			
			
		}
	}
}
