package edu.upc.cpl.smeagol.client.exception;

/**
 * Thrown during object creation to indicate that such object is already defined
 * in the Sméagol server.
 * 
 * @author angel
 * 
 */
public class AlreadyExistsException extends SmeagolClientException {

	private static final long serialVersionUID = 242924237091274809L;

	public AlreadyExistsException() {
		super();
	}

	public AlreadyExistsException(String message) {
		super(message);
	}
}
