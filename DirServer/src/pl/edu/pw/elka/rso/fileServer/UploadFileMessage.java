package pl.edu.pw.elka.rso.fileServer;

public class UploadFileMessage extends FileMessage {


	public UploadFileMessage(String id, long sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
		this.id = id;
	}


	public UploadFileMessage() {
	}

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
