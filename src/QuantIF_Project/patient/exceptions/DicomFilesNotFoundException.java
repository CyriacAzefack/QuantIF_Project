package QuantIF_Project.patient.exceptions;


public class DicomFilesNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DicomFilesNotFoundException() {
		super ();
	}
	
	public DicomFilesNotFoundException(String reason) {
		super (reason);
	}
	
	
}
