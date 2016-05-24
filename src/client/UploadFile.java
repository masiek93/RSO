package client;

import java.io.Serializable;

/* Klasa do komunikacji miedzy Klientem a Serwerem Katalogowym. 
 * Klient wysy�a Obiekt tej klasy z wype�nion� zmienn� originalFileName.
 * originalFileName - musi zawiera� cala sciezke wraz z nazwa pliku, ktory chce wgrac, np. root/folder1/folder2/plik.txt 
 * Serwer zachwouje sob� oryginaln� nazw� pliku i wpisuj� wygenerowan� 
 * automatycznie nazw� pliku do zmiennej generatedFileName oraz wpisuje do zmiennej
 * fileServerPort numer portu Serwera Plikowego, na ktory klient ma wgrac plik.
 */
public class UploadFile implements Serializable {
	private String originalFileName;
	private String generatedFileName;
	private int fileServerPort;
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public String getGeneratedFileName() {
		return generatedFileName;
	}
	public void setGeneratedFileName(String generatedFileName) {
		this.generatedFileName = generatedFileName;
	}
	public int getFileServerPort() {
		return fileServerPort;
	}
	public void setFileServerPort(int fileServerPort) {
		this.fileServerPort = fileServerPort;
	}
	
}
