/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.process;

import QuantIF_Project.gui.Main_Window;
import QuantIF_Project.patient.AortaResults;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.serie.DicomImage;
import QuantIF_Project.serie.Serie;
import QuantIF_Project.utils.Curve;
import QuantIF_Project.utils.DicomUtils;
import QuantIF_Project.utils.MathUtils;
import ij.measure.CurveFitter;
import ij.measure.ResultsTable;
import ij.process.FloatProcessor;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implémentation de la méthode patlak
 * @author Cyriac
 */
public class Patlak {
    
    /**
     * Cb(t)
     * Tableau de concentration sanguines
     */
    private double[] Cb;
    
    /**
     * AUC(t)
     * Tableau des aires sous la courbe
     */
    private double[] AUC;
    
    /**
     * L'ensemble des 3 séries constituant l'acquisition
     */
    private final Serie[] series;
    
    
    /**
     * Nombres de points temporels dans la dynamique tardive
     */
    private int nbTimePoints;
    
    /**
     * Nombre de points temporels dans les données de segmentatation d'aorte
     */
    private int dataAortaSize;
    
    /**
     * Valeurs des time Points en minutes
     */
    private double[] timeArray;
    
    /**
     * Images des différentes série
     */
    private final FloatProcessor[][] patientImages;
    
    /**
     * Dimension des images
     */
    private final int width, height;
    
    /**
     * Nombre d'images sur un timePoint
     */
    private int stackSize;
    
  
    
    private final FloatProcessor[] kiImages;
    
    private final FloatProcessor[] vbImages;
    
    private static final double CB_MIN =  1E-10;
    
   
    
    
    
