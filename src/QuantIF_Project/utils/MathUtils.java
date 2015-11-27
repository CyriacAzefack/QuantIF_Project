/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.utils;

import QuantIF_Project.patient.exceptions.BadParametersException;

/**
 *
 * @author Cyriac
 */
public class MathUtils {
    
    /**
     * Calcul l'aire sous la courbe de la fonction y = f(x).
     * En utilisant la méthode des trapèzes
     * @param x valeurs en abscisses
     * @param y y=f(x) : valeurs en ordonnées
     * @return l'aire sous la courbe
     * @throws QuantIF_Project.patient.exceptions.BadParametersException
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
}
