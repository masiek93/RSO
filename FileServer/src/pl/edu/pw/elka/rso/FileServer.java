package pl.edu.pw.elka.rso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;
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
		   Socket socket=null; // used to provide messaging 
		   Socket fileSocket = null; // used to upload/download files 
		   try{
			   try{
				   socket = servsock.accept();
				   System.out.println("Server has accepted connetion"+socket);
				   ois = new ObjectInputStream(socket.getInputStream());
				   oos = new ObjectOutputStream(socket.getOutputStream());
				   
				   object = (Object) ois.readObject();
				   SystemMessage systemMessage= new SystemMessage();
				   if (object instanceof SystemMessage){
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
					   DownloadFileMessage dgm= (DownloadFileMessage) object;
						   FileHandler fh = new FileHandler();
						   fileSocket=fileServsock.accept();
						   fh.uploadFile(fileStoragePath+"/"+dgm.getId(), fileSocket);					   
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
					   // TODO add hash
					   confirmationMessageToDirectoryServer(socketToDirectoryServer,Type.FILE_RECIVED,Status.SUCCESSFUL,ufm.getId(),null);
				   }
			   }finally{
				   if (ois!=null) ois.close();
				   if (oos!=null)oos.close();
				   if (socket!=null) socket.close();
			   }
		   }catch (Exception e){
			   e.printStackTrace();
		   }
	}

	void confirmationMessageToDirectoryServer(Socket socketToDirectoryServer, Type type,Status status, String id,byte[] hash){
		ConfirmationMessage cm = new ConfirmationMessage();
		cm.setStatus(status);
		cm.setType(type);
		cm.setId(id);
		cm.setHash(hash);
		
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
	   		// TODO Auto-generated catch block
//	   		System.out.println("confirmationMessageToDirectoryServer: "+e.toString());
	   		e.printStackTrace();
	   	}catch (NullPointerException e) {
	   		// TODO Auto-generated catch block
//	   		System.out.println("confirmationMessageToDirectoryServer: "+e.toString());
	   		e.printStackTrace();
	   	}
	}
	
	
//	public static String getDigest(InputStream is, MessageDigest md, int byteArraySize)
//			throws NoSuchAlgorithmException, IOException {
//
//		md.reset();
//		byte[] bytes = new byte[byteArraySize];
//		int numBytes;
//		while ((numBytes = is.read(bytes)) != -1) {
//			md.update(bytes, 0, numBytes);
//		}
//		byte[] digest = md.digest();
//		String result = new String(Hex.encodeHex(digest));
//		return result;
//	}
	
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
	void startServer(String fileStoragePath){
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
		fs.startServer(fileStoragePath);
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
