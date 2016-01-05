/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package aa;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
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
        
        
        
        float Ki = (float) 1.6644976333716384E-4;
        float Vb = (float) 2.609357232864692;
        BigDecimal bdKi = new BigDecimal(Ki);
        bdKi = bdKi.round(new MathContext(4));
        
        BigDecimal bdVb = new BigDecimal(Vb);
        bdVb = bdVb.round(new MathContext(4));
        
        
        System.out.println("########## Ki = " + Ki + " #########");
        System.out.println("########## Vb = " + Vb + " #########");
        JOptionPane.showMessageDialog(null, "Ki = " +bdKi.floatValue()+"\nVb = " + bdVb.floatValue());
        
    }
}

    
