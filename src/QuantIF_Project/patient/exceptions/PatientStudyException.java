/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.patient.exceptions;

/**
 *
 * @author Cyriac
 */
public class PatientStudyException extends Exception {
    
    public PatientStudyException() {
        super();
    }
    
    public PatientStudyException(String reason) {
        super(reason);
    }
}
