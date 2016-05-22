/* 
 *  Klasa umo�liwiaj�ca wczytywanie danych z klawiatury
 *  
 *
 *  Autor: Pawe� Rogalinski
 *   Data: 15 pa�dziernik 2009 r.
 */

package client;
 
import java.io.*;
import javax.swing.JOptionPane;

/**
 * Klasa <code> EnterData </code> implementuje proste
 * metody do wczytywania danych z klawiatury
 * 
 * Program demonstruje nast�puj�ce zagadnienia:
 * <ul>
 *  <li> por�wnywanie �a�cuch�w znak�w za pomoc� metody
 *       <code> String.equals </code>,</li>
 *  <li> wy�wietlanie danych r�nych typ�w w oknie konsoli, </li>
 *  <li> czytanie danych r�nych typ�w ze strumienia wejsciowego
 *       w oknie konsoli za pomoc� klasy <code> BufferedReader </code></li>
 *  <li> obs�ug� wyj�tk�w dla operacji wej�cia-wyj�cia</li>
 *  <li> konwersj� obiekt�w typu <code>String</code> na znaki lub liczby
 *       typu <code>char, int, double</code>,
 *  <li> obs�ug� wyj�tk�w przy konwersji danych</li>
 *  <li> wy�wietlanie komunikat�w za pomoc� okna dialogowego
 *       z biblioteki swing: <br> 
 *       <code> JOptionPane.showMessageDialog</code>,</li>
 *  <li> wprowadzanie danych za pomoc� okna dialogowego
 *       z biblioteki swing: <br> 
 *       <code> JOptionPane.showInputDialog</code>,</li>
 * </ul>
 *
 * @author Pawel Rogali�ski
 * @version 15 pa�dziernik 2009
 */
public class EnterData {

     /** Komunikat o b��dnym formacie wprowadzonych danych. */
     private static final String ERROR_MESSAGE =
           "Nieprawidlowe dane!\nSprobuj jeszcze raz.";


	/** pomocniczy obiekt klasy <code> BufferedReader </code> 
	 *  do czytania danych w oknie konsoli .
	 */
	private BufferedReader reader;


	/**
     * Konstruktor tworz�cy obiekt do czytania danych w oknie konsoli.
     */
	public EnterData() {
		reader = new BufferedReader(
                       new InputStreamReader(System.in), 128);
	}

	
	/**
     * Konstruktor tworz�cy obiekt do czytania danych.
     *
     * @param mode - tryb wczytywania. Mo�e przyjmowa� nast�puj�ce warto�ci:
     *  <ul>
     *  <li> GUI - odczyt danych w trybie graficznym za pomoc� okna dialogowego
     *             <code> showInputDialog </code> z klasy <code> JOptionPane</code>,
     *  <li> CON - odczyt danych w trybie w oknie konsoli za pomoc� pomocniczej klasy 
     *             <code> BufferedReader</code>. 
     */
	public EnterData(String mode) {
		if (mode.equals("GUI")) reader = null;
		else if (mode.equals("CON")) reader = new BufferedReader(
                       							new InputStreamReader(System.in), 128);
		else try {
				throw new Exception("Zly parametr dla konstruktora klasy EnterData.");
			} catch(Exception e){
				System.err.println("Zly parametr dla konstruktora klasy EnterData.");
				System.err.println("Musi byc GUI lub CON.");
				e.printStackTrace();
				System.exit(1);
			}
	}
	
	
	/**
	 * Metoda wy�wietla komunikat o b��dzie.
	 * 
	 * @param message tekst komunikatu.
	 */
	public void printErrorMessage(String message){
		if (reader!=null){
			System.out.println(message);
			enterString("Naci�nij ENTER");
		} else {
			JOptionPane.showMessageDialog(null, message);
		}
	}
	

    /**
     * Metoda czyta �a�cuch znak�w.
     *
     * @param prompt tekst zach�ty do wprowadzania danych.
     * @return obiekt reprezentuj�cy wprowadzony ci�g znak�w.
     */
    public String enterString(String prompt) {
    	if (reader!=null)
    	{ System.out.print(prompt);
    	  String s = null;
    	  try{
    	  	s = reader.readLine();
    	  }
    	  catch (IOException e)
    	  	{ System.err.println("Error reading stream System.in \n");
            }
    	  return s;
    	} else
    	{ return JOptionPane.showInputDialog(prompt);
    	}
    }
    
    
     /**
     * Metoda czyta pojedy�czy znak.
     *
     * Metoda faktycznie czyta ca�y �a�cuch znak�w, z kt�rego
     * wybierany jest tylko pierwszy znak.
     * @param prompt tekst zach�ty do wprowadzania danych.
     * @return wprowadzony znak.
     */  
    public char enterChar(String prompt) {
        boolean blad;
        char c = ' ';
        do{
            blad = false;
            try{ 
                c = enterString(prompt).charAt(0);
            } catch(IndexOutOfBoundsException e){
            	printErrorMessage(ERROR_MESSAGE);
                blad = true;
            }
        }while(blad);
        return c;
    }

    
     /**
     * Metoda czyta liczb� ca�kowit�.
     *
     * Metoda faktycznie czyta ca�y �a�cuch znak�w, kt�ry
     * nast�pnie jest kowertowany na liczb� ca�kowit�.
     * @param prompt tekst zach�ty do wprowadzania danych.
     * @return wprowadzona liczba.
     */
    public int enterInt(String prompt) {
        boolean blad;
        int i = 0;
        do{
            blad = false;
            try{ 
                i = Integer.parseInt(enterString(prompt));
            } catch(NumberFormatException e){
            	printErrorMessage(ERROR_MESSAGE);
            	blad = true;
            }
        }while(blad);
        return i;
    }

    
     /**
     * Metoda czyta liczb� rzeczywist�.
     *
     * Metoda faktycznie czyta ca�y �a�cuch znak�w, kt�ry
     * nast�pnie jest kowertowany na liczb� rzeczywist�.
     * @param prompt tekst zach�ty do wprowadzania danych.
     * @return wprowadzona liczba.
     */
    public double enterDouble(String prompt) {
        boolean blad;
        double d = 0;
        do{
            blad = false;
            try{
                d = Double.parseDouble(enterString(prompt));
            } catch(NumberFormatException e){
            	printErrorMessage(ERROR_MESSAGE);
                blad = true;
            }
        }while(blad);
        return d;
    }   
    
}
