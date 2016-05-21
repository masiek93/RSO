package pl.edu.pw.elka.rso;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.Assert;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

public class FileServerTest {
	final int SOCKET_PORT = 13267;
	public final static String SERVER = "127.0.0.1";  // localhost
	ServerSocket servsock = null;
	Socket socket_server;
	Socket socket_client;
	 
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetFreeSpace() {
		Thread t = new Thread()
		{
		    public void run() {
		    	server();
		    }
		};
		t.start();

		Object result=null;
		SystemMessage sm= new SystemMessage();
		sm.setOperation(Operation.GET_FREE_SPACE);
		for(int i=0;i<2;i++){
			try{
				ObjectInputStream ois=null;
				ObjectOutputStream oos=null;
				try{
					socket_client = new Socket(SERVER, SOCKET_PORT);
					System.out.println("Connecting...");
//					order of oos and ois is important (should be opposite to the communicator)
					oos = new ObjectOutputStream(socket_client.getOutputStream());
					ois = new ObjectInputStream(socket_client.getInputStream());
				    
					oos.writeObject(sm);
					result=ois.readObject();
				}finally{
					ois.close();
				    oos.close();
				}
			}catch (Exception e) {
		    	System.out.println("Client socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
		    }
		}
		t.stop();
		assertTrue(result instanceof Long);
	}
	
	
	@Test
	public void testFileList() {
		Thread t = new Thread()
		{
		    public void run() {
		    	server();
		    }
		};
		t.start();

		Object result=null;
		SystemMessage sm= new SystemMessage();
		sm.setOperation(Operation.GET_FILE_LIST);
		for(int i=0;i<2;i++){
			try{
				ObjectInputStream ois=null;
				ObjectOutputStream oos=null;
				try{
					socket_client = new Socket(SERVER, SOCKET_PORT);
					System.out.println("Connecting...");
//					order of oos and ois is important (should be opposite to the communicator)
					oos = new ObjectOutputStream(socket_client.getOutputStream());
					ois = new ObjectInputStream(socket_client.getInputStream());
				    
					oos.writeObject(sm);
					result=ois.readObject();
				}finally{
					ois.close();
				    oos.close();
				}
			}catch (Exception e) {
		    	System.out.println("Client socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
		    }
		}
		t.stop();
		System.out.println(((String[])result)[0]);
		assertEquals(null, "test_file.txt",  ((String[])result)[0]);
	}
	
	
	void server(){
		FileServer fs=new FileServer();
		try {
    		servsock = new ServerSocket(SOCKET_PORT);
    		while (true){
    			System.out.println("Server: Waiting...");
		    		fs.communicator(servsock,"storage");	  
    		}
		}catch (Exception e) {
    		System.out.println("server socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
    	}
		
	}
	
	

}
