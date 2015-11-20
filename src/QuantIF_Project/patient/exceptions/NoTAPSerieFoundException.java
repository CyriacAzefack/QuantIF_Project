/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuanttIF of the "Centre Henri Becquerel"
 *   * 
 */
package QuantIF_Project.patient.exceptions;

/**
 *
 * @author Cyriac
 */
public class NoTAPSerieFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoTAPSerieFoundException() {
		super ();
	}
	
	public NoTAPSerieFoundException(String reason) {
		super (reason);
	}
    
}
