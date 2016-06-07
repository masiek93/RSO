import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import Requests.ConnectAccept;
import Requests.ConnectRequest;
import Requests.FileListRequest;

/**
 * Symulowanie prostego klienta
 * 
 * @author Mateusz
 *
 */
public class Client {

	private static EnterData enter;

	private static boolean isConnected = false;

	private static Socket socket;

	static final String MENU = "M E N U\n" + "1 - wyswietl liste plikow i folderów\n" + "2 - dodaj plik\n"
			+ "3 - usun plik\n" + "4 - po³¹cz z serwerem\n" + "0 - zakoñcz program\n";
	
	public static void main(String[] args) {
		Client client = new Client();
		int portNumber1 = 4321; // For Catalog Server 1
		int portNumber2 = 23; // For Catalog Server 2
		String choice = null;
		String fileName = null;
		GetFileList gfl = null;
		AddFile af = null;
		ConnectToServer cts = null;
		DeleteFile df = null;
		
		/* Trzeba ustalic w jakiej kolejnosci podawane sa parametry
		 * teraz zak³adam ¿e: 
		 * args[0] - komenda np. addfile
		 * args[1] - nazwa pliku
		 */
		
		if (args.length > 0) {
			/*if (args[0] != null) {
				portNumber1 = Integer.parseInt(args[0]);
			}
			*/
			if (args[0] !=null){
				choice = args[0];
				if (choice.equalsIgnoreCase("filelist")){
					// próba po³¹czenia siê z pierwszym serwerem katalogowym
					cts = new ConnectToServer(portNumber1);
					socket = cts.connect();
					isConnected = cts.isConnected();
					if(isConnected){
						gfl = new GetFileList(socket);
						gfl.getList();
					}else{
						// Je¿eli nie udalo sie polaczyc z pierwszym SK to próbujemy polaczyc siê z drugim
						cts.setPortNumber(portNumber2);
						socket = cts.connect();
						isConnected = cts.isConnected();
						if(isConnected){
							gfl = new GetFileList(socket);
							gfl.getList();
						}else{
							System.out.println("Client debug: Nie uda³o siê po³¹czyæ z ¿adnym z serwerów katalogowych");
						}
					}
					
					
				}else if(choice.equalsIgnoreCase("addfile")){
					if (args[1] !=null){
						fileName = args[1];
						// próba po³¹czenia siê z pierwszym serwerem katalogowym
						cts = new ConnectToServer(portNumber1);
						socket = cts.connect();
						isConnected = cts.isConnected();
						if(isConnected){
							af = new AddFile(socket);
						}else{
							// Je¿eli nie udalo sie polaczyc z pierwszym SK to próbujemy polaczyc siê z drugim
							cts.setPortNumber(portNumber2);
							socket = cts.connect();
							isConnected = cts.isConnected();
							if(isConnected){
								af = new AddFile(socket);
							}else{
								System.out.println("Client debug: Nie uda³o siê po³¹czyæ z ¿adnym z serwerów katalogowych");
							}
						}
						
								
					}
					else System.out.println("Gdy u¿ywasz komendy addfile musisz podaæ jako trzeci parametr nazwe pliku.");	
				}else if(choice.equalsIgnoreCase("deletefile")){
					if (args[1] !=null){
						fileName = args[1];
						// próba po³¹czenia siê z pierwszym serwerem katalogowym
						cts = new ConnectToServer(portNumber1);
						socket = cts.connect();
						isConnected = cts.isConnected();
						if(isConnected){
							df = new DeleteFile(socket);
						}else{
							// Je¿eli nie udalo sie polaczyc z pierwszym SK to próbujemy polaczyc siê z drugim
							cts.setPortNumber(portNumber2);
							socket = cts.connect();
							isConnected = cts.isConnected();
							if(isConnected){
								df = new DeleteFile(socket);
							}else{
								System.out.println("Client debug: Nie uda³o siê po³¹czyæ z ¿adnym z serwerów katalogowych");
							}
						}
					}
					else System.out.println("Gdy u¿ywasz komendy deletefile musisz podaæ jako trzeci parametr nazwe pliku.");	
				}
			}
		}

		/*enter = new EnterData("CON");
		int choice;

		while (true) {
			System.out.println(MENU);
			choice = enter.enterInt("Podaj swój wybór: ");
			switch (choice) {
			case 1:
				if (isConnected) {
					GetFileList gfl = new GetFileList(socket);
					gfl.getList();
				} else
					System.out.println("Najpierw po³¹cz siê z serwerem");
				break;
			case 2:
				System.out.println(
						"\nPodaj nazwê pliku, który chcesz wgraæ(pamiêtaj o padaniu ca³ej œcie¿ki np. root/folder1/folder2/plik.txt):\n");
				break;
			case 3:
				System.out.println("\ncase 3\n");
				break;
			case 4:
				ConnectToServer cts = new ConnectToServer(portNumber);
				socket = cts.connect();
				isConnected = cts.isConnected();
				break;
			case 0:
				System.exit(0);
			default:
				System.out.println("Podaj prawid³owy numer!");
				break;
			}
		}*/
	}

}
