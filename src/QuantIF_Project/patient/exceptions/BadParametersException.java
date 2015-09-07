package Quantif_project.exceptions;

public class BadParametersException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public BadParametersException() {
		super ();
	}
	
	public BadParametersException(String reason) {
		super (reason);
	}
	
}