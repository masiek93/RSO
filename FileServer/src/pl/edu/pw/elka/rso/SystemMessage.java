package pl.edu.pw.elka.rso;

import java.io.Serializable;
 enum Operation{ GET_FILE_LIST, GET_FREE_SPACE}

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
