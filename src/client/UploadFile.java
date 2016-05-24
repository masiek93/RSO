package client;

import java.io.Serializable;

/* Klasa do komunikacji miedzy Klientem a Serwerem Katalogowym. 
 * Klient wysy³a Obiekt tej klasy z wype³nion¹ zmienn¹ originalFileName.
 * originalFileName - musi zawieraæ cala sciezke wraz z nazwa pliku, ktory chce wgrac, np. root/folder1/folder2/plik.txt 
 * Serwer zachwouje sobê oryginaln¹ nazwê pliku i wpisujê wygenerowan¹ 
 * automatycznie nazwê pliku do zmiennej generatedFileName oraz wpisuje do zmiennej
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
