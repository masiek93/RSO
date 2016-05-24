package client;

import java.io.Serializable;
import java.util.ArrayList;

/* Klasa do komunikacji miedzy Klientem a Serwerem Katalogowym. 
 * Klient wysy�a Obiekt tej klasy z wype�nion� zmienn� baseFolder a 
 * Serwer wype�nia list� files nazwami plik�w znajduj�cych si� w tym folderze. 
 * Nazwy plik�w podaje z rozszerzeniem a nazwy folder�w bez rozszerzenia.
 * 
 */
public class GetFileList implements Serializable {
	private String baseFolder; 
	private ArrayList<String> files = new ArrayList<String>();
	
	public String getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}

	public ArrayList<String> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<String> files) {
		this.files = files;
	}
	
	public void addItemToList(String name){
		this.files.add(name);
	}
	
	public void clearList(){
		this.files.clear();
	}
}
