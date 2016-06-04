package pl.edu.pw.elka.rso;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

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
				    		fh.uploadFile(FILE_TO_SEND, socket_server,0);
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
					System.out.println("Client: file content: "+mybytearray);
					
				}finally{
					socket_client.close();
				}
			}catch (Exception e) {
				 System.out.println("client: "+e.getMessage() +" "+e.toString()); // TODO: handle exception
			}
		}
		t.stop();
		
		assertEquals("[B@1d56ce6a", mybytearray.toString());
	}

	@Test
	public void testDownloadFile() {
		System.out.println("\n Test: testDownloadFile() \n");
		String new_client_file="downloaded_file.txt";

		Path path = Paths.get(new_client_file);
		try {
			Files.deleteIfExists(path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("File deleting problems for:"+new_client_file);
			e1.printStackTrace();
		}
		
		
		
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
				    		fh.uploadFile(FILE_TO_SEND, socket_server,0);
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
		for (int i=0;i<2;i++){
			try{
				File test_file = new File (FILE_TO_SEND);
				socket_client = new Socket(SERVER, SOCKET_PORT);
				System.out.println("Client: connected : " + socket_client);
				fh.downloadFile(new_client_file, socket_client,(int)(1.05*test_file.length()));
//				1.05 alocate a bit bigger array so the stream can return -1 and everyone is happy
			}catch (Exception e) {
	    		System.out.println("Client socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
	    	}
		}
		t.stop();
		String nxt_ln=null;
		try{
			File tested_file = new File(new_client_file);
			Scanner scanner = new Scanner(tested_file);
			nxt_ln=scanner.nextLine();
			scanner.close();
		}catch (Exception e) {
    		System.out.println("Scanner: "+e.toString());// TODO: handle exception
    	}
		if (nxt_ln.toLowerCase().contains("Lorem ipsum dolor".toLowerCase())  ) 
			assertTrue(true);
		else
			assertTrue(false);
	}

	@Test
	public void testDownloadOfFileChunk() {
		System.out.println("\n Test: testDownloadFile() \n");
		String new_client_file="downloaded_file_2_10.txt";

		Path path = Paths.get(new_client_file);
		try {
			Files.deleteIfExists(path);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("File deleting problems for:"+new_client_file);
			e1.printStackTrace();
		}
		
		
		
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
				    		fh.uploadFile(FILE_TO_SEND, socket_server,2);
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
		for (int i=0;i<2;i++){
			try{
				File test_file = new File (FILE_TO_SEND);
				socket_client = new Socket(SERVER, SOCKET_PORT);
				System.out.println("Client: connected : " + socket_client);
				fh.downloadFile(new_client_file, socket_client,(int)(1.05*test_file.length()));
//				1.05 alocate a bit bigger array so the stream can return -1 and everyone is happy
			}catch (Exception e) {
	    		System.out.println("Client socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
	    	}
		}
		t.stop();
		String nxt_ln=null;
		try{
			File tested_file = new File(new_client_file);
			Scanner scanner = new Scanner(tested_file);
			nxt_ln=scanner.nextLine();
			scanner.close();
		}catch (Exception e) {
    		System.out.println("Scanner: "+e.toString());// TODO: handle exception
    	}
		if (nxt_ln.toLowerCase().contains("elis, malesuada ultricies.".toLowerCase())  ) 
			assertTrue(true);
		else
			assertTrue(false);
	}
}


