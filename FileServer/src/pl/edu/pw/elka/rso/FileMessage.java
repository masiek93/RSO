package pl.edu.pw.elka.rso;

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
// serwer plikow nie zna nazw plikow
//	private String filename;
//	String getFilename(){
//		return filename;
//	}
//	void setFilename(String filename){
//		this.filename=filename;
//	}
}
