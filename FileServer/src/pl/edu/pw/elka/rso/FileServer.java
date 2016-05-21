package pl.edu.pw.elka.rso;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
	
	void communicator(ServerSocket servsock, String fileStoragePath){
		   Object object=null;
		   ObjectInputStream ois=null;
		   ObjectOutputStream oos=null;
		   Socket socket=null;
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
			   }finally{
				   ois.close();
				   oos.close();
				   if (socket!=null) socket.close();
			   }
		   }catch (Exception e){
			   e.printStackTrace();
		   }
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
