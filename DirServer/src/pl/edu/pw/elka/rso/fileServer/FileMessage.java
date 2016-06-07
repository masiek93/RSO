package pl.edu.pw.elka.rso.fileServer;

import java.io.Serializable;

public abstract class FileMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
