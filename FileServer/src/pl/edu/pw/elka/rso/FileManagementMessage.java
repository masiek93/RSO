package pl.edu.pw.elka.rso;

import java.io.Serializable;

enum FileOperation {LOCK, UNLOCK } 

public class FileManagementMessage extends FileMessage  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3033690712389824972L;
	private String id;
	private FileOperation fileOperation;
	
	public FileOperation getFileOperation() {
		return fileOperation;
	}
	
	public void setFileOperation(FileOperation fileOperation) {
		this.fileOperation = fileOperation;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
