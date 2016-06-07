package pl.edu.pw.elka.rso;

enum Type{ FILE_RECIVED,FILE_DELETED }
enum Status {SUCCESSFUL, UNSUCCESSFUL }

public class ConfirmationMessage extends FileMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1547900496401457425L;
	private Type type;
	private Status status;
	private byte[] hash; //used by FILE_RECIVED
	private long serverID;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public long getServerID() {
		return serverID;
	}

	public void setServerID(long serverID) {
		this.serverID = serverID;
	}

}
