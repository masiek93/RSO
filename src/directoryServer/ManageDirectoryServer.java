package directoryServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * @author Mateusz
 *Akcje serwera katalogowego wywo³ywane s¹ poleceniami nadchodz¹cymi od klienta
 */

public class ManageDirectoryServer 
{
	private static final String SEND_FILE_LIST = "send_file_list";
	private static final String ADD_FILE = "add_file";
	private static final String DELETE_FILE = "delete_file";
	private static final String DOWNLOAD_FILE = "download_file";
	
	private static final int CLIENT_ENTRY_SOCKET = 60010;
	
	public static void main(String[] args) 
	{
		startServer();
	}
	
	private static void startServer() 
	{
        (new Thread() 
        {
            @Override
            public void run() 
            {
                ServerSocket ss;
                try {
                    ss = new ServerSocket(CLIENT_ENTRY_SOCKET);

                    Socket s = ss.accept();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(s.getInputStream()));
                    String line = null;
                    while ((line = in.readLine()) != null) 
                    {
                        System.out.println(line);
                        switch(line)
                        {
                        case SEND_FILE_LIST:
                        	sendFileList(s);
                        	break;
                        case ADD_FILE:
//                        	addFile();
                        	break;
                        case DELETE_FILE:
//                        	deleteFile();
                        	break;
                        case DOWNLOAD_FILE:
//                        	downloadFile();
                        	break;

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

	/**
	 * 
	 * @param s - socket przez który ma zostaæ wys³ana lista plików
	 * Docelowo ma nast¹piæ po³¹czenie z serwerami plikowymi, zebranie list plików i scalenie w jedn¹
	 */
	private static void sendFileList(Socket s)
	{
		SendFileList sfl = new SendFileList();
		
		try {
            s = new Socket("localhost", CLIENT_ENTRY_SOCKET);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());

            out.writeObject(sfl.getFileList());

            Thread.sleep(200);
            

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

	}

}
