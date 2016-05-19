package pl.edu.pw.elka.rso;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class FileHandler {
	public void uploadFile(String filename, Socket socket){
		try{
			try {
				File myFile = new File (filename);
		        byte [] mybytearray  = new byte [(int)myFile.length()];
				OutputStream os = socket.getOutputStream();
				System.out.println("Sending " + filename + "(" + mybytearray.length + " bytes)");
				os.write(mybytearray,0,mybytearray.length);
		        os.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if (socket!=null) socket.close();
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void downloadFile(){
		
	}

}
