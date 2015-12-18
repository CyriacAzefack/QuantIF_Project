/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package aa;

import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.utils.MathUtils;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cyriac
 */
public class test2 {
   
   
    /**
    * @param args
    */
    public static void main(String[] args) {
        
        double[] a = {1, 2};
        double[] b = {-3, 3};
        double[] c = {6, 3};
        
        try {
            System.out.println(Arrays.toString(MathUtils.solveEquations(a, b, c)));
        } catch (BadParametersException ex) {
            Logger.getLogger(test2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

    
