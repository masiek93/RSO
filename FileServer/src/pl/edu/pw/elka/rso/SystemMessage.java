package pl.edu.pw.elka.rso;

import java.io.Serializable;
 


public class SystemMessage implements Serializable {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3811970315589982748L;
	private Operation operation;

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	

}
