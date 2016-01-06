/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel de Rouen"
 *   * 
 */
package QuantIF_Project.process;


import QuantIF_Project.gui.Main_Window;
import QuantIF_Project.gui.PatientSerieViewer;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.serie.DicomImage;
import QuantIF_Project.serie.TEPSerie;
import QuantIF_Project.utils.DicomUtils;
import QuantIF_Project.utils.MathUtils;
import ij.ImageStack;
import ij.gui.Roi;
import ij.process.FloatProcessor;
import ij.util.ArrayUtil;
import ij.util.Tools;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Cyriac
 */
public class Barbolosi {
    
    /**
     * Concentrations sanguine mésurées en <b>MBq</b><br/>
     * Size : NB_TAKINGS
     */
    private double[] Cbi;
    
    /**
     * Temps de mesure des différents prélèvements en <b>min</b><br/>
     * Size : NB_TAKINGS 
     * 
     */
    private double[] times;
    
    /**
     * Images de la série dynamique de fin
     */
    private FloatProcessor[][] patientImages;
    
    /**
     * Valeur de Ki
     */
    private double Ki;
    
    /**
     * Valeur de Kh issue de Hunter
     */
    private double Kh;
    
    /**
     * Valeur moyenne des max des 3 ROI autour de la tumeur pour chaque prélèvement
     */
    private double[] meanFDGTumor;
    
     /**
     * Valeur écart-type des max des 3 ROI autour de la tumeur pour chaque prélèvement
     */
    private double[] sigmaFDGTumor;
    
    
    /**
     * Valeur de Vb
     * 
     */
    private double Vb;
    
    /**
     * Aires sous la courbe
     */
    private double[] AUC;
            
    /**
     * Nombre de prélèvements de départ
     */
    private final static int NB_TAKINGS = 4;
    
    /**
     * Nombre de tirages aléatoires
     */
    private final static int NB_RANDOM_DRAWINGS = 10000;
    
    /**
     * Précision sur la sélection du meilleur Ki
     */
    private final static int NB_KH_STEPS = 1000;
    
    /**
     * Ecart type des prélévement sanguins
     */
    private final static double SIGMA_CB = 0.02;
    
    
   
    
    /**
     * Roi de recherche pour le MAX
     */
    private Roi roiSearch;
    
    /**
     * Paramètres de la Roi de recherche du max
     */
    private int startX, endX, startY, endY;
    
    /**
     * Position du pixel max {x, y, z}
     */
    private int[] pixelMax;
    
    private PatientMultiSeries pms;
    
    /**
     * Hunter servant à trouver le Kh maximisant le Ki
     */
    private Hunter hunter;
    
    /**
     * Dimensions de l'image
     */
    private int width, height;
    
    /**
     * Nombre d'images par frame
     */
    private int stackSize;
    
    /**
     * Nombre de frames dans la série dynamique de fin
     */
    private int nbFrames;
    
    /**
     * Vaut true quand on arrête les calculs avant la fin
     */
    private boolean cancelled;
    
    private Object lock;
    
    
    
    
    
    /**
     * Tableau des indices des frames les plus proche de chaque
     * prélèvement
     */
    private int[] nearestTimeFrameIndexes;
    
    
    
