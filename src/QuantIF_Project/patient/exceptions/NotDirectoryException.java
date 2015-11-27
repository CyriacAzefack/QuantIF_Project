/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.patient.exceptions;

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
