/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.utils;

import QuantIF_Project.patient.exceptions.BadParametersException;
import ij.measure.CurveFitter;
import java.awt.Color;
import java.util.Arrays;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionPenaltyAdapter;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
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
     *  croissante. On garde le résultat du  
     * @param xArray tableau des abscisses
     * @param yArray tableau des ordonnées
     * @param newXArray Le résultat sera l'image de ce tableau d'abscisses
     * @return result résultat fit partie croissante + partie décroissante de départ
     * @throws QuantIF_Project.patient.exceptions.BadParametersException 
     */
    public static double[] arterialFit(double[] xArray, double[] yArray, double[] newXArray) throws BadParametersException {
        if(xArray.length != yArray.length) 
            throw new BadParametersException("Les deux tableaux doivent avoir la même taille!!");
        
        
        double[][] data = new double[2][xArray.length];
        data[1] = yArray;
        data[0] = xArray;
        
        
        //index de la valeur max sur la courbe
        int symetryIndex = getMaxIndex(data[1]);
        
        //dataToFit représente la partie sur laquelle on vas faire le fit : ici c'est la partie décroissante de la courbe (Après le max)
        double[][] datatoFit = {Arrays.copyOfRange(data[0], symetryIndex, data[0].length), Arrays.copyOfRange(data[1], symetryIndex, data[1].length)};
        
        CurveFitter fitter = new CurveFitter(datatoFit[0], datatoFit[1]);
        
        fitter.doFit(CurveFitter.EXPONENTIAL);
        
        double[][] decayResults = new double[2][datatoFit[0].length];
        
        //decayResults réprésente le résultat du fit pour la partie décroissante
        decayResults[0] = fitter.getXPoints();
        
        for (int i = 0; i < decayResults[0].length; i++)
            decayResults[1][i] = fitter.f(decayResults[0][i]);
        
        //risingResults répresente le résultat de la symétrie de fit pour la partie croissante
        double[][] risingResults = new double[2][symetryIndex+1];
        
        for (int i = 0; i < risingResults[0].length; i++) {
            risingResults[0][i] = data[0][i];
            
            //On utilise une formule de symétrie,  l'axe de symétrie étant la droite x = a, on a fsym(x) = f(2a - x)
            risingResults[1][i] = fitter.f(2*data[0][symetryIndex] - data[0][i]);
        }
        
        
        double[] result = new double[newXArray.length];
        
        //Valeur de x du max en y
        double xMax = data[0][symetryIndex];
        
        
        
        for (int i = 0; i < result.length; i++) {
            double x = newXArray[i];
            if (x < xMax) {
                result[i] = fitter.f(2*xMax - x);
            }
            else {
                int diff = result.length - yArray.length;
                result[i] = yArray[i - diff];
            }
           
        }
        
        
        
      
        
        
        System.out.println("########################################");
        System.out.println("Fit de courbe: " +  fitter.getResultString());
        System.out.println("########################################");
        Curve chart = new Curve("Fit courbe","R2 = " + fitter.getRSquared(), "Temps (min)",  "data", data[0], data[1]);
        
        chart.addData(newXArray, result, "Résultat Fit", Color.YELLOW, 4.0f);
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
    
    /**
     * Méthode de Quasi Newton pour une fonction
     * f(x,y) = ax<sup>2</sup> + by<sup>2</sup> + cxy + dx + ey + f
     * @param coeffs tableau [a, b, c, d, e, f]
     * @param eps tolérance sur le résultat
     * @param x0 valeur initiale    
     * @param y0 valeur initiale
     * @param xmin valeur minimale de x
     * @param xmax valeur maximale de x
     * @param ymin valeur minimale de y
     * @param ymax valeur maximale de y
     * @return  
     */
    public static double[] quadraticOptimization(double[] coeffs, double eps, double x0, double y0, 
                                                    double xmin, double xmax, double ymin, double ymax) {
        
        SimplexOptimizer optimizer = new SimplexOptimizer(eps, 1E-30);
        QuadraFunction function = new QuadraFunction(coeffs);
        
        //Contraintes de type <= et >=
        double[] lower = {xmin, ymin};
        double[] upper = {xmax, ymax};
        
        if (xmax < xmin) {
            System.out.println("xmin > xmax");
        }
       
       
        if (ymax < ymin) {
            System.out.println("ymin > ymax");
        }
        
        
        MultivariateFunctionPenaltyAdapter functionWithBounds = new MultivariateFunctionPenaltyAdapter(function, lower, upper, 1000, new double[]{1000, 1000});
        
        
        PointValuePair optimum = 
                
            optimizer.optimize(
                new MaxEval(1000), 
                new ObjectiveFunction(functionWithBounds), 
                GoalType.MINIMIZE,
                new InitialGuess(new double[]{ x0, y0 }), 
                new NelderMeadSimplex(new double[]{ 0.2, 0.2 })); //-> voir interêt NelderMeadSimplex
             
            
        //System.out.println(Arrays.toString(optimum.getPoint()) + " : " + optimum.getSecond());
         
        return optimum.getPoint();
    }
    
    private static class QuadraFunction implements MultivariateFunction  {
        
        private double[] c;
        public QuadraFunction(double[] coeffs) {
            this.c = coeffs;
        }
        public double value(double[] point)
        {
            final double x = point[0];
            final double y = point[1];
            return f(x,y);
        }
        
        private double f(double x, double y) {
            double v = c[0]*x*x + c[1]*y*y + c[2]*x*y + c[3]*x + c[4]*y + c[5];
        
        return v;
        }
    }
    
    
    
   
    
    

    
    
    
   
}