    public Barbolosi (PatientMultiSeries pms) {
        //On initialise les variables
        this.pms = pms;
        width = pms.getStartDynSerie().getWidth();
        height = pms.getStartDynSerie().getHeight();
        
        lock = new Object();
        
        stackSize = pms.getEndDynSerie().getNbImages();
        nbFrames = pms.getEndDynSerie().getNbBlocks();
        
        
        
        Cbi = new double[NB_TAKINGS];
        times = new double[NB_TAKINGS];
        AUC = new double[NB_TAKINGS];
        meanFDGTumor = new double[NB_TAKINGS];
        sigmaFDGTumor = new double[NB_TAKINGS];
        nearestTimeFrameIndexes = new int[NB_TAKINGS];
        cancelled = false;
        
        
        
        
        
        patientImages = new FloatProcessor[nbFrames][stackSize];
        Ki = 0;
        Kh = 0;
        Vb = 0;
        
        
        // On récupère la ROI déssinée à l'écran
        roiSearch = PatientSerieViewer.getRoi();
        
        startX = 0;
        endX = 0;
        startY = 0;
        endY = 0;
        if (roiSearch == null) {
            JOptionPane.showMessageDialog(null, "Aucune Roi n'a été déssinée!!\nVous devez dessiner la zone de recherche de maximun avant de lancer la méthode");
            cancelled = true;
        } else {
            
            //System.out.println(roiSearch);
            int showConfirmDialog = JOptionPane.showConfirmDialog(null, "La ROI dessinée à l'écran sera utilisée comme zone de recherche de maximun pour la méthode Barbolosi.\n"
                    + "Voulez vous la conserver?", "Conserver ROI", JOptionPane.YES_NO_OPTION);
            
            if (showConfirmDialog != JOptionPane.YES_OPTION)
                cancelled = true;
            
            startX = roiSearch.getPolygon().xpoints[0];
            endX = roiSearch.getPolygon().xpoints[1];
            startY = roiSearch.getPolygon().ypoints[0];
            endY = roiSearch.getPolygon().xpoints[2];
        }
        
        //Tirages aléatoires des valeurs de fdg sur les images
        System.out.println("ZONE SELECTIONNEE");
        System.out.println("X ["+startX+" "+endX+"]");
        System.out.println("Y ["+startY+" "+endY+"]");
        System.out.println("Width : " + (endX - startX) +" Height : " + (endY-startY));
        
        findPixelMax();
        
        getEndSerieImages();
        
        //On récupère les valeurs de prélèvements
        //getTakingData();
        
        //Dans le cas des tests, on uitilise des valeurs par défaut
        setDefaultTakingData();
        
        
        if (!cancelled) {
            
            //On fait un hunter avec le premier prélèvement
            hunter = new Hunter(pms, Cbi[0], times[0], false);
            
            
            setAreaUnderCurve();
            //Pour chaque prélèvement
            
               
            for (int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {

                //On cherche la frame la plus proche
                System.out.println("************ PRELEVEMENT N°"+takingIndex+" *************");
                int frameIndex = findNearestTimeFrameIndex(times[takingIndex]);
                nearestTimeFrameIndexes[takingIndex] = frameIndex;
                System.out.println("Taking N°"+ (takingIndex+1)+" -> "
                        + "Nearest Time Frame Index  = " 
                        + (nearestTimeFrameIndexes[takingIndex] + 1));

                //On cherche l'image ayant le max sur cette frame
                //  En restant dans la ROI de recherche



                if (takingIndex == 0)
                    Kh = hunter.getKh(pixelMax[2], roiSearch);

                System.out.println("Kh Hunter : " + Kh);




                FloatProcessor[] trioImages = new FloatProcessor[3]; //le trio des images -1 0 +1

                for (int i = 0; i < 3; i++) {
                    trioImages[i] = patientImages[frameIndex][pixelMax[2] + i - 1];
                }

                ImageStack is = new ImageStack(width, height);
                for (int i = 0; i < 3; i++)
                    is.addSlice(trioImages[i]);



                System.out.println("Trio d'images récupéré!");

                //On récupère la ROI déssinée par l'utilisateur



                System.out.println("Acquisition de la ROI Tumorale");

                getMeanAndSigmaFDGTumor(takingIndex, trioImages);

            }

            compileKiAndVb();
                 
             
           
            
            
            
            
            
        } 
            
    }
    
    /**
     * On récupère les images de la série dynamique de fin
     */
    private void getEndSerieImages() {
        TEPSerie serie = pms.getEndDynSerie();
        for (int frameIndex = 0; frameIndex < nbFrames; frameIndex++) {
            for (int stackIndex = 0; stackIndex < stackSize; stackIndex++) {
                try {
                    DicomImage dcm = serie.getBlock(frameIndex).getDicomImage(stackIndex); 
                    if (dcm == null)
                        patientImages[frameIndex][stackIndex] = new FloatProcessor(width, height);
                    else {
                        patientImages[frameIndex][stackIndex] = dcm.getImageProcessor();
                    }
                    } catch (BadParametersException ex) {
                    Logger.getLogger(ParaPET.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * On récupère les valeurs de prélèvement 
     */
    private void getTakingData() {
        
        //On crée les champs à remplir
        JPanel[] panels = new JPanel[NB_TAKINGS];
        JPanel superPanel = new JPanel();
        superPanel.setLayout(new BoxLayout(superPanel, BoxLayout.PAGE_AXIS));
        
        InputDataField[] hour = new InputDataField[NB_TAKINGS];
        InputDataField[] minutes = new InputDataField[NB_TAKINGS];
        InputDataField[] secondes = new InputDataField[NB_TAKINGS];
        InputDataField[] value = new InputDataField[NB_TAKINGS];
        
        for (int i = 0; i<NB_TAKINGS; i++ ) {
            panels[i] = new JPanel();
            JPanel subPanel = new JPanel();
            hour[i] = new InputDataField(2, "[0-1][0-9]|2[0-3]");

            minutes[i] = new InputDataField(2, "[0-5][0-9]");
            secondes[i] = new InputDataField(2, "[0-5][0-9]");
            value[i] = new InputDataField(8, "[0-9]+");
           
            
            
            panels[i].add(new JLabel("Cb"+ i +" : "));
            subPanel.add(hour[i]);
            subPanel.add(new JLabel("h "));
            subPanel.add(minutes[i]);
            subPanel.add(new JLabel("mn "));
            subPanel.add(secondes[i]);
            subPanel.add(new JLabel("s "));
            panels[i].add(subPanel);
            //panels[i].add(new JLabel("Entrez la valeur du prélèvement en MBq : "));
            panels[i].add(new JLabel("       "));
            panels[i].add(value[i]);
            
            superPanel.add(panels[i]);
        }
        
        boolean allOK = true;
        
        do {
            int showConfirmDialog = JOptionPane.showConfirmDialog(null, superPanel, "Données des prélèvements ", JOptionPane.DEFAULT_OPTION);
            if (showConfirmDialog == JOptionPane.CLOSED_OPTION) {
                 cancelled = true;
                 System.out.println("BARBOLOSI CANCELLED!!!");
            }   
            //On vérifie les heures
            for (InputDataField idf : hour) {
                if (!idf.checked())
                    allOK = false;
            }
            
            //On vérifie les minutes
            for (InputDataField idf : minutes) {
                if (!idf.checked())
                    allOK = false;
            }
            
            //On vérifie les secondes
            for (InputDataField idf : secondes) {
                if (!idf.checked())
                    allOK = false;
            }
            //On vérfie les valeurs de prélèvements
            for (InputDataField idf : value) {
                if (!idf.checked())
                    allOK = false;
            }
        
        } while(!allOK && !cancelled) ;
        
        //Une fois que toutes les valeurs sont vérifiées, on les enregistre
        if (!cancelled) {
            TEPSerie serie = pms.getStartDynSerie();
            for (int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {
                //On enregistre le temps
                String timeStr = hour[takingIndex].getText() + minutes[takingIndex].getText()
                                    + secondes[takingIndex].getText();
                timeStr += ".000000"; //On ajotue les fractions de secondes pour avoir le format dicom

                this.times[takingIndex] = DicomUtils.getMinutesBetweenDicomDates(serie.getSeriesTime(), timeStr);
                this.Cbi[takingIndex] = Tools.parseDouble(value[takingIndex].getText());
                System.out.println("***Prélèvement N°"+takingIndex+"***");
                System.out.println("Cb["+takingIndex+"] = "+Cbi[takingIndex]+" MBq");
                System.out.println("Temps après injection = "+times[takingIndex]+" min");
            }
        }
    }
    
    /**
     * On utilise des données de prélèvements par défaut
     */
    private void setDefaultTakingData() {
        //format : hhmmss
        String[] timesStr = {"085600", "090100", "090600", "091100"};
        TEPSerie serie = pms.getStartDynSerie();
        for (int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {
            timesStr[takingIndex] += ".000000"; // on ajoute .frac pour avoir le mm format que dans les champs DICOM
            times[takingIndex] = DicomUtils.getMinutesBetweenDicomDates(serie.getSeriesTime(), timesStr[takingIndex]);
            Cbi[takingIndex] = 1000 - takingIndex*100; 
            System.out.println("Cb["+takingIndex+"] = "+Cbi[takingIndex]+" MBq");
            System.out.println("Temps après injection = "+times[takingIndex]+" min");
        }
    }
    
    /**
     * Retrouve l'indice de la frame la plus proche de ce temps
     * @param time Temps après l'injection en <b>min</b>
     * @return Indice de la frame
     */
    private int findNearestTimeFrameIndex(double time) {
       
        TEPSerie lateSerie = pms.getEndDynSerie();
        
        //tableau contenant la delai entre "time" et le temps moyen des coupe temporelles
        double[] delay = new double[lateSerie.getNbBlocks()];
        
        for (int frameIndex = 0; frameIndex < lateSerie.getNbBlocks(); frameIndex++) {
            try {
                delay[frameIndex] = Math.abs(time - (lateSerie.getBlock(frameIndex).getMidTime()/60));
            } catch (BadParametersException ex) {
                Logger.getLogger(ParaPET.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        return MathUtils.getMinIndex(delay);
    }
    
     /**
     * On calcule les aires sous la courbe des différents prélèvements
     */
    private void setAreaUnderCurve() {
        int N = 1000; //nombres d'intervalles de calculs
        for(int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {
            double t = times[takingIndex];
            double[] x = new double[N];
            double[] y = new double[N];
            for (int i = 1; i < N; i++) {
                x[i] = x[i-1] + t/N;
                y[i] = hunter.cb(x[i]);
            }
            try {
                AUC[takingIndex] = MathUtils.AreaUnderTheCurve(x, y);
                
                
            } catch (BadParametersException ex) {
                Logger.getLogger(ParaPET.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Recherche la valeur du pixel max dans une frame
     * @param frameIndex indice de la frame
     * @return [xPixel, yPixel, imageIndex]
     */
    
    private int[] findMax(FloatProcessor[] frameImages) {
       
       
       double max = Double.NEGATIVE_INFINITY;
       
       int x, y, imageIndex = 0;
       FloatProcessor fp ;
       FloatProcessor imagePatient;
        for (int stackIndex = 0; stackIndex < stackSize; stackIndex++) {
            imagePatient = frameImages[stackIndex];
            imagePatient.setRoi(roiSearch);
            fp = (FloatProcessor) imagePatient.crop();
            
            double localMax = getMax(fp);
            if (localMax > max) {
                max = localMax;
                imageIndex = stackIndex;
            }
        }
        
      
        
        imagePatient = frameImages[imageIndex];
        imagePatient.setRoi(roiSearch);
        FloatProcessor fpCrop = imagePatient.crop().convertToFloatProcessor();
        
        
        int[] pixelPosition = getPixelPosition(fpCrop, max);
        
        int[] result = {pixelPosition[0]+startX, pixelPosition[1]+startY, imageIndex};
        
        return result;
    }
    
    /**
     * Recherche la position du premier pixel ayant la valeur val
     * @param fp image
     * @param val valeur du pixel
     * @return [x, y]
     */
    private int[] getPixelPosition(FloatProcessor fp, double val) {
        int[] result = new int[2];
        
        for(int x = 0; x < fp.getWidth(); x++ ) {
            for(int y = 0; y < fp.getHeight(); y++ ) {
                if (fp.getf(x, y) == val) {
                    result[0] = x;
                    result[1] = y;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Retourne la valeur de pixel max
     * @param fp Image FloatProcessor
     * @return 
     */
    private double  getMax (FloatProcessor fp) {
        float[] pixels = (float[]) fp.getPixels();
        double max =(new ArrayUtil(pixels)).getMaximum();
        
        return max;
        
    }
    
    
    
    /**
     * Fonction f(x,y) à minimiser
     * @param drawingIndex
     * @param FDG
     * @param x
     * @param y
     * @return 
     */
    private double f(double[] aj, double[] bj, double[] cj, double x, double y) {
        double result = 0;
        for (int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {
            double v = x*aj[takingIndex] + y*bj[takingIndex] - cj[takingIndex];
            result += v*v;
        }
        
        return result;
    }
    
    
    /**
     * Calcule de la moyenne et de l'écart-type des trois images
     * @param takingIndex Indice du prélèvement
     * @param trioImages Images autour de l'image contenant le pixelMax
     */
    private void getMeanAndSigmaFDGTumor(int takingIndex, FloatProcessor[] trioImages) {
        

        //On calcule la moyenne sur les 3 max
        for (int i = 0; i < 3; i++) {
            //La colonne 3 correspond à la valeur max de la ROI
            meanFDGTumor[takingIndex] += trioImages[i].getf(pixelMax[0], pixelMax[1])/3;
        }

        //On calcule l'écart-type
        double variance = 0;
        for (int i = 0; i < 3; i++) {
            double max  = trioImages[i].getf(pixelMax[0], pixelMax[1]);
            variance += Math.pow(max - meanFDGTumor[takingIndex], 2)/3;
        }

        sigmaFDGTumor[takingIndex] = Math.sqrt(variance);
        
        System.out.println("Mean FDG = " + meanFDGTumor[takingIndex] + "\nSigma FDG = " + sigmaFDGTumor[takingIndex]);
    }
    
    /**
     * On calcule Ki et Vb
     */
    private void compileKiAndVb() {
        //On fait les tirages aléatoires
        double[] aj = new double[NB_TAKINGS]; //Aires sous la courbes pour les différents temps de prélèvement
        double[] bj = new double[NB_TAKINGS]; //Valeurs de Cp(tj)
        double[] cj = new double[NB_TAKINGS]; //Valeurs de FDG(tj)

        for (int drawIndex = 0; drawIndex < NB_RANDOM_DRAWINGS; drawIndex++) {
            Random rand = new Random();

            for (int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {
                aj[takingIndex] = AUC[takingIndex];
                bj[takingIndex] = Cbi[takingIndex] + rand.nextGaussian()*SIGMA_CB*Cbi[takingIndex];
                cj[takingIndex] = meanFDGTumor[takingIndex] + rand.nextGaussian()*sigmaFDGTumor[takingIndex];
            }
            double[] alpha = new double[2];
            double[] beta = new double[2];
            double[] gamma = new double[2];

            for (int takingIndex  = 0; takingIndex < NB_TAKINGS; takingIndex++) {
                alpha[0] += aj[takingIndex]*aj[takingIndex];
                alpha[1] += aj[takingIndex]*bj[takingIndex];

                beta[0] += aj[takingIndex]*bj[takingIndex];
                beta[1] += bj[takingIndex]*bj[takingIndex];

                gamma[0] += aj[takingIndex]*cj[takingIndex];
                gamma[1] += bj[takingIndex]*cj[takingIndex];
            }

            double bestX = 0;
            double bestY = 0;


            /* RESOLUTION A L'AIDE DES EQUATIONS*/

            double[] resultEquations = null;
            try {
                //On résouds le système sans tenir compte des contraintes
                resultEquations = MathUtils.solveEquations(alpha, beta, gamma);
            } catch (BadParametersException ex) {
                Logger.getLogger(ParaPET.class.getName()).log(Level.SEVERE, null, ex);
            }
            bestX = resultEquations[0];
            bestY = resultEquations[1];
            
            /*
            System.out.println("Aj -> " + Arrays.toString(aj));
            System.out.println("Bj -> " + Arrays.toString(bj));
            System.out.println("Cj -> " + Arrays.toString(cj));
            */
            
            double minf = Double.POSITIVE_INFINITY;
            //Si les solutions ne respectent pas les contraintes, 
            //on les trouve en parcourant l'ensemble des valeurs posssiblse
            if ((bestX > 0) && (bestX < Kh) && (bestY < 0)) {
            }
            else {

                bestX = 0;
                bestY = 0;
                //System.out.println("Need to parse!!");
                for (int i = 0; i<=NB_KH_STEPS; i++) {
                    double x = i*Kh/NB_KH_STEPS;
                    double y = (gamma[1] - x*alpha[1])/beta[1];
                    if (y >= 0) {
                        double f = f(aj, bj, cj, x, y);
                        if (minf > f) {
                            minf = f;
                            bestX = x;
                            bestY = y;
                            
                        }
                    }
                }
            }
            
            





            Ki += bestX/NB_RANDOM_DRAWINGS;
            Vb += bestY/NB_RANDOM_DRAWINGS;

        }
        
        //On défini les 3 chiffres significatifs après la virgule
        
        BigDecimal bdKi = new BigDecimal(Ki);
        bdKi = bdKi.round(new MathContext(4));
        
        BigDecimal bdVb = new BigDecimal(Vb);
        bdVb = bdVb.round(new MathContext(4));
        
        Ki = bdKi.doubleValue();
        Vb = bdVb.doubleValue();
        System.out.println("########## Ki = " + Ki + " #########");
        System.out.println("########## Vb = " + Vb + " #########");
        
        /*
        Main_Window.println("########## Resultats BARBOLOSI #########\n");
        Main_Window.println("### Ki = " + Ki + " ###");
        Main_Window.println("### Vb = " + Vb + " ###");
       */
        
        JOptionPane.showMessageDialog(null, "Ki = " + Ki +"\nVb = " + Vb);
        
        System.out.println("Message résultat supposé affiché!!");
    }
    
    
    /**
     * On recherche le pixel max sur la somme des images de la série dynamique
     */
    private void findPixelMax() {
        
        pixelMax = findMax(pms.getEndDynSerie().getSummALL());
        
        System.out.println("Le Pixel Max se trouve sur l'image N°" + pixelMax[2]+"/"+ stackSize + ""
                + " à la position ["+pixelMax[0]+" "+pixelMax[1]+"]");
    }
    
    /**
     * On récupèle le coupe {K<sub>i</sub>, V<sub>b</sub>} résultat
     * @return {Ki, Vb}
     */
    public double[] getKiAndVb() {
        double[] kivb = {Ki, Vb};
        
        return kivb;
    }

    
    
    
}
