package pl.edu.pw.elka.rso;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

public class FileHandlerTest {
	final int SOCKET_PORT = 13267;
	final String FILE_TO_SEND = "test_file.txt";
	public final static String SERVER = "127.0.0.1";  // localhost
	ServerSocket servsock = null;
	Socket socket_server;
	Socket socket_client;
	FileHandler fh= new FileHandler();
	@Before
	public void setUp() throws Exception {
//		try {
//			 servsock = new ServerSocket(SOCKET_PORT);
//		} catch (Exception e) {
//			System.out.println("setUp: "+e.getMessage());// TODO: handle exception
//		}

	}

	@Test
	public void testUploadFile() {
		Thread t = new Thread()
		{
		    public void run() {
		    	try {
		    		servsock = new ServerSocket(SOCKET_PORT);
		    		while (true){
		    			System.out.println("Server: Waiting...");
			    		try {
				    		socket_server = servsock.accept();
				    		System.out.println("Server: Accepted connection : " + socket_server);
				    		fh.uploadFile(FILE_TO_SEND, socket_server);
			    		}finally{
			    			if (socket_server!=null) socket_server.close();
			    		}
		    		}
		    	}catch (Exception e) {
		    		System.out.println("server socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
		    	}
		    }
		};
		t.start();
		byte [] mybytearray  = new byte [50];
		for(int i=0;i<2;i++){
			try{
				socket_client = new Socket(SERVER, SOCKET_PORT);
				System.out.println("Connecting...");
				InputStream is;
				try {
					is = socket_client.getInputStream();
					is.read(mybytearray,0,mybytearray.length);
					System.out.println("file content: "+mybytearray);
					
				}finally{
					socket_client.close();
				}
			}catch (Exception e) {
				 System.out.println("client: "+e.getMessage() +" "+e.toString()); // TODO: handle exception
			}
		}
		t.stop();
		
//		fail("Not yet implemented");
		assertEquals("[B@5d099f62", mybytearray.toString());
	}

	@Test
	public void testDownloadFile() {
		fail("Not yet implemented");
	}

}
