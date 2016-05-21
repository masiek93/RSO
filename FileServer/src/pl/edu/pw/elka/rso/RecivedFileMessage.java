package pl.edu.pw.elka.rso;

public class RecivedFileMessage extends FileMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5059039457611624572L;
	private String sender_address;
	private byte[] hash;
	public String getSender_address() {
		return sender_address;
	}
	public void setSender_address(String sender_address) {
		this.sender_address = sender_address;
	}
	public byte[] getHash() {
		return hash;
	}
	public void setHash(byte[] hash) {
		this.hash = hash;
	}
	
}
