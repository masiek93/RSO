package pl.edu.pw.elka.rso;

public class UploadFileMessage extends FileMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -938424682529556279L;
	private long sizeInBytes;
	public long getSizeInBytes() {
		return sizeInBytes;
	}
	public void setSizeInBytes(long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}
	
}
