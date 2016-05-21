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

public class FileServer {
	
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
					   RecivedFileMessage rfm = new RecivedFileMessage();
					   rfm.setId(ufm.getId());
					   rfm.setSender_address(socket.getRemoteSocketAddress().toString());
					   MessageDigest md = MessageDigest.getInstance("MD5");
					   InputStream fis_ = Files.newInputStream(Paths.get(path));
					   DigestInputStream dis = new DigestInputStream(fis_, md);
			
//					   rfm.setHash(md.digest());
//					   ObjectOutputStream oos_dirServer = new ObjectOutputStream(socketToDirectoryServer.getOutputStream());
////					   ois_dirServer = new ObjectInputStream(socketToDirectoryServer.getInputStream());
//					   oos_dirServer.writeObject(rfm);
//					   oos_dirServer.close();
					   
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileStoragePath="storage";
		int SOCKET_PORT = 13267;
		int FILE_SOCKET_PORT = 13999;
		ServerSocket servsock = null;
		ServerSocket fileServsock = null;
		Socket socketToDirectoryServer = null;
		try{
			servsock = new ServerSocket(SOCKET_PORT);
			fileServsock = new ServerSocket(FILE_SOCKET_PORT);
		}catch (Exception e){
			   e.printStackTrace();
		}
		FileServer fs= new FileServer();
		while(true){
			fs.communicator(servsock,fileServsock,socketToDirectoryServer,fileStoragePath);
		}

	}

}
