/* 
 *  Klasa umożliwiająca wczytywanie danych z klawiatury 
*/


 
import java.io.*;
import javax.swing.JOptionPane;

public class EnterData {

     private static final String ERROR_MESSAGE =
           "Nieprawidlowe dane!\nSprobuj jeszcze raz.";
     
	private BufferedReader reader;

	public EnterData() {
		reader = new BufferedReader(
                       new InputStreamReader(System.in), 128);
	}

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
	
	public void printErrorMessage(String message){
		if (reader!=null){
			System.out.println(message);
			enterString("Naciśnij ENTER");
		} else {
			JOptionPane.showMessageDialog(null, message);
		}
	}
	
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
