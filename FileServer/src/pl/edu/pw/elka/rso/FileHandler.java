package pl.edu.pw.elka.rso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileHandler {
	public void uploadFile(String filename, Socket socket){
		
		try{
			OutputStream os = null;
			FileInputStream fis = null;
		    BufferedInputStream bis = null;
			try {
				File myFile = new File (filename);
		        byte [] mybytearray  = new byte [(int)myFile.length()];
				os = socket.getOutputStream();
				fis = new FileInputStream(myFile);
		        bis = new BufferedInputStream(fis);
		        bis.read(mybytearray,0,mybytearray.length);
				System.out.println("Sending " + filename + "(" + mybytearray.length + " bytes)");
				os.write(mybytearray,0,mybytearray.length);
		        os.flush();
			}finally{
				if (bis != null) bis.close();
				if (socket!=null) socket.close();
				if (os != null) os.close();
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void downloadFile(String filename, Socket socket, int size){
//		otworz strumien i czeka, jak pojawi się plik to pobieraj dopoki nie null
		FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
	    int current = 0;
	    try{
	    	try {
		      // receive file
		      byte [] mybytearray  = new byte [size];
		      InputStream is = socket.getInputStream();
		      fos = new FileOutputStream(filename);
		      bos = new BufferedOutputStream(fos);
		      int bytesRead = is.read(mybytearray,0,mybytearray.length);
		      current = bytesRead;

		      do {
		         bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
		         if(bytesRead >= 0) current += bytesRead;
		      } while(bytesRead > -1);

		      bos.write(mybytearray, 0 , current);
		      bos.flush();
		      System.out.println("File " + filename
		          + " downloaded (" + current + " bytes read)");
		    }
		    finally {
		      if (fos != null) fos.close();
		      if (bos != null) bos.close();
		      if (socket != null) socket.close();
		    }
	    }catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
