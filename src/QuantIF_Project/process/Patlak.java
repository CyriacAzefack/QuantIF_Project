/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.process;

import QuantIF_Project.gui.PatientSerieViewer;
import QuantIF_Project.patient.AortaResults;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.serie.DicomImage;
import QuantIF_Project.serie.Serie;
import QuantIF_Project.utils.DicomUtils;
import QuantIF_Project.utils.MathUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.CurveFitter;
import ij.measure.ResultsTable;
import ij.process.FloatProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
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
     * Nombres de points temporels dans l'acquisition totale
     */
    private int nbTimePoints;
    
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
    
    private static final double CB_MIN =  1E-10;
    
   
    
    
    
    /**
     * On applique la méthode de Patlak sur cette acquisition patient.
     * Les résultats de selection d'aorte utilisées sont celles les plus récentes 
     * situées dans le repertoire <b>tmp/aortaResults</b>
     * @param  patientMultiSeries PatientMultiSeries
     * 
     */
    public Patlak(PatientMultiSeries patientMultiSeries) {
        
        String aortaResultsPath = "tmp\\aortaResults";
        ResultsTable rt = ResultsTable.open2(aortaResultsPath+"\\resultsTable");
        patientMultiSeries.loadAortaResult(aortaResultsPath);
        this.series = new Serie[3];
        series[0] = patientMultiSeries.getStartDynSerie();
        series[1] = patientMultiSeries.getStaticSerie();
        series[2] = patientMultiSeries.getEndDynSerie();
        
        
        //Nombres de points placées sur Cb(t)
        nbTimePoints = rt.size();
        
        
        
        
        try {
            stackSize = series[0].getNbImages(0);
        } catch (BadParametersException ex) {
            Logger.getLogger(Patlak.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.width = series[0].getWidth();
        this.height = series[0].getHeight();
        
        //On initialise les tableaux de concentration et d'aire sous la courbe
        Cb = new double[nbTimePoints];
        
        AUC = new double[nbTimePoints];
        
        timeArray = new double[nbTimePoints];
        
        //On les remplit à l'aide des résultats lié à la segmentation de l'aorte
        
        setBloodConcentrations(patientMultiSeries.getAortaResults());
        
        setAreaUnderCurve();
        
        patientImages = new FloatProcessor[stackSize][nbTimePoints];
        kiImages = new FloatProcessor[stackSize];
        
        /*
         On vas essayer de construire une seule image de Ki/Vb
         1- On doit récupérer pour chaque timePoint , la première image patient.
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
        for (int stackIndex = 0; stackIndex < stackSize; stackIndex ++) 
            kiImages[stackIndex] = buidKiImage(stackIndex);
        
        displayAndSaveKiImages();
        
    }
    
    /**
     * Renvoie le tableau de concentrations sanguines après le fit.
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
        
       
        
        for (int timeIndex = 0; timeIndex < nbTimePoints; timeIndex++) {
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
     * On construit l'image Ki
     * @return 
     */
    private FloatProcessor buidKiImage(int stackIndex) {
        
        /*
         2- Pour chaque pixel, on récupère la valeur du pixel (correspond à FDG(t))
            pour chaque timePoints
         3- On calcule les coordonnées {FDG(t)/Cb(t), AUC(t)/Cb(t)} correspondantes
            ce qui nous donne nbTimePoints points à placer.
         4- On fait une régression linéaire à l'aide des points et on trouve les paramètres Ki/Vb
         5- Puis on boucle sur tous les pixels et on construit ainsi l'image Ki/Vb
        */
        
        //System.out.println("On Construit les images Ki et Vb pour le stackIndex " + (stackIndex+1));
        boolean first = true;
        
        int nbErrors = 0;
                
        float[] kiArray = new float[width*height];
        float[] vbArray = new float[width*height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //System.out.println("***** NEW PIXEL ***** ");
                //Pour chaque pixel, on doit avoir l'ensembles des abscices et des ordonnées
                //  pour la régression linéaire
                double[] xArray = new double[nbTimePoints];
                double[] yArray = new double[nbTimePoints];
                
                /**
                 * Vaut true si ts les valeurs de fdg sont nulles
                 */
                boolean nullFDG = true;
                //Pour chaque valeur de timeIndex, on calcule les coordonées {FDG(t)/Cb(t), AUC(t)/Cb(t)}
                for (int timeIndex = 0; timeIndex < nbTimePoints; timeIndex++) {
                    double fdg = patientImages[stackIndex][timeIndex].getPixelValue(x, y);
                    //System.out.println(timeIndex);
                    yArray[timeIndex] = fdg/Cb[timeIndex];
                    xArray[timeIndex] = AUC[timeIndex]/Cb[timeIndex];
                    //System.out.println("X : " + xArray[timeIndex] + "\nY : " + yArray[timeIndex]);
                }
                
                
                
                //On fait une régression linéaire sur ces valeurs
                
                CurveFitter linRegr = new CurveFitter(xArray, yArray);
                linRegr.doFit(CurveFitter.STRAIGHT_LINE);
                
        
                
                kiArray[y*width + x] = (float) linRegr.getParams()[1];
                vbArray[y*width + x] = (float) linRegr.getParams()[0];
                
               
                /*
                if (x==102 && y==78 && stackIndex >= 45 && stackIndex <= 50) {
                    Curve c = new Curve("Linear Regression" , "R2 = " + linRegr.getRSquared() 
                            + " & stackIndex ="+ stackIndex, "AUC(t)/Cb(t)", 
                            "FDG(t)/Cb(t)", xArray, yArray);
                    double[] droite = new double[xArray.length];
                    for (int i = 0; i < xArray.length; i++) {
                        droite[i] = linRegr.f(xArray[i]);
                    }
                    c.addData(xArray, droite, "droite");
                    c.setVisible( true );
                    //On place la courbe au centre de l'écran
                    RefineryUtilities.centerFrameOnScreen(c);
                    System.out.println(linRegr.getResultString());
                }
                
                /*
                if (vbArray[y*width + x] == 0) {
                    System.out.println("Vb max val");
                }
                */
                
                //Affichage Ki/Vb
                /*
                System.out.println("Ki : " + kiArray[x][y]);
                System.out.println("Vb : " + vbArray[x][y]);
                */
            }
        }
        
        /*
        System.out.println("****************************");
        System.out.println("Nombre d'erreurs : " + nbErrors);
        System.out.println("****************************");
        */
        FloatProcessor kiProc = new FloatProcessor(width, height, kiArray);
        //ImagePlus impKi = new ImagePlus("Ki Image", kiProc);
        //impKi.show();
        
        FloatProcessor vbProc = new FloatProcessor(width, height, vbArray);
        //ImagePlus impVb = new ImagePlus("Vb Image", vbProc);
        //impVb.show();
        
        
        return kiProc;
    }
    
    /**
     * Recupère les images patient sous forme de ImageProcessor
     * @return 
     */
    private FloatProcessor[] getPatientImages(int stackIndex)  {
        FloatProcessor[] imagesProc = new FloatProcessor[nbTimePoints];
        int currentTimeIndex = 0;
        //On parcourt les séries
        for (Serie serie : series) {
            
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
    
    /**
     * On sauvegarde et on affiche les images des Ki
     */
    private void displayAndSaveKiImages() {
        String dirPath = "tmp\\imagesKi";
        File file = new File(dirPath);
        //On vide le dossier
        DicomUtils.emptyDirectory(file);
        ImagePlus imp;
        ImageStack is = new ImageStack(width, height);
        BufferedImage[] buffs = new BufferedImage[kiImages.length];
        for (int i = 0; i < buffs.length; i++) {
            buffs[i] = kiImages[i].getBufferedImage();
            is.addSlice(kiImages[i]);
            imp = new ImagePlus("image "+i, kiImages[i]);
            //On sauvegarde les images ki
            IJ.save(imp, file.getAbsolutePath()+"\\IM"+i);
        }
        
        System.out.println("Images des Ki sauvegardées dans le dossier \"" 
                + file.getAbsolutePath() + "\"");
        
       PatientSerieViewer.setDisplayedImage(buffs, "Ki Images");
       
      
      
       
    }

    
}
