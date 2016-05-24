package client;

import client.EnterData;

public class Client { 
	private static EnterData enter;
	
	static final String MENU = "M E N U\n" 
			+ "1 - wyswietl liste plikow i folderów\n"
			+ "2 - dodaj plik\n"
			+ "3 - usun plik\n"
			+ "4 - pobierz plik\n"
			+ "0 - zakoñcz program\n";
	
	//private void communicator();
	
	public static void main(String[] args) {
		public final static String SERVER = "127.0.0.1";  // localhost
		
		enter = new EnterData("CON");
		int choice;
		
		while (true) {
			System.out.println(MENU);
			choice = enter.enterInt("Podaj swój wybór: ");
			switch (choice) {
			case 1:
				System.out.println("\nPodaj nazwê folderu, którego zawartoœæ chcesz wyœwietliæ: ");
				break;
			case 2:
				System.out.println("\nPodaj nazwê pliku, który chcesz wgraæ(pamiêtaj o padaniu ca³ej œcie¿ki np. root/folder1/folder2/plik.txt):\n");
				break;
			case 3:
				System.out.println("\nPodaj nazwê pliku, który chcesz usun¹æ(pamiêtaj o padaniu ca³ej œcie¿ki np. root/folder1/folder2/plik.txt):\n");
				break;
			case 4:
				System.out.println("\nPodaj nazwê pliku, który chcesz pobraæ(pamiêtaj o padaniu ca³ej œcie¿ki np. root/folder1/folder2/plik.txt):\n");
				/*DODAÆ WSTÊPN¥ KOMUNIKACJÊ Z SERWEREM KATALOGOWYM*/
				
				/*pocz¹tek komunikacji z Serwerem Plików*/
				Object result=null;
				DownloadFileMessage dfm= new DownloadFileMessage();
				dfm.setId("test_file.txt");
//				TODO implement partial download
				dfm.setPartOfFile(0);
				for(int i=0;i<2;i++){
					try{
						ObjectInputStream ois=null;
						ObjectOutputStream oos=null;
						Socket file_socket_client=null;
						InputStream is = null;
						FileOutputStream fos = null;
					    BufferedOutputStream bos = null;
						try{
							socket_client = new Socket(SERVER, SOCKET_PORT);
							System.out.println("Connecting...");
//							order of oos and ois is important (should be opposite to the communicator)
							oos = new ObjectOutputStream(socket_client.getOutputStream());
							ois = new ObjectInputStream(socket_client.getInputStream());
						    
							oos.writeObject(dfm);
							file_socket_client = new Socket(SERVER, FILE_SOCKET_PORT);
							System.out.println("Connecting to "+file_socket_client);
							
							is = file_socket_client.getInputStream();
							
							byte [] mybytearray  = new byte [10240];
							int current = 0;
							int bytesRead = is.read(mybytearray,0,mybytearray.length);
						    current = bytesRead;
							do {
						         bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
						         if(bytesRead >= 0) current += bytesRead;
						    } while(bytesRead > -1);
							fos = new FileOutputStream("recived_file.txt");
						    bos = new BufferedOutputStream(fos);
						    bos.write(mybytearray, 0 , current);
						    bos.flush();
							
							System.out.println("Client: file content: "+mybytearray);
						}finally{
							ois.close();
						    oos.close();
						    is.close();
						    file_socket_client.close();
						    if (fos != null) fos.close();
						    if (bos != null) bos.close();
						}
					}catch (Exception e) {
				    	System.out.println("Client socket: "+e.getMessage()+" "+e.toString());// TODO: handle exception
				    }
				}
				break;
			case 0:
				System.out.println("\nKONIEC\n");
				System.exit(0);
			default:
				System.out.println("Podaj prawid³owy numer!");
				break;
			}
		}
	}
}


