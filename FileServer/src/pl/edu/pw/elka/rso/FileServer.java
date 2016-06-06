package pl.edu.pw.elka.rso;

//import pl.edu.pw.elka.rso.config.Config;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBException;

import pl.edu.pw.elka.rso.manage.util.Config;
//import org.apache.commons.codec.binary.Hex;

public class FileServer {
	String directoryServerAddress;
	String RedundantDirectoryServerAddress;
	int directoryServerPort ;
	int RedundantDirectoryServerPort;
	Integer serverID;
	static int SOCKET_PORT = 13267;
	static int FILE_SOCKET_PORT = 13999;
	
	void communicator(ServerSocket servsock,ServerSocket fileServsock,Socket socketToDirectoryServer, String fileStoragePath){
		   Object object=null;
		   ObjectInputStream ois=null;
		   ObjectOutputStream oos=null;
		   ObjectInputStream ois2=null;
		   ObjectOutputStream oos2=null;
		   Socket comunicationSocket=null;
		   Socket socket=null; // used to provide messaging 
		   Socket fileSocket = null; // used to upload/download files 
		   try{
			   try{
				   socket = servsock.accept();
				   System.out.println("Server has accepted connetion"+socket);
				   ois = new ObjectInputStream(socket.getInputStream());
				   oos = new ObjectOutputStream(socket.getOutputStream());
				   
				   object = (Object) ois.readObject();
				  
				   if (object instanceof SystemMessage){
					   SystemMessage systemMessage= new SystemMessage();
					   systemMessage = (SystemMessage) object;
					   if (systemMessage.getOperation().equals( Operation.GET_FREE_SPACE)){
							   File file =new File(fileStoragePath);
					   		   Long free_space=file.getUsableSpace();
					   		   oos.writeObject(free_space);
					   }
					   if (systemMessage.getOperation().equals( Operation.GET_FILE_LIST)){
						   File folder = new File(fileStoragePath);
						   String[] listOfFiles=folder.list();
						   oos.writeObject(listOfFiles);
					   }		   
				   }
				   if (object instanceof DownloadFileMessage){
					   System.out.println("inside DownloadFileMessage");
					   DownloadFileMessage dgm= (DownloadFileMessage) object;
						   FileHandler fh = new FileHandler();
						   fileSocket=fileServsock.accept();
						   fh.uploadFile(fileStoragePath+"/"+dgm.getId(), fileSocket,dgm.getPartOfFile());					   
				   }
				   if (object instanceof DeleteFileMessage){
					   DeleteFileMessage dflm = (DeleteFileMessage) object;
					   String path_str=fileStoragePath+"/"+dflm.getId();
					   Path path=Paths.get(path_str);
					   try {
						    Files.delete(path);
						} catch (NoSuchFileException x) {
						    System.err.format("%s: no such" + " file or directory%n", path);
						} catch (DirectoryNotEmptyException x) {
						    System.err.format("%s not empty%n", path);
						} catch (IOException x) {
						    // File permission problems are caught here.
						    System.err.println(x);
						}
				   }
				   if (object instanceof UploadFileMessage){
					   UploadFileMessage ufm= (UploadFileMessage)object;
					   FileHandler fh = new FileHandler();
					   fileSocket=fileServsock.accept();
					   String path=fileStoragePath+"/"+ufm.getId();
					   
					   fh.downloadFile(path, fileSocket, (int)(1.05*ufm.getSizeInBytes()));
					   // send notification to directory server
					   confirmationMessageToDirectoryServer(socketToDirectoryServer,Type.FILE_RECIVED,Status.SUCCESSFUL,ufm.getId(), getDigest(path),serverID);
				   }
				   if (object instanceof ForwardFileMessage){
					   ForwardFileMessage ffm= (ForwardFileMessage) object;
					   UploadFileMessage ufm = new UploadFileMessage();
					   ufm.setId(ffm.getId());
					   String path=fileStoragePath+"/"+ffm.getId();
					   File file = new File (path);
					   ufm.setSizeInBytes(file.length());
					   comunicationSocket= new Socket(ffm.getDestinationAddress(),ffm.getDestinationPort());
					   
					   oos2 = new ObjectOutputStream(comunicationSocket.getOutputStream());
					   ois2 = new ObjectInputStream(comunicationSocket.getInputStream());
					   
					   oos2.writeObject(ufm);
					  
					   Socket fileSocketToAnotherFS= new Socket(ffm.getDestinationAddress(),ffm.getDestinationFilePort());
					   FileHandler fh =new FileHandler();
					   fh.uploadFile(path, fileSocketToAnotherFS,0);	
					   
				   }
			   }finally{
				   if (ois!=null) ois.close();
				   if (oos!=null)oos.close();
				   if (socket!=null) socket.close();
				   
				   if (ois2!=null) ois2.close();
				   if (oos2!=null) oos2.close();
				   if (comunicationSocket!=null) comunicationSocket.close();
			   }
		   }catch (Exception e){
			   e.printStackTrace();
		   }
	}

