/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package aa;

import QuantIF_Project.utils.Curve;
import ij.measure.CurveFitter;
import ij.util.ArrayUtil;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
        BufferedReader br = null;
        try {
            //lecture du fichier
            String path = "C:\\Users\\kamelya\\Desktop\\PETDYN_002---0001-0087-0084 Tableau de résultats.xls";
            br = new BufferedReader(new FileReader(path));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            
            double[][] data = new double[2][33];
            double x;
            double y;
            
            //Collect data
           // WeightedObservedPoints obs = new WeightedObservedPoints();
            
            
            int lineIndex = 0;
            while (line != null) {
                lineIndex ++;
                //On ne lit pas la premiere ligne
               
                if (lineIndex > 1) {
                    //On recupere les deux valeurs et on les ajoute
                    String[] split = line.split("	", 2);
                    
                    
                    x = Double.valueOf(split[0]);
                    y = Double.valueOf(split[1]);
                    //obs.add(x, y);
                    data[0][lineIndex - 2] = x;
                    data[1][lineIndex - 2] = y;
                    
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                
                line = br.readLine();
                
            }
            
            //System.out.println(everything);
            for (int i = 0; i < data[0].length; i++)
                System.out.println(data[0][i] + " -- "+ data[1][i]);
            
            
            //Indice de la valeur max de la courbe de départ
            int symetryIndex = getMaxIndex(data[1]);
            
            double[][] datatoFit = {Arrays.copyOfRange(data[0], symetryIndex, data[0].length), Arrays.copyOfRange(data[1], symetryIndex, data[1].length)};
            CurveFitter endFitter = new CurveFitter(datatoFit[0], datatoFit[1]);
            
            endFitter.doFit(CurveFitter.EXPONENTIAL);
            
           // Résultats du fitting de la partie décroissante
            double[][] decayResults = new double[2][datatoFit[0].length];
            decayResults[0] = endFitter.getXPoints();
            for (int i = 0; i < decayResults[0].length; i++)
                decayResults[1][i] = endFitter.f(decayResults[0][i]);
            
            //Résultats de la symétrie du fit de la partie décroissante sur la partie croissante
            double[][] risingResults = new double[2][symetryIndex+1];
            for (int i = 0; i < risingResults[0].length; i++) {
                risingResults[0][i] = data[0][i];
                risingResults[1][i] = endFitter.f(2*data[0][symetryIndex] - data[0][i]);
            }
            
            
            /*
            System.out.println("RISING RESULTS");
            for (int i = 0; i < risingResults[0].length; i++) {
                System.out.println(risingResults[0][i] +"-->"+risingResults[1][i]);
            }
            
            System.out.println("DECAYING RESULTS");
            for (int i = 0; i < decayResults[0].length; i++) {
                System.out.println(decayResults[0][i] +"-->"+decayResults[1][i]);
            }
            */
           
               
            
                
            
            
          
           System.out.println("########################################");
           System.out.println("Fit fin de courbe: " +  endFitter.getResultString());
           System.out.println("########################################");
            
           //"R^2_debut = " + startFitter.getRSquared() + "\n" + "R^2_fin = " + endFitter.getRSquared()
            Curve chart = new Curve("Fit courbe","fit" , "Temps (sec)",  "data", data[0], data[1]);
            chart.addData(decayResults[0], decayResults[1], "fin de courbe", Color.YELLOW, 4.0f);
            chart.addData( risingResults[0],  risingResults[1], "début de courbe", Color.GREEN, 2.0f);
            
            
           
            chart.setVisible( true );
            
            //On place la courbe au centre de l'écran
            RefineryUtilities.centerFrameOnScreen(chart);
            
            
            
            
            
            
            
            
            
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestExponentialFit.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(TestExponentialFit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
    }    
    
    /**
     * 
     * @param data
     * @return l'indice du max
     */
    private static int getMaxIndex(double[] data) {
        int maxIndex = 0;
        double max = 0;
       
        for (int i = 0; i < data.length; i++) {
            if (max < data[i]) {
                max = data[i];
                maxIndex = i;
            }
        }
        
        return maxIndex;
    }
    public double[] expFunction(double a, double b, double[] x) {
        double[] y = new double[x.length];

        for (int i = 0 ; i < x.length; i++) {
            y[i] = a * Math.exp(b * x[i]);
        }

        return y;
    }
    
    

}
