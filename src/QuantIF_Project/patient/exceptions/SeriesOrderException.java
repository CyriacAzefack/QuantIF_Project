/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
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