	void confirmationMessageToDirectoryServer(Socket socketToDirectoryServer, Type type,Status status, String id,byte[] hash,int serverID){
		ConfirmationMessage cm = new ConfirmationMessage();
		cm.setStatus(status);
		cm.setType(type);
		cm.setId(id);
		cm.setHash(hash);
		cm.setServerID(serverID);
		
		ObjectOutputStream oos_dirServer = null;
		try {
			try{
				oos_dirServer = new ObjectOutputStream(socketToDirectoryServer.getOutputStream());
				oos_dirServer.writeObject(cm);
	//			moze sie zaciac bo nie ma ObjectInputStream
			}finally{
				if (oos_dirServer!=null) oos_dirServer.close();
			}
	   	} catch (IOException e) {
	   		e.printStackTrace();
	   	}catch (NullPointerException e) {
	   		e.printStackTrace();
	   	}
	}
	
	static byte[] getDigest(String filename){
		MessageDigest m = null;
		String string=null;
		try {
			Path p1=Paths.get(filename);
			Charset charset = Charset.forName("utf-8");
			string=Files.readAllLines(p1,charset).toString();
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.reset();
		m.update(string.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1,digest);
		System.out.println( "hashed file "+filename+": "+bigInt.toString(16));
		return digest;
	}
	
	Socket getDirectoryServerSocket(){
		Socket socket=null;
		do{
			try{
				try{
					socket = new Socket(directoryServerAddress, directoryServerPort);
				}catch (Exception e){
					   e.printStackTrace();
					   socket = new Socket(RedundantDirectoryServerAddress, RedundantDirectoryServerPort);
				}
			}catch (Exception e){
				   e.printStackTrace();
				   try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}while(socket==null);
		return socket;
		
	}
	
	void startServer(String fileStoragePath,String[] args) throws IOException, JAXBException{
		
		Config cnf =  Config.load(args[0]);
		
			
		directoryServerAddress = cnf.directoryServerList.get(0).address;
		directoryServerPort = cnf.directoryServerList.get(0).nodesManagementPort;
		
		RedundantDirectoryServerAddress=cnf.directoryServerList.get(1).address;
		RedundantDirectoryServerPort=cnf.directoryServerList.get(1).nodesManagementPort;
			
		//TODO Czytam konfiguracje i zapisuje adres i port  SK i  SKR
		Socket socketToDirectoryServer = getDirectoryServerSocket();
		ObjectInputStream ois=null;
		ObjectOutputStream oos=null;
		System.out.println("Diractory Server has accepted connetion"+socketToDirectoryServer);
		try{
			ois = new ObjectInputStream(socketToDirectoryServer.getInputStream());
			oos = new ObjectOutputStream(socketToDirectoryServer.getOutputStream());
			//Zarejestrowac się u SK  (pobrać ID,wyslac listę portow na ktorych słucham)
			RegistrationMessage rm =new RegistrationMessage();
			rm.setSocket_port(SOCKET_PORT);
			rm.setFile_socket_port(FILE_SOCKET_PORT);
			oos.writeObject(rm);
			serverID = (Integer) ois.readObject();
			//wysłać ilość wolnego miejsca
			File file =new File(fileStoragePath);
	   		Long free_space=file.getUsableSpace();
	   		oos.writeObject(free_space);
	   		//wysłać listę plików
	   		File folder = new File(fileStoragePath);
	   		String[] listOfFiles=folder.list();
	   		oos.writeObject(listOfFiles);
		}catch (Exception e){
			   e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) {
		String fileStoragePath="storage";
		FileServer fs= new FileServer();
		try {
			fs.startServer(fileStoragePath,args);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ServerSocket servsock = null;
		ServerSocket fileServsock = null;
		Socket socketToDirectoryServer = fs.getDirectoryServerSocket();
		try{
			servsock = new ServerSocket(SOCKET_PORT);
			fileServsock = new ServerSocket(FILE_SOCKET_PORT);
		}catch (Exception e){
			   e.printStackTrace();
		}
		
		while(true){
			fs.communicator(servsock,fileServsock,socketToDirectoryServer,fileStoragePath);
		}

	}

}
