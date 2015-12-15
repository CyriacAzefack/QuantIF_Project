/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel de Rouen"
 *   * 
 */
package QuantIF_Project.patient.exceptions;

/**
 *
 * @author Cyriac
 */
public class NonStaticSerieException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public NonStaticSerieException() {
            super ();
    }

    public NonStaticSerieException(String reason) {
            super (reason);
    }
}
