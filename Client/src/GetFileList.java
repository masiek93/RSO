import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import Requests.FileListRequest;

public class GetFileList {

	protected Socket socket;

	public GetFileList(Socket _s) {
		socket = _s;
	}

	public void getList() {
		try {

			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			FileListRequest flRequest = new FileListRequest();
			
			oos.writeObject(flRequest);
			
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Object obj;
			while (!((obj = ois.readObject()) instanceof FileListRequest)) {
				
			}
			
			flRequest = (FileListRequest) obj;
			
			System.out.println("Client debug: File list: ");	
			List<String> fileList = flRequest.getFileList();
			for(String s: fileList)
	            System.out.println(s);
			
			Thread.sleep(2000);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}


	}
}