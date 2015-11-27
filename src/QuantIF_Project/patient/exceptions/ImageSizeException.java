/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.patient.exceptions;

/**
 *
 * @author Cyriac
 */
public class ImageSizeException extends Exception {
    
    public ImageSizeException () {
        super();
    }
    
    public ImageSizeException (String reason) {
        super(reason);
    }
}
