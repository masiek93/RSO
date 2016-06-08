package pl.edu.pw.elka.rso.fileServer;

public class DownloadFileMessage extends FileMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3045309452233791109L;
	int partOfFile; // 0 - whole file
	long fromBytes = 0;

	public DownloadFileMessage(String id) {
		this.id = id;
	}

	public DownloadFileMessage(int partOfFile) {
		this.partOfFile = partOfFile;
	}

	public int getPartOfFile() {
		return partOfFile;
	}
	public void setPartOfFile(int partOfFile) {
		this.partOfFile = partOfFile;
	}

	public long getFromBytes() {
		return fromBytes;
	}

	public void setFromBytes(long fromBytes) {
		this.fromBytes = fromBytes;
	}
	
}
