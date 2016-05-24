package client;

import java.io.Serializable;

/* Klasa do komunikacji miedzy Klientem a Serwerem Katalogowym. 
 * Klient wysy�a Obiekt tej klasy z wype�nion� zmienn� originalFileName.
 * originalFileName - musi zawiera� cala sciezke wraz z nazwa pliku ktory chce usunac, np. root/folder1/folder2/plik.txt 
 */
public class RemoveFile implements Serializable{
	private String originalFileName;

	public String getFileName() {
		return originalFileName;
	}

	public void setFileName(String fileName) {
		this.originalFileName = fileName;
	}
}
