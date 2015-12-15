/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package aa;

import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.utils.Curve;
import QuantIF_Project.utils.MathUtils;
import ij.measure.CurveFitter;
import ij.measure.ResultsTable;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.ui.RefineryUtilities;








public class TestExponentialFit {
    
    /**
     * TEST FIT EXPONENETIEL
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        try {
            BufferedReader br = null;
            String path = "tmp\\aortaResults\\resultsTable";
            ResultsTable rt = ResultsTable.open2(path);
            double[][] data = new double[2][rt.size()];
            data[1] = rt.getColumnAsDoubles(1);
            data[0] = rt.getColumnAsDoubles(5);
            
            MathUtils.arterialFit(data[0], data[1]);
        } catch (BadParametersException ex) {
            Logger.getLogger(TestExponentialFit.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
