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
import Requests.*;

public class Client {

	private static int clientId = 0;
	private static EnterData enter;

	static final String MENU = "M E N U\n"
			+ "1 - Wyswietl listê plików i folderów\n" + "2 - Dodaj plik\n"
			+ "3 - Usuñ plik\n" + "4 - Pobierz plik\n"
			+ "0 - Zakoñcz program\n";

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
		final int FILE_SERVER_FILE_TRANSFER_PORT = 13267;
		final int FILE_SERVER_COMMUNICATION_PORT = 13267;
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

		Client client = new Client();
		enter = new EnterData("CON");
		int choice;
		Object obj = null;
		String fileName;

		while (true) {
			System.out.println(MENU);
			choice = enter.enterInt("Podaj swój wybór: ");
			switch (choice) {
			case 1:
				/* Wyswietlanie listy plikow i katalogow */
				try {
					obj = null;
					oos = null;
					ois = null;
					socketForCatalogServer = null;
					try {
						socketForCatalogServer = new Socket(HOST,
								KATALOG_SERVER_PORT);

						oos = new ObjectOutputStream(
								socketForCatalogServer.getOutputStream());
						ois = new ObjectInputStream(
								socketForCatalogServer.getInputStream());

						ConnectRequest connectRequest = new ConnectRequest(
								client.getClientId());
						oos.writeObject(connectRequest);

						while (!((obj = ois.readObject()) instanceof ConnectAccept)) {
							System.out
									.println("Client: Waiting for the connect-request acceptation by Catalog Server...");
						}
						System.out
								.println("Client: Connected to Catalog Server.");

						// wysy³anie ¿¹dania o wyœwietlenie listy plików
						FileListRequest fileListRequest = new FileListRequest();
						oos.writeObject(fileListRequest);
						System.out
								.println("Client: FileListRequest sent to Catalog Server");

						// odbieranie i wyswietlanie listy plikow
						while ((obj = ois.readObject()) != null) {
							if (obj instanceof FileListRequest) {
								FileListRequest fileListRequest1 = (FileListRequest) obj;
								System.out.println("Client debug: "
										+ fileListRequest1.getMessage());

								System.out.println("\nLista plików :\n"
										+ fileListRequest1.getPathList());
							} else {
								System.out
										.println("Client debug: Catalog Server sent wrong object");
							}
						}

						oos.close();
						ois.close();
						socketForCatalogServer.close();

					} catch (UnknownHostException exc) {
						// TODO: handle exception
					} catch (SocketException exc) {
						// TODO: handle exception
					} catch (IOException exc) {
						// TODO: handle exception
					}
				} catch (Exception e) {
					System.out.println("Client socket: " + e.getMessage() + " "
							+ e.toString());// TODO: handle exception
				}

				break;
			case 2:
				/* Dodawanie pliku */
				fileName = enter
						.enterString("\nPodaj nazwê pliku, który chcesz wgraæ(pamiêtaj o padaniu ca³ej œcie¿ki wraz z nazw¹ i rozszerzeniem pliku np. /root/folder1/folder2/plik.txt ):\n");
				try {
					obj = null;
					oos = null;
					ois = null;
					socketForCatalogServer = null;
					try {
						// nawiazywanie polaczenia z serwerem katalogowym
						socketForCatalogServer = new Socket(HOST,
								KATALOG_SERVER_PORT);

						oos = new ObjectOutputStream(
								socketForCatalogServer.getOutputStream());
						ois = new ObjectInputStream(
								socketForCatalogServer.getInputStream());

						ConnectRequest connectRequest = new ConnectRequest(
								client.getClientId());
						oos.writeObject(connectRequest);

						while (!((obj = ois.readObject()) instanceof ConnectAccept)) {
							System.out
									.println("Client: Waiting for the connect-request acceptation by Catalog Server...");
						}
						System.out
								.println("Client: Connected to Catalog Server.");

						// wys³anie ¿¹dania o dodanie pliku
						AddFileRequest addFileRequest = new AddFileRequest();
						oos.writeObject(addFileRequest);
						System.out
								.println("Client: AddFileRequest sent to Catalog Server");

						/*
						 * TODO: otrzymywanie danych od SK o SP do polaczenia
						 * siê z nim laczenie sie wgrywanie pliku
						 */
						oos.close();
						ois.close();
						socketForCatalogServer.close();

					} catch (UnknownHostException exc) {
						// TODO: handle exception
					} catch (SocketException exc) {
						// TODO: handle exception
					} catch (IOException exc) {
						// TODO: handle exception
					}
				} catch (Exception e) {
					System.out.println("Client socket: " + e.getMessage() + " "
							+ e.toString());// TODO: handle exception
				}

				break;
			case 3:
				/* Usuwanie pliku */
				fileName = enter
						.enterString("\nPodaj nazwê pliku, który chcesz usun¹æ(pamiêtaj o padaniu ca³ej œcie¿ki wraz z nazw¹ i rozszerzeniem pliku np. /root/folder1/folder2/plik.txt ):\n");
				try {
					obj = null;
					oos = null;
					ois = null;
					socketForCatalogServer = null;
					try {
						// nawiazywanie polaczenia z serwerem katalogowym
						socketForCatalogServer = new Socket(HOST,
								KATALOG_SERVER_PORT);

						oos = new ObjectOutputStream(
								socketForCatalogServer.getOutputStream());
						ois = new ObjectInputStream(
								socketForCatalogServer.getInputStream());

						ConnectRequest connectRequest = new ConnectRequest(
								client.getClientId());
						oos.writeObject(connectRequest);

						while (!((obj = ois.readObject()) instanceof ConnectAccept)) {
							System.out
									.println("Client: Waiting for the connect-request acceptation by Catalog Server...");
						}
						System.out
								.println("Client: Connected to Catalog Server.");

						// wys³anie ¿¹dania o usuniecie pliku
						DeleteFileRequest deleteFileRequest = new DeleteFileRequest();
						oos.writeObject(deleteFileRequest);
						System.out
								.println("Client: DeleteFileRequest sent to Catalog Server");

						oos.close();
						ois.close();
						socketForCatalogServer.close();

					} catch (UnknownHostException exc) {
						// TODO: handle exception
					} catch (SocketException exc) {
						// TODO: handle exception
					} catch (IOException exc) {
						// TODO: handle exception
					}
				} catch (Exception e) {
					System.out.println("Client socket: " + e.getMessage() + " "
							+ e.toString());// TODO: handle exception
				}

				break;
			case 4:
				/* Pobieranie pliku */
				fileName = enter
						.enterString("\nPodaj nazwê pliku, który chcesz pobraæ(pamiêtaj o padaniu nazwy pliku wraz z rozszerzeniem np. plik.txt ):\n");
				try {
					obj = null;
					oos = null;
					ois = null;
					socketForCatalogServer = null;
					try {
						// nawiazywanie polaczenia z serwerem katalogowym
						socketForCatalogServer = new Socket(HOST,
								KATALOG_SERVER_PORT);

						oos = new ObjectOutputStream(
								socketForCatalogServer.getOutputStream());
						ois = new ObjectInputStream(
								socketForCatalogServer.getInputStream());

						ConnectRequest connectRequest = new ConnectRequest(
								client.getClientId());
						oos.writeObject(connectRequest);

						while (!((obj = ois.readObject()) instanceof ConnectAccept)) {
							System.out
									.println("Client: Waiting for the connect-request acceptation by Catalog Server...");
						}
						System.out
								.println("Client: Connected to Catalog Server.");

						// wys³anie ¿¹dania o pobranie pliku
						/*
						 * tutaj powinna byæ od razu wysylana nazwa pliku,
						 * trzeba dodac cos w klasie DownloadFileRequest i od
						 * razu dodac konstruktor parametryczny
						 */
						DownloadFileRequest downloadFileRequest = new DownloadFileRequest();
						oos.writeObject(downloadFileRequest);
						System.out
								.println("Client: DownloadFileRequest sent to Catalog Server");

						/*
						 * TODO: otrzymywanie danych od SK o SP do polaczenia
						 * siê z nim laczenie sie pobieranie pliku
						 */

						oos.close();
						ois.close();
						socketForCatalogServer.close();

					} catch (UnknownHostException exc) {

					} catch (SocketException exc) {

					} catch (IOException exc) {

					}

					/* pocz¹tek komunikacji z Serwerem Plików */
					/*
					 * TODO: 1.Serwer Katalogowy powinien zwrocic nr PORTU Serwera
					 * Plikowego do ktorego Klient ma sie polaczyc; 2.Serwer
					 * Katalogowy powinien zwrocic nazwe z jaka Klient ma
					 * wgrywac plik na Serwer Plikowy;
					 */
					DownloadFileMessage dfm = new DownloadFileMessage();
					dfm.setId("test_file.txt"); // tutaj muszê dodaæ, ¿e
												// parametrem
												// ma byc nazwa zwracana przez
												// Serwer Katalogowy
					// TODO implement partial download
					dfm.setPartOfFile(0);
					for (int i = 0; i < 2; i++) {
						try {
							ois = null;
							oos = null;
							socketForFileServerFileTransfer = null;
							is = null;
							fos = null;
							bos = null;
							try {
								socketForFileServerCommunication = new Socket(
										HOST, FILE_SERVER_COMMUNICATION_PORT);
								System.out.println("Connecting...");
								// order of oos and ois is important (should be
								// opposite to the communicator)
								oos = new ObjectOutputStream(
										socketForFileServerCommunication
												.getOutputStream());
								ois = new ObjectInputStream(
										socketForFileServerCommunication
												.getInputStream());

								oos.writeObject(dfm);
								socketForFileServerFileTransfer = new Socket(
										HOST, FILE_SERVER_FILE_TRANSFER_PORT);
								System.out.println("Connecting to "
										+ socketForFileServerFileTransfer);

								is = socketForFileServerFileTransfer
										.getInputStream();

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
								socketForFileServerFileTransfer.close();
								if (fos != null)
									fos.close();
								if (bos != null)
									bos.close();
							}
						} catch (Exception e) {
							System.out.println("Client socket: "
									+ e.getMessage() + " " + e.toString());
							// TODO: handle exception
						}
					}

				} catch (Exception e) {
					System.out.println("Client socket: " + e.getMessage() + " "
							+ e.toString());// TODO: handle exception
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
