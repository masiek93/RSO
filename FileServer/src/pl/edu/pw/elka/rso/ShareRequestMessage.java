package pl.edu.pw.elka.rso;

import java.io.Serializable;

enum ShareRequestOperation {UPLOAD, UPLOAD_SUCCESSFUL}

public class ShareRequestMessage extends FileMessage  {
	// Wysyłanie kopii do innego servera
	// z serwera katalogowego do serwera plikowego
	/**
	 * 
	 */
	private static final long serialVersionUID = 2524181413980823325L;
	private String serverAdrress;
	ShareRequestOperation shareRequestOperation;
	
	public ShareRequestOperation getShareRequestOperation() {
		return shareRequestOperation;
	}
	public void setShareRequestOperation( ShareRequestOperation shareRequestOperation) {
		this.shareRequestOperation = shareRequestOperation;
	}

	public String getServerAdrress() {
		return serverAdrress;
	}
	public void setServerAdrress(String serverAdrress) {
		this.serverAdrress = serverAdrress;
	}
	
}
