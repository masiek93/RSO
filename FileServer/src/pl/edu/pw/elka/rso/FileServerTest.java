package pl.edu.pw.elka.rso;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;

import junit.framework.Assert;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

public class FileServerTest {
	final int SOCKET_PORT = 13267;
	final int FILE_SOCKET_PORT = 13999; 
	public final static String SERVER = "127.0.0.1";  // localhost
	ServerSocket servsock = null;
	ServerSocket fileServsock = null;
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
	
	@Test
	public void testDownloadFileFromServer() {
		Thread t = new Thread()
		{
		    public void run() {
		    	server();
		    }
		};
		t.start();

		Object result=null;
		DownloadFileMessage dfm= new DownloadFileMessage();
		dfm.setId("test_file.txt");
//		TODO implement partial download
		dfm.setPartOfFile(0);
		for(int i=0;i<2;i++){
			try{
				ObjectInputStream ois=null;
				ObjectOutputStream oos=null;
				Socket file_socket_client=null;
				InputStream is = null;
				FileOutputStream fos = null;
			    BufferedOutputStream bos = null;
				try{
					socket_client = new Socket(SERVER, SOCKET_PORT);
					System.out.println("Connecting...");
//					order of oos and ois is important (should be opposite to the communicator)
					oos = new ObjectOutputStream(socket_client.getOutputStream());
					ois = new ObjectInputStream(socket_client.getInputStream());
				    
					oos.writeObject(dfm);
					file_socket_client = new Socket(SERVER, FILE_SOCKET_PORT);
					System.out.println("Connecting to "+file_socket_client);
					
					is = file_socket_client.getInputStream();
					
					byte [] mybytearray  = new byte [10240];
					int current = 0;
					int bytesRead = is.read(mybytearray,0,mybytearray.length);
				    current = bytesRead;
					do {
				         bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
				         if(bytesRead >= 0) current += bytesRead;
				    } while(bytesRead > -1);
					fos = new FileOutputStream("recived_file.txt");
				    bos = new BufferedOutputStream(fos);
				    bos.write(mybytearray, 0 , current);
				    bos.flush();
					
					System.out.println("Client: file content: "+mybytearray);
				}finally{
					ois.close();
				    oos.close();
				    is.close();
				    file_socket_client.close();
				    if (fos != null) fos.close();
				    if (bos != null) bos.close();
				}
			}catch (Exception e) {
		    	System.out.println("Client socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
		    }
		}
		t.stop();
		String nxt_ln=null;
		try{
			File tested_file = new File("recived_file.txt");
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
	public void testUploadFileToFileServer()  {
		Thread t = new Thread()
		{
		    public void run() {
		    	server();
		    }
		};
		t.start();
		Object result=null;
		UploadFileMessage ufm= new UploadFileMessage();
		String filename="test_file.txt";//"downloaded_file.txt";
		ufm.setId(filename);
		File f =new File(filename);
		ufm.setSizeInBytes(f.length());
		
		
		
		for(int i=0;i<2;i++){
			try{
				ObjectInputStream ois=null;
				ObjectOutputStream oos=null;
				Socket socket=null;
				try{
					socket_client = new Socket(SERVER, SOCKET_PORT);
					System.out.println("Client: Connecting Messaging");
//					order of oos and ois is important (should be opposite to the communicator)
					oos = new ObjectOutputStream(socket_client.getOutputStream());
					ois = new ObjectInputStream(socket_client.getInputStream());
					oos.writeObject(ufm);
				    
					socket=new Socket(SERVER,FILE_SOCKET_PORT);
					System.out.println("Client: Connecting File socket");
					FileHandler fh =new FileHandler();
					fh.uploadFile(filename,socket);	
//					result=ois.readObject();
				}finally{
					ois.close();
				    oos.close();
				    socket.close();
				}
			}catch (Exception e) {
		    	System.out.println("Client socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
		    }
		}
		try {
			Thread.sleep(800); // time for thread to write from buffer to file
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.stop();

		
	}
	
	@Test
	public void testLockFile()  {
		Thread t = new Thread()
		{
		    public void run() {
				Object result=null;
				FileManagementMessage fmm= new FileManagementMessage();
				fmm.setFileOperation(FileOperation.UNLOCK);
				fmm.setId("ala_ma_kota.txt");
				do{
					try{
						ObjectInputStream ois=null;
						ObjectOutputStream oos=null;
						try{
							socket_client = new Socket(SERVER, SOCKET_PORT);
							System.out.println("Connecting...");
//							order of oos and ois is important (should be opposite to the communicator)
							oos = new ObjectOutputStream(socket_client.getOutputStream());
							ois = new ObjectInputStream(socket_client.getInputStream());
						    
							oos.writeObject(fmm);
							result=ois.readObject();
						}finally{
							ois.close();
						    oos.close();
						}
					}catch (Exception e) {
				    	//System.out.println("Client socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
				    }
				}while(true);
		    }
		};
		t.start();
		
		FileServer fs=new FileServer();
		try {
			Thread.sleep(2);
    		servsock = new ServerSocket(SOCKET_PORT);
    		fileServsock = new ServerSocket(FILE_SOCKET_PORT);
    		Socket socketToDirectoryServer = null;
    		Vector<String> lockedFiles = new Vector<String>();
    		lockedFiles.add("ala_ma_kota.txt");
    		fs.setlockedFile(lockedFiles);
    		System.out.println("Server: Waiting...");
    		fs.communicator(servsock,fileServsock,socketToDirectoryServer,"storage");	  
		}catch (Exception e) {
    		System.out.println("server socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
    	}
		t.stop();
		assertTrue(fs.getlockedFiles().isEmpty());
		
	}
	
	
	void server(){
		FileServer fs=new FileServer();
		try {
			
    		servsock = new ServerSocket(SOCKET_PORT);
    		fileServsock = new ServerSocket(FILE_SOCKET_PORT);
    		Socket socketToDirectoryServer = null;
    		while (true){
    			System.out.println("Server: Waiting...");
		    	fs.communicator(servsock,fileServsock,socketToDirectoryServer,"storage");	  
    		}
		}catch (Exception e) {
    		System.out.println("server socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
    	}
		
	}
	
	

}
