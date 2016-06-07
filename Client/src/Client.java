import java.net.Socket;

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
		int portNumber = 60010;
		String choice = null;
		String fileName = null;
		
		if (args.length > 0) {
			if (args[0] != null) {
				portNumber = Integer.parseInt(args[0]);
			}
			// tutaj trzeba ustalic jak podawane sa parametry
			if (args[1] !=null){
				choice = args[1];
				
				if (choice.equalsIgnoreCase("filelist")){
					
				}else if(choice.equalsIgnoreCase("addfile")){
					fileName = args[3];
					//TODO: implementation
				}else if(choice.equalsIgnoreCase("removefile")){
					fileName = args[3];
					//TODO: implementation
				}else if(choice.equalsIgnoreCase("connect")){
					//TODO: implementation
				}
			}
		}

		enter = new EnterData("CON");
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
		}
	}

}
