/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package aa;

import java.math.BigDecimal;
import java.math.MathContext;
import javax.swing.JOptionPane;

/**
 *
 * @author Cyriac
 */
public class test2 {
   
   
    /**
    * @param args
    */
    public static void main(String[] args) {
        
        
       JOptionPane.showOptionDialog(null, 
        "Do you like this answer?", 
        "Feedback", 
        JOptionPane.OK_CANCEL_OPTION, 
        JOptionPane.INFORMATION_MESSAGE, 
        null, 
        new String[]{"Yes I do", "No I don't"}, // this is the array
        "default");
        
    }
}

    
