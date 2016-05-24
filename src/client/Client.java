package client;

import client.EnterData;

public class Client {
	private static EnterData enter;
	
	static final String MENU = "M E N U\n" 
			+ "1 - wyswietl liste plikow\n"
			+ "2 - dodaj plik\n"
			+ "3 - usun plik\n"
			+ "4 - pobierz plik\n"
			+ "0 - zakoñcz program\n";
	
	public static void main(String[] args) {
		//new Client();
		// Proszê wybraæ sposób komunikacji z u¿ytkownikiem
		// "CON" - komunikacja z u¿ytkownikiem prowadzona w oknie konsoli
		enter = new EnterData("CON");
		// enter = new EnterData("GUI");
		int choice;
		while (true) {
			System.out.println(MENU);
			choice = enter.enterInt("Podaj swój wybór: ==>> ");
			switch (choice) {
			case 1:
				System.out.println("\ncase 1\n");
				break;
			case 2:
				System.out.println("\ncase 2\n");
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
				System.out.println("Podaj prawid³owy numer!");
				break;
			}
		}
	}
}


