package client;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import client.EnterData;

public class Client {

	private static int clientId = 0;
	private static EnterData enter;

	static final String MENU = "M E N U\n"
			+ "1 - wyswietl liste plikow i folderów\n" + "2 - dodaj plik\n"
			+ "3 - usun plik\n" + "4 - pobierz plik\n"
			+ "0 - zakoñcz program\n";

	public Client() {
		clientId++;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public static void main(String[] args) {
		final String HOST = "127.0.0.1"; // localhost
		final int KATALOG_SERVER_PORT = 60010;
		final int FILE_SERVER_PORT = 13267;
		Socket socketForCatalogServer;
		Socket socketForFileServerCommunication;
		Socket socketForFileServerFileTransfer;
		// Socket socketClient;

		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		/* case 4 */
		Object result = null;
		DownloadFileMessage dfm = new DownloadFileMessage();

		Client client = new Client();
		enter = new EnterData("CON");
		int choice;
		Object obj = null;
		String path;

		while (true) {
			System.out.println(MENU);
			choice = enter.enterInt("Podaj swój wybór: ");
			switch (choice) {
			case 1:
				obj = null;
				try {
					// nawiazywanie polaczenia z serwerem katalogowym
					socketForCatalogServer = new Socket(HOST, KATALOG_SERVER_PORT);

					oos = new ObjectOutputStream(socketForCatalogServer.getOutputStream());
					ois = new ObjectInputStream(socketForCatalogServer.getInputStream());

					// komunikacja
					ConnectRequest connectRequest = new ConnectRequest(client.getClientId());
					oos.writeObject(connectRequest);

					while (!((obj = ois.readObject()) instanceof AcceptRequest)) {
						System.out.println("Client: Waiting for connect request acceptation (Catalog Server)...");
					}
					System.out.println("Client: Connected to Catalog Server.");
					
					FileListRequest fileListRequest = new FileListRequest();
					oos.writeObject(fileListRequest);
					// tutaj dodac odbieranie Listy plików, i wyswietlanie jej
					
						
					oos.close();
					ois.close();
					socketForCatalogServer.close();

				} catch (UnknownHostException exc) {
					
				} catch (SocketException exc) {
					
				} catch (IOException exc) {
					
				}
				

				break;
			case 2:
				System.out
						.println("\nPodaj nazwê pliku, który chcesz wgraæ(pamiêtaj o padaniu ca³ej œcie¿ki np. root/folder1/folder2/plik.txt):\n");
				break;
			case 3:
				System.out
						.println("\nPodaj nazwê pliku, który chcesz usun¹æ(pamiêtaj o padaniu ca³ej œcie¿ki np. root/folder1/folder2/plik.txt):\n");
				break;
			case 4:
				System.out
						.println("\nPodaj nazwê pliku, który chcesz pobraæ(pamiêtaj o padaniu ca³ej œcie¿ki np. root/folder1/folder2/plik.txt):\n");
				/* DODAÆ WSTÊPN¥ KOMUNIKACJÊ Z SERWEREM KATALOGOWYM */

				/* pocz¹tek komunikacji z Serwerem Plików */

				dfm.setId("test_file.txt"); // tutaj muszê dodaæ, ¿e parametrem
											// ma byc nazwa zwracana przez
											// Serwer Katalogowy
				// TODO implement partial download
				dfm.setPartOfFile(0);
				for (int i = 0; i < 2; i++) {
					try {
						ois = null;
						oos = null;
						socketFileServerFileTransfer = null;
						is = null;
						fos = null;
						bos = null;
						try {
							socketFileServerCommunication = new Socket(SERVER,
									SOCKET_PORT);
							System.out.println("Connecting...");
							// order of oos and ois is important (should be
							// opposite to the communicator)
							oos = new ObjectOutputStream(
									socketFileServerCommunication
											.getOutputStream());
							ois = new ObjectInputStream(
									socketFileServerCommunication
											.getInputStream());

							oos.writeObject(dfm);
							socketFileServerFileTransfer = new Socket(SERVER,
									FILE_SOCKET_PORT);
							System.out.println("Connecting to "
									+ socketFileServerFileTransfer);

							is = socketFileServerFileTransfer.getInputStream();

							byte[] mybytearray = new byte[10240];
							int current = 0;
							int bytesRead = is.read(mybytearray, 0,
									mybytearray.length);
							current = bytesRead;
							do {
								bytesRead = is.read(mybytearray, current,
										(mybytearray.length - current));
								if (bytesRead >= 0)
									current += bytesRead;
							} while (bytesRead > -1);
							fos = new FileOutputStream("recived_file.txt");
							bos = new BufferedOutputStream(fos);
							bos.write(mybytearray, 0, current);
							bos.flush();

							System.out.println("Client: file content: "
									+ mybytearray);
						} finally {
							ois.close();
							oos.close();
							is.close();
							socketFileServerFileTransfer.close();
							if (fos != null)
								fos.close();
							if (bos != null)
								bos.close();
						}
					} catch (Exception e) {
						System.out.println("Client socket: " + e.getMessage()
								+ " " + e.toString());// TODO: handle exception
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
