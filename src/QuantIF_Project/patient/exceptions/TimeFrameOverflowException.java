/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.patient.exceptions;

/**
 *
 * @author Cyriac
 */
public class TimeFrameOverflowException extends Exception{
    
    public TimeFrameOverflowException() {
        super();
    }
    
    public TimeFrameOverflowException(String reason) {
        super(reason);
    }
    
}