    /**
     * On applique la méthode de Patlak sur cette acquisition patient.
     * Les résultats de selection d'aorte utilisées sont celles les plus récentes 
     * situées dans le repertoire <b>tmp/aortaResults</b>
     * @param  patientMultiSeries PatientMultiSeries
     * 
     */
    public Patlak(PatientMultiSeries patientMultiSeries) {
        
        
        
        
        
        String aortaResultsPath = Main_Window.outputDir()+"aortaResults";
        ResultsTable rt = ResultsTable.open2(aortaResultsPath+"\\resultsTable");
        
        patientMultiSeries.loadAortaResult(aortaResultsPath);
        
        Main_Window.println("Données de segmentation d'aorte chargées de \"" + aortaResultsPath + "\"");
        
        this.series = new Serie[3];
        
        series[0] = patientMultiSeries.getStartDynSerie();
        series[1] = patientMultiSeries.getStaticSerie();
        series[2] = patientMultiSeries.getEndDynSerie();
        
        
        //Nombres de points placées sur Cb(t)
        dataAortaSize = rt.size();
        
        nbTimePoints = series[2].getNbBlocks();
        
       
           
        
        
        
        
        
        try {
            stackSize = series[0].getNbImages(0);
        } catch (BadParametersException ex) {
            Logger.getLogger(Patlak.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.width = series[0].getWidth();
        this.height = series[0].getHeight();
        
        //On initialise les tableaux de concentration et d'aire sous la courbe
        Cb = new double[dataAortaSize];
        
        AUC = new double[dataAortaSize];
        
        timeArray = new double[dataAortaSize];
        
        //On les remplit à l'aide des résultats lié à la segmentation de l'aorte
        
        setBloodConcentrations(patientMultiSeries.getAortaResults());
        
        //On calcules les aires sous la courbe
        setAreaUnderCurve();
        
        patientImages = new FloatProcessor[stackSize][nbTimePoints];
        kiImages = new FloatProcessor[stackSize];
        vbImages = new FloatProcessor[stackSize];
        
        /*
         On vas essayer de construire une seule image de Ki/Vb
         1- On doit récupérer pour chaque timePoint , les images patient correspondant à la frame
         2- Pour chaque pixel, on récupère la valeur du pixel (correspond à FDG(t))
            pour chaque timePoints
         3- On calcule les coordonnées {FDG(t)/Cb(t), AUC(t)/Cb(t)} correspondantes
            ce qui nous donne nbTimePoints points à placer.
         4- On fait une régression linéaire à l'aide des points et on trouve les paramètres Ki/Vb
         5- Puis on boucle sur tous les pixels et on construit ainsi l'image Ki/Vb
         */
        
        // 1- On doit récupérer pour chaque timePoint , les images 
        for (int stackIndex = 0; stackIndex < stackSize; stackIndex ++) 
            patientImages[stackIndex] = getPatientImages(stackIndex);
        
        // On calcule l'image des Ki pour chaque coupe spaciale
        for (int stackIndex = 0; stackIndex < stackSize; stackIndex ++) {
            FloatProcessor[] kivb = buidKiAndVbImage(stackIndex);
            kiImages[stackIndex] = kivb[0];
            vbImages[stackIndex] = kivb[1];
        }    
        
        //On affiche les images Ki
        Main_Window.print("Affichage Images Ki");
        DicomUtils.display(kiImages, "Ki Images PATLAK");
        
        //On sauvegarde les images Ki & Vb
        DicomUtils.saveImages(kiImages, "tmp\\Patlak\\imagesKi\\");
        DicomUtils.saveImages(vbImages, "tmp\\Patlak\\imagesVb\\");
        
        
      
        
    }
    
    /**
     * Récupère le tableau de concentrations sanguines après le fit.
     * On fait le fit sur la série dynamique de départ (plus la série statique si présente) 
     * et on garde les valeurs de la série dynamique de fin
     * @param aortaResults résultats d'aorte de la multi-acquisition
     * @return {double[]} tableau de concentrations sanguines
     */
    private void setBloodConcentrations(AortaResults aortaResults) {
        try {
           
            
            ResultsTable rt = aortaResults.getResultsTable();
            
            Cb = rt.getColumnAsDoubles(1); //On recupere la VALEUR MOYENNE
            timeArray = rt.getColumnAsDoubles(5); //On récupère le valeur MID TIME;
            //On passe les temps en minute
            for (int i = 0; i < timeArray.length; i++) {
                timeArray[i] /= 60;
            }
            
            //ON FAIT LE FIT sur la premiere série dynamique de debut (et la série TAP
            // si elle est présente) et on garde les valeurs de départ pour la série dynamique
            // de fin
            
            double[] CbfirstDyn = Arrays.copyOfRange(Cb, 0,  Cb.length-series[2].getNbBlocks());
            double[] timeArrayfirstDyn = Arrays.copyOfRange(timeArray, 0, Cb.length- series[2].getNbBlocks());
            
            CbfirstDyn = MathUtils.arterialFit(timeArrayfirstDyn, CbfirstDyn);
            System.arraycopy(CbfirstDyn, 0, Cb, 0, CbfirstDyn.length); 
            
            
            //Cb = MathUtils.arterialFit(timeArray, Cb);
            //rt.show("Aorta results");
            //On remplace les zéros par des valeurs infinitésimales
            
            /*
            for (int i = 0 ; i<Cb.length; i++) {
            if (Cb[i] < CB_MIN)
                Cb[i] = CB_MIN;
            }
            */
            
        } catch (BadParametersException ex) {
            Logger.getLogger(Patlak.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         System.out.println("On récupère les valeurs de concentrations sanguine");
       
    }
    
    /**
     * Calcule l'aire sous la courbe
     */
    private void setAreaUnderCurve() {
        
       
        
        for (int timeIndex = 0; timeIndex < dataAortaSize; timeIndex++) {
            double[] xVals = Arrays.copyOfRange(timeArray, 0, timeIndex+1);
            double[] yVals = Arrays.copyOfRange(Cb, 0, timeIndex+1);
            
            try {
                AUC[timeIndex] = MathUtils.AreaUnderTheCurve(xVals, yVals);
            } catch (BadParametersException ex) {
                Logger.getLogger(Patlak.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.println("On récupère les aires sous la courbe");
    }
    
    /**
     * On construit l'image Ki et Vb
     * @return 
     */
    private FloatProcessor[] buidKiAndVbImage(int stackIndex) {
        
        /*
         2- Pour chaque pixel, on récupère la valeur du pixel (correspond à FDG(t))
            pour chaque timePoints de la dyn tardive
         3- On calcule les coordonnées {FDG(t)/Cb(t), AUC(t)/Cb(t)} correspondantes
            ce qui nous donne nbTimePoints points à placer.
         4- On fait une régression linéaire à l'aide des points et on trouve les paramètres Ki/Vb
         5- Puis on boucle sur tous les pixels et on construit ainsi l'image Ki/Vb
        */
        
        //System.out.println("On Construit les images Ki et Vb pour le stackIndex " + (stackIndex+1));
        boolean first = true;
               
        float[] kiArray = new float[width*height];
        float[] vbArray = new float[width*height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                
                //Pour chaque pixel, on doit avoir l'ensembles des abscices et des ordonnées
                //  pour la régression linéaire
                double[] xArray = new double[nbTimePoints];
                double[] yArray = new double[nbTimePoints];
                
               
                //Pour chaque valeur de timeIndex, on calcule les coordonées {FDG(t)/Cb(t), AUC(t)/Cb(t)}
                for (int timeIndex = 0; timeIndex < nbTimePoints; timeIndex++) {
                    double fdg = patientImages[stackIndex][timeIndex].getf(x, y);
                    
                    yArray[timeIndex] = fdg/Cb[dataAortaSize - nbTimePoints + timeIndex];
                    xArray[timeIndex] = AUC[dataAortaSize - nbTimePoints + timeIndex]/Cb[dataAortaSize - nbTimePoints + timeIndex];
                    
                }
                
                
                
                //On fait une régression linéaire sur ces valeurs
                
                CurveFitter linRegr = new CurveFitter(xArray, yArray);
                linRegr.doFit(CurveFitter.STRAIGHT_LINE);
                if (x == 100 && y == 80 && stackIndex == 41) {
                    System.out.println("Fit linéaire :" + linRegr.getResultString());
                    Curve chart = new Curve("RegressionLinéaire", "Image "+(stackIndex+1), "x", "y", xArray, yArray);
                }
                
                kiArray[y*width + x] = (float) linRegr.getParams()[1];
                vbArray[y*width + x] = (float) linRegr.getParams()[0];
                
               
                
            }
        }
        
        
        FloatProcessor kiProc = new FloatProcessor(width, height, kiArray);
                
        FloatProcessor vbProc = new FloatProcessor(width, height, vbArray);
       
        
        FloatProcessor[] kivb = {kiProc, vbProc};
        
        return kivb;
    }
    
    /**
     * Recupère les images patient de dynamique tardive....... sous forme de ImageProcessor
     * @return 
     */
    private FloatProcessor[] getPatientImages(int stackIndex)  {
        FloatProcessor[] imagesProc = new FloatProcessor[nbTimePoints];
        int currentTimeIndex = 0;
        //On parcourt les séries
        Serie serie = series[2];
        
        for (int timeIndex = 0; timeIndex < serie.getNbBlocks(); timeIndex++) {
            if (currentTimeIndex < nbTimePoints) {

                try {
                    DicomImage dcm = serie.getBlock(timeIndex).getDicomImage(stackIndex);
                    if (dcm == null)
                        imagesProc[currentTimeIndex] = new FloatProcessor(width, height);
                    else {
                        imagesProc[currentTimeIndex] = dcm.getImageProcessor();
                    }
                    currentTimeIndex++;
                } catch (BadParametersException ex) {
                    Logger.getLogger(Patlak.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        
        
        
        //Affichage images patient
        /*
        ImageStack is = new ImageStack(width, height);
        for (FloatProcessor ip : imagesProc) {
            //ip.resetThreshold();
            is.addSlice(ip);
        }
        
        ImagePlus imp = new ImagePlus("Images Patient", is);
        imp.show();
        */
        return imagesProc;
    }
    
    

    
}
