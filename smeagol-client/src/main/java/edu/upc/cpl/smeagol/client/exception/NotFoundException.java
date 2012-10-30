package edu.upc.cpl.smeagol.client.exception;

/**
 * Thrown during object manipulation, to indicate that such object is not
 * defined in the Sméagol Server.
 * 
 * @author angel
 * 
 */
public class NotFoundException extends SmeagolClientException {

	private static final long serialVersionUID = -4590153559415781644L;

	public NotFoundException() {
		super();
	}

	public NotFoundException(String message) {
		super(message);
	}

}
