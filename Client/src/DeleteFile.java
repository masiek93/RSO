import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import Requests.AddFileRequest;
import Requests.FileListRequest;

public class DeleteFile {

	protected Socket socket;

	public DeleteFile(Socket _s) {
		socket = _s;
	}

	public void deleteFile() {
		try {

			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			AddFileRequest afRequest = new AddFileRequest();
			
			oos.writeObject(afRequest);
			
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Object obj;
			while (!((obj = ois.readObject()) instanceof AddFileRequest)) {
				
			}
			
			afRequest = (AddFileRequest) obj;
			// Klasa adfileRequest musi zwracac numer portu serwera plikowego do ktorego mam wgrac plik
			
			
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