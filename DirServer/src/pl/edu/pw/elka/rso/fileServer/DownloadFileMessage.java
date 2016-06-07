package pl.edu.pw.elka.rso.fileServer;

public class DownloadFileMessage extends FileMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3045309452233791109L;
	int partOfFile; // 0 - whole file
	
	public int getPartOfFile() {
		return partOfFile;
	}
	public void setPartOfFile(int partOfFile) {
		this.partOfFile = partOfFile;
	}
}
