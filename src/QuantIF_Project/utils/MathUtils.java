/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.utils;

import QuantIF_Project.patient.exceptions.BadParametersException;
import ij.measure.CurveFitter;
import java.awt.Color;
import java.util.Arrays;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Cyriac
 */
public class MathUtils {
    
    /**
     *
     * Calcul l'aire sous la courbe de la fonction y = f(x).
     * En utilisant la méthode des trapèzes
     * @param x valeurs en abscisses
     * @param y y=f(x) : valeurs en ordonnées
     * @return l'aire sous la courbe
     * @throws QuantIF_Project.patient.exceptions.BadParametersException
     *      Quand les deux tableaux n'ont pas la même taille
     */
    public static double AreaUnderTheCurve (double[] x, double[] y) throws BadParametersException {
        
        if(x.length != y.length) 
            throw new BadParametersException("Les deux tableaux doivent avoir la même taille!!");
        
        double AUC = 0; //Area Under the Curve
        int N = x.length; //
       
        for (int i = 0; i < N-1; i++) {
            
            AUC += (x[i+1] - x[i]) * (y[i] + y[i+1]) / 2;
            
        }
        
        return AUC;
    }
    
    /**
     * Fit sur une courbe d'entrée artérielle.
     * On fit la courbe sur sa partie décroissante à l'aide d'une exponentielle décroissante,
     *  puis on fait la symétrie de la courbe par rapport à la valeur max pour avoir la partie
     *  croissante
     * @param xArray tableau des abscisses
     * @param yArray tableau des ordonnées
     * @return 
     * @throws QuantIF_Project.patient.exceptions.BadParametersException 
     */
    public static double[] arterialFit(double[] xArray, double[] yArray) throws BadParametersException {
        if(xArray.length != yArray.length) 
            throw new BadParametersException("Les deux tableaux doivent avoir la même taille!!");
        
        double[] result = new double[xArray.length];
        double[][] data = new double[2][xArray.length];
        data[1] = yArray;
        data[0] = xArray;
        double x;
        double y;
        
        //index de la valeur max sur la courbe
        int symetryIndex = getMaxIndex(data[1]);
        
        double[][] datatoFit = {Arrays.copyOfRange(data[0], symetryIndex, data[0].length), Arrays.copyOfRange(data[1], symetryIndex, data[1].length)};
        
        CurveFitter fitter = new CurveFitter(datatoFit[0], datatoFit[1]);
        /*
        double[] initialParameters = new double[2];
        String formula = "y = a*exp(-b*x)";
        int doCustomFit = endFitter.doCustomFit(formula, initialParameters, true);
        System.out.println("Custom fit = " + doCustomFit);
        */
        fitter.doFit(CurveFitter.EXPONENTIAL);
        
        double[][] decayResults = new double[2][datatoFit[0].length];
        decayResults[0] = fitter.getXPoints();
        
        for (int i = 0; i < decayResults[0].length; i++)
            decayResults[1][i] = fitter.f(decayResults[0][i]);
        
        double[][] risingResults = new double[2][symetryIndex+1];
        
        for (int i = 0; i < risingResults[0].length; i++) {
            risingResults[0][i] = data[0][i];
            risingResults[1][i] = fitter.f(2*data[0][symetryIndex] - data[0][i]);
        }
        
        
        
        //On copie les résultats de la partie croissante
        System.arraycopy(risingResults[1], 0, result, 0, risingResults[1].length);
        //On copie les résultats de la partie décroissante
        System.arraycopy(decayResults[1], 1, result, risingResults[1].length, decayResults[1].length-1);
        
        
        System.out.println("########################################");
        System.out.println("Fit de courbe: " +  fitter.getResultString());
        System.out.println("########################################");
        Curve chart = new Curve("Fit courbe","R2 = " + fitter.getRSquared(), "Temps",  "data", data[0], data[1]);
        
        chart.addData(data[0], result, "fin courbe", Color.YELLOW, 4.0f);
        /*
        chart.addData( risingResults[0],  risingResults[1], "début de courbe", Color.GREEN, 2.0f);
        */
        chart.setVisible( true );
        RefineryUtilities.centerFrameOnScreen(chart);
        
        return result;
    }    
    
    /**
     * 
     * @param data Tableau de données
     * @return L'indice du max
     */
    public static int getMaxIndex(double[] data) {
        int maxIndex = 0;
        double max = Double.NEGATIVE_INFINITY;
       
        for (int i = 0; i < data.length; i++) {
            if (max < data[i]) {
                max = data[i];
                maxIndex = i;
            }
        }
        
        return maxIndex;
    }
    
     /**
     * 
     * @param data Tableau de données
     * @return L'indice du min
     */
    public static int getMinIndex(double[] data) {
        int minIndex = 0;
        double min = Double.POSITIVE_INFINITY;
       
        for (int i = 0; i < data.length; i++) {
            if (min > data[i]) {
                min = data[i];
                minIndex = i;
            }
        }
        
        return minIndex;
    }
    
    /**
     * Applique la fonction f(x) = a*exp(b*x) au tableau passé en entrée
     * @param a
     * @param b
     * @param x
     * @return 
     */
    private double[] expFunction(double a, double b, double[] x) {
        double[] y = new double[x.length];

        for (int i = 0 ; i < x.length; i++) {
            y[i] = a * Math.exp(b * x[i]);
        }

        return y;
    }
    
    /**
     * <p>Résoudre un système de 2 équations 2 inconnues :</p> 
     * <p>a<sub>1</sub>x + b<sub>1</sub>x  = c<sub>1</sub> </p>

     * <p>a<sub>2</sub>x + b<sub>2</sub>x  =c<sub>2</sub> </p>
     * 

     * @param a [a<sub>1</sub>, a<sub>2</sub>]
     * @param b [b<sub>1</sub>, b<sub>2</sub>]
     * @param c [c<sub>1</sub>, c<sub>2</sub>]
     * @return 
     */ 
    public static double[] solveEquations(double[] a, double[] b, double[] c) throws BadParametersException {
        if (a.length != 2 || b.length != 2 || c.length != 2)
            throw new BadParametersException("Les tableaux doivent être de taille 2");
        
        double det = a[0]*b[1] - a[1]*b[0];
        double x = Double.NaN;
        double y = Double.NaN;
       
            
        
        if (det != 0) {
            x = (c[0]*b[1] - c[1]*b[0])/det;
            y = (a[0]*c[1] - a[1]*c[0])/det;
        }
        
        double[] result = {x, y};
        
        return result;
    }
   
}
