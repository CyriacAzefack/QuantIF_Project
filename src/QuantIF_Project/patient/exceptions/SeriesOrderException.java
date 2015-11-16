/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuanttIF of the "Centre Henri Becquerel"
 *   * 
 */
package QuantIF_Project.patient.exceptions;

/**
 *
 * @author Cyriac
 */
public class SeriesOrderException extends Exception{
    public SeriesOrderException () {
        super();
    }
    
    public SeriesOrderException (String reason) {
        super(reason);
    }
}
