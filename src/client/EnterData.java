/* 
 *  Klasa umo¿liwiaj¹ca wczytywanie danych z klawiatury
 *  
 *
 *  Autor: Pawe³ Rogalinski
 *   Data: 15 paŸdziernik 2009 r.
 */

package client;
 
import java.io.*;
import javax.swing.JOptionPane;

/**
 * Klasa <code> EnterData </code> implementuje proste
 * metody do wczytywania danych z klawiatury
 * 
 * Program demonstruje nastêpuj¹ce zagadnienia:
 * <ul>
 *  <li> porównywanie ³añcuchów znaków za pomoc¹ metody
 *       <code> String.equals </code>,</li>
 *  <li> wyœwietlanie danych ró¿nych typów w oknie konsoli, </li>
 *  <li> czytanie danych ró¿nych typów ze strumienia wejsciowego
 *       w oknie konsoli za pomoc¹ klasy <code> BufferedReader </code></li>
 *  <li> obs³ugê wyj¹tków dla operacji wejœcia-wyjœcia</li>
 *  <li> konwersjê obiektów typu <code>String</code> na znaki lub liczby
 *       typu <code>char, int, double</code>,
 *  <li> obs³ugê wyj¹tków przy konwersji danych</li>
 *  <li> wyœwietlanie komunikatów za pomoc¹ okna dialogowego
 *       z biblioteki swing: <br> 
 *       <code> JOptionPane.showMessageDialog</code>,</li>
 *  <li> wprowadzanie danych za pomoc¹ okna dialogowego
 *       z biblioteki swing: <br> 
 *       <code> JOptionPane.showInputDialog</code>,</li>
 * </ul>
 *
 * @author Pawel Rogaliñski
 * @version 15 paŸdziernik 2009
 */
public class EnterData {

     /** Komunikat o b³êdnym formacie wprowadzonych danych. */
     private static final String ERROR_MESSAGE =
           "Nieprawidlowe dane!\nSprobuj jeszcze raz.";


	/** pomocniczy obiekt klasy <code> BufferedReader </code> 
	 *  do czytania danych w oknie konsoli .
	 */
	private BufferedReader reader;


	/**
     * Konstruktor tworz¹cy obiekt do czytania danych w oknie konsoli.
     */
	public EnterData() {
		reader = new BufferedReader(
                       new InputStreamReader(System.in), 128);
	}

	
	/**
     * Konstruktor tworz¹cy obiekt do czytania danych.
     *
     * @param mode - tryb wczytywania. Mo¿e przyjmowaæ nastêpuj¹ce wartoœci:
     *  <ul>
     *  <li> GUI - odczyt danych w trybie graficznym za pomoc¹ okna dialogowego
     *             <code> showInputDialog </code> z klasy <code> JOptionPane</code>,
     *  <li> CON - odczyt danych w trybie w oknie konsoli za pomoc¹ pomocniczej klasy 
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
	 * Metoda wyœwietla komunikat o b³êdzie.
	 * 
	 * @param message tekst komunikatu.
	 */
	public void printErrorMessage(String message){
		if (reader!=null){
			System.out.println(message);
			enterString("Naciœnij ENTER");
		} else {
			JOptionPane.showMessageDialog(null, message);
		}
	}
	

    /**
     * Metoda czyta ³añcuch znaków.
     *
     * @param prompt tekst zachêty do wprowadzania danych.
     * @return obiekt reprezentuj¹cy wprowadzony ci¹g znaków.
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
     * Metoda czyta pojedyñczy znak.
     *
     * Metoda faktycznie czyta ca³y ³añcuch znaków, z którego
     * wybierany jest tylko pierwszy znak.
     * @param prompt tekst zachêty do wprowadzania danych.
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
     * Metoda czyta liczbê ca³kowit¹.
     *
     * Metoda faktycznie czyta ca³y ³añcuch znaków, który
     * nastêpnie jest kowertowany na liczbê ca³kowit¹.
     * @param prompt tekst zachêty do wprowadzania danych.
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
     * Metoda czyta liczbê rzeczywist¹.
     *
     * Metoda faktycznie czyta ca³y ³añcuch znaków, który
     * nastêpnie jest kowertowany na liczbê rzeczywist¹.
     * @param prompt tekst zachêty do wprowadzania danych.
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
