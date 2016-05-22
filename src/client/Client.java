package client;

import client.EnterData;

public class Client {
	private static EnterData enter;
	
	static final String MENU = "\n\nM E N U\n" 
			+ "1 - wyswietl liste plikow\n"
			+ "2 - dodaj plik\n"
			+ "3 - usun plik\n"
			+ "4 - pobierz plik\n"
			+ "0 - zakoñcz program";
	
	public static void main(String[] args) {

		// Proszê wybraæ sposób komunikacji z u¿ytkownikiem
		// "CON" - komunikacja z u¿ytkownikiem prowadzona w oknie konsoli
		enter = new EnterData("CON");
		// enter = new EnterData("GUI");
		
		System.out.println(MENU);
		int size = enter.enterInt("Podaj swój wybór:");

		//Client client = new Client();

		while (true) {
			// kasowanie okna konsoli
			// System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

			switch (enter.enterInt(MENU + "\n==>> ")) {
			case 1:
				System.out.println("case 1");
				break;
			case 2:
				System.out.println("case 2");
				break;
			case 3:
				System.out.println("case 3");
				break;
			case 4:
				System.out.println("case 4");
				break;
			case 0:
				System.exit(0);
			default:
				System.out.println("Podaj prawid³owy numer");
			}
		}
	}
}


