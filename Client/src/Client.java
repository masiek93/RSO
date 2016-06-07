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

	static final String MENU = "M E N U\n" + "1 - wyswietl liste plikow i folder�w\n" + "2 - dodaj plik\n"
			+ "3 - usun plik\n" + "4 - po��cz z serwerem\n" + "0 - zako�cz program\n";
	
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
		 * teraz zak�adam �e: 
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
					// pr�ba po��czenia si� z pierwszym serwerem katalogowym
					cts = new ConnectToServer(portNumber1);
					socket = cts.connect();
					isConnected = cts.isConnected();
					if(isConnected){
						gfl = new GetFileList(socket);
						gfl.getList();
					}else{
						// Je�eli nie udalo sie polaczyc z pierwszym SK to pr�bujemy polaczyc si� z drugim
						cts.setPortNumber(portNumber2);
						socket = cts.connect();
						isConnected = cts.isConnected();
						if(isConnected){
							gfl = new GetFileList(socket);
							gfl.getList();
						}else{
							System.out.println("Client debug: Nie uda�o si� po��czy� z �adnym z serwer�w katalogowych");
						}
					}
					
					
				}else if(choice.equalsIgnoreCase("addfile")){
					if (args[1] !=null){
						fileName = args[1];
						// pr�ba po��czenia si� z pierwszym serwerem katalogowym
						cts = new ConnectToServer(portNumber1);
						socket = cts.connect();
						isConnected = cts.isConnected();
						if(isConnected){
							af = new AddFile(socket);
						}else{
							// Je�eli nie udalo sie polaczyc z pierwszym SK to pr�bujemy polaczyc si� z drugim
							cts.setPortNumber(portNumber2);
							socket = cts.connect();
							isConnected = cts.isConnected();
							if(isConnected){
								af = new AddFile(socket);
							}else{
								System.out.println("Client debug: Nie uda�o si� po��czy� z �adnym z serwer�w katalogowych");
							}
						}
						
								
					}
					else System.out.println("Gdy u�ywasz komendy addfile musisz poda� jako trzeci parametr nazwe pliku.");	
				}else if(choice.equalsIgnoreCase("deletefile")){
					if (args[1] !=null){
						fileName = args[1];
						// pr�ba po��czenia si� z pierwszym serwerem katalogowym
						cts = new ConnectToServer(portNumber1);
						socket = cts.connect();
						isConnected = cts.isConnected();
						if(isConnected){
							df = new DeleteFile(socket);
						}else{
							// Je�eli nie udalo sie polaczyc z pierwszym SK to pr�bujemy polaczyc si� z drugim
							cts.setPortNumber(portNumber2);
							socket = cts.connect();
							isConnected = cts.isConnected();
							if(isConnected){
								df = new DeleteFile(socket);
							}else{
								System.out.println("Client debug: Nie uda�o si� po��czy� z �adnym z serwer�w katalogowych");
							}
						}
					}
					else System.out.println("Gdy u�ywasz komendy deletefile musisz poda� jako trzeci parametr nazwe pliku.");	
				}
			}
		}

		/*enter = new EnterData("CON");
		int choice;

		while (true) {
			System.out.println(MENU);
			choice = enter.enterInt("Podaj sw�j wyb�r: ");
			switch (choice) {
			case 1:
				if (isConnected) {
					GetFileList gfl = new GetFileList(socket);
					gfl.getList();
				} else
					System.out.println("Najpierw po��cz si� z serwerem");
				break;
			case 2:
				System.out.println(
						"\nPodaj nazw� pliku, kt�ry chcesz wgra�(pami�taj o padaniu ca�ej �cie�ki np. root/folder1/folder2/plik.txt):\n");
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
				System.out.println("Podaj prawid�owy numer!");
				break;
			}
		}*/
	}

}
