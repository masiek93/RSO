package client;

import client.EnterData;

public class Client { 
	private static EnterData enter;
	
	static final String MENU = "M E N U\n" 
			+ "1 - wyswietl liste plikow i folder�w\n"
			+ "2 - dodaj plik\n"
			+ "3 - usun plik\n"
			+ "4 - pobierz plik\n"
			+ "0 - zako�cz program\n";
	
	//private void communicator();
	
	public static void main(String[] args) {
		//new Client();
		// Prosz� wybra� spos�b komunikacji z u�ytkownikiem
		// "CON" - komunikacja z u�ytkownikiem prowadzona w oknie konsoli
		enter = new EnterData("CON");
		// enter = new EnterData("GUI");
		int choice;
		while (true) {
			System.out.println(MENU);
			choice = enter.enterInt("Podaj sw�j wyb�r: ");
			switch (choice) {
			case 1:
				System.out.println("\nPodaj nazw� folderu, kt�rego zawarto�� chcesz wy�wietli�: ");
				break;
			case 2:
				System.out.println("\nPodaj nazw� pliku, kt�ry chcesz wgra�(pami�taj o padaniu ca�ej �cie�ki np. root/folder1/folder2/plik.txt):\n");
				break;
			case 3:
				System.out.println("\ncase 3\n");
				break;
			case 4:
				System.out.println("\ncase 4\n");
				break;
			case 0:
				System.exit(0);
			default:
				System.out.println("Podaj prawid�owy numer!");
				break;
			}
		}
	}
}


