package clientConnection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import DirectoryServerActions.SendFileList;
import Requests.AddFileRequest;
import Requests.DeleteFileRequest;
import Requests.DownloadFileRequest;
import Requests.FileListRequest;

/**
 * Nowy w¹tek dla ka¿dego klienta, który po³¹czy³ siê z serwerem katalogowym 
 * Obs³uguje wszystkie ¿¹dania klienta
 * @author Mateusz
 *
 */
public class EchoThread extends Thread{

	protected Socket socket;
	
	public EchoThread(Socket clientSocket) {
		this.socket = clientSocket;
	}
	
	public void run() {

		ObjectInputStream ois;
		System.out.println("Server debug: New thread created");
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			Object obj;

			while ((obj = ois.readObject()) != null) {
				if (obj instanceof FileListRequest) 
				{
					FileListRequest flRequest = (FileListRequest) obj;
					System.out.println("Server debug: " + flRequest.getMessage());
					
					SendFileList listOfFiles = new SendFileList();
					flRequest.setPathList(listOfFiles.getFileList());
					
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					oos.writeObject(flRequest);
				}
				else if (obj instanceof DownloadFileRequest)
				{
					DownloadFileRequest dowfRequest = (DownloadFileRequest) obj;
					System.out.println("Server debug: " + dowfRequest.getMessage());
					// TODO Download file
				}
				else if (obj instanceof DeleteFileRequest)
				{
					DeleteFileRequest delfRequest = (DeleteFileRequest) obj;
					System.out.println("Server debug: " + delfRequest.getMessage());
					// TODO Delete file
				}
				else if (obj instanceof AddFileRequest)
				{
					AddFileRequest addfRequest = (AddFileRequest) obj;
					System.out.println("Server debug: " + addfRequest.getMessage());
					// TODO Add file
				}
				else
				{
					System.out.println("Server debug: Unknown object");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


}
