/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.patient.exceptions;

public class BadParametersException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public BadParametersException() {
		super ();
	}
	
	public BadParametersException(String reason) {
		super (reason);
	}
	
}
