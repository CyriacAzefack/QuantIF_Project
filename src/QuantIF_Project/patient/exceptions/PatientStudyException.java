/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuanttIF of the "Centre Henri Becquerel"
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
