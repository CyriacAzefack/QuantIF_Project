package Quantif_project.exceptions;

public class NotDirectoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NotDirectoryException() {
		super ();
	}
	
	public NotDirectoryException(String reason) {
		super (reason);
	}
	
	
}
