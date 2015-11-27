/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.patient.exceptions;

/**
 *
 * @author Cyriac
 */

public class BadMaskStructException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public BadMaskStructException() {
		super ();
	}
	
	public BadMaskStructException(String reason) {
		super (reason);
	}
	
}
