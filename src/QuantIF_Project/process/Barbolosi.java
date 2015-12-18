/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel de Rouen"
 *   * 
 */
package QuantIF_Project.process;

import QuantIF_Project.gui.PatientSerieViewer;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.serie.DicomImage;
import QuantIF_Project.serie.TEPSerie;
import QuantIF_Project.utils.DicomUtils;
import QuantIF_Project.utils.MathUtils;
import ij.gui.Roi;
import ij.process.FloatProcessor;
import ij.util.Tools;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
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
     * Modèle de hunter
     */
    private Hunter hunter;
    
    /**
     * Images de la série dynamique de fin
     */
    private FloatProcessor[][] patientImages;
    
    /**
     * Images des Ki<br/>
     * Size : StackSize
     */
    private FloatProcessor[] imagesKi; 
    
    /**
     * Images des Vb<br/>
     * Size : StackSize
     * 
     */
    private FloatProcessor[] imagesVb;
            
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
    private final static double SIGMA_Cb = 0.5;
    
    /**
     * Moyenne de chaque prélèvement sanguins après les
     * tirages aléatoires <br/>
     * Size : NB_TAKINGS
     */
    private final double[] meanCb;
    
    /**
     * Images écart-type des frames -1 0 +1 de chaque prélèvement<br/>
     * Size : NB_TAKINGS x StackSize
     * 
     */
    private final FloatProcessor[][] sigmaImages;
    
    /**
     * Images moyenne des frames -1 0 +1 de chaque prélèvement <br/>
     * Size : NB_TAKINGS x StackSize
     */
    private final FloatProcessor[][] meanImages;
    
   
            
    /**
     * Images moyenne après les tirages aléatoires <br/>
     * Size : NB_TAKINGS x StackSize
     */
    private final FloatProcessor[][] meanImagesAfterRandomDrawings;
    
    /**
     * On annule le déroulement du calcul
     */
    private boolean cancelled;
    
    /**
     * Nombre d'images dans une coupe temporelle
     */
    private final int stackSize;
    
    /**
     * Indices des coupe temporelle les plus proches par prélèvement <br/>
     * Size : NB_TAKINGS
     */
    private final int[] nearestTimeFrameIndexes;
    
    private final PatientMultiSeries pms;
    
    /**
     * Dimension des images
     */
    private final int width, height;
    
    /**
     * Zone dans laquelle sera faite les calculs
     */
    private Roi roi;
    
    /**
     * Zone sélectionnée
     */
    private int startX, endX, startY, endY;
    
    /**
     * Ensemble des tirages aléatoires Concernant les prélèvement <br/>
     * Size : NB_TAKINGS x NB_RANDOM_DRAWINGS
     */
    private double[][] CbValues; 
    
    /**
     * Aires sous la courbe pour les différents prélèvements <br/>
     * Size : NB_TAKINGS
     */
    private double[] AUC;
    
    /**
     * On applique la méthode de barbolosie sur l'acquisition en cours
     * 
     * @param pms PatientMutiSeries
     */
    
    public Barbolosi(PatientMultiSeries pms) {
        
        //On initialise les variables
        this.pms = pms;
        width = pms.getStartDynSerie().getWidth();
        height = pms.getStartDynSerie().getHeight();
        
        
        stackSize = pms.getStartDynSerie().getNbImages();
        //stackSize = 1;
        
        
        Cbi = new double[NB_TAKINGS];
        times = new double[NB_TAKINGS];
        cancelled = false;
        
        patientImages = new FloatProcessor[pms.getEndDynSerie().getNbBlocks()][stackSize];
        imagesKi = new FloatProcessor[stackSize];
        imagesVb = new FloatProcessor[stackSize];
        
        meanCb = new double[NB_TAKINGS];
        sigmaImages = new FloatProcessor[NB_TAKINGS][stackSize];
        meanImages = new FloatProcessor[NB_TAKINGS][stackSize];
        meanImagesAfterRandomDrawings = new FloatProcessor[NB_TAKINGS][stackSize];
        nearestTimeFrameIndexes = new int[NB_TAKINGS];
        CbValues = new double[NB_TAKINGS][NB_RANDOM_DRAWINGS];
        AUC = new double[NB_TAKINGS];
        
        
        roi = PatientSerieViewer.getRoi();
        startX = 0;
        endX = 0;
        startY = 0;
        endY = 0;
        if (roi == null) {
            JOptionPane.showMessageDialog(null, "Aucune Roi n'a été déssinée!!");
            cancelled = true;
        } else {
            Polygon p = roi.getPolygon();
            startX = p.xpoints[0]*width/PatientSerieViewer.IMAGE_SIZE;
            endX = p.xpoints[1]*width/PatientSerieViewer.IMAGE_SIZE;
            startY = p.ypoints[0]*width/PatientSerieViewer.IMAGE_SIZE;
            endY = p.ypoints[2]*width/PatientSerieViewer.IMAGE_SIZE;
        }
        
        getPatientImages();
        
        //On commence par récupérer les valeurs de prélèvements
        //getTakingData();
        setDefaultTakingData();
        
        
        //cancelled  = true;
        if (!cancelled) { //On Commence les calculs
            
            //Début chronomètre
            long debut = System.currentTimeMillis();
           
            
           
            
             //On fait un hunter avec le premier prélèvement
            hunter = new Hunter(pms, Cbi[0], times[0]);
            
             setAreaUnderCurve();
            
            //On recherche les frames les plus proches des temps de prélèvements
            for (int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {
                this.nearestTimeFrameIndexes[takingIndex] = findNearestTimeFrameIndex(times[takingIndex]);
                System.out.println("Taking N°"+ (takingIndex+1)+" -> "
                        + "Nearest Time Frame Index  = " 
                        + (this.nearestTimeFrameIndexes[takingIndex] + 1));
                //On calcule les images moyennes
                meanImages[takingIndex] = getMeanFrame(nearestTimeFrameIndexes[takingIndex]);
                
            }
            
            
            
            //On calcule les images sigma
            for (int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {
                int nearestFrameIndex = nearestTimeFrameIndexes[takingIndex];
                sigmaImages[takingIndex] = getSigmaImages(takingIndex, nearestFrameIndex);
            }
            
            //Générations aléatoires des valeurs de Cb et calcul de leur moyenne
            
            System.out.println("Générations aléatoires des valeurs de Cb et calcul de leur moyenne");
            for (int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {
                //si x suit une loi normale de moyenne nulle et d'écart-type 1
                //  alors la variable X = m + s*x suit une loi normale de moyenne
                //  m et d'écart type s
                
               
                //double[] CbValues = new double[NB_RANDOM_DRAWINGS];  //Ensemble des valeurs après les tirages aléatoires
                Random rand = new Random(); //Générateur de nombre aléatoires
                
                for (int drawingIndex = 0; drawingIndex < NB_RANDOM_DRAWINGS; drawingIndex++) {
                    double epsilonCb = rand.nextGaussian()*SIGMA_Cb*Cbi[takingIndex];
                   
                    CbValues[takingIndex][drawingIndex] = Cbi[takingIndex] + epsilonCb;
                    meanCb[takingIndex] += CbValues[takingIndex][drawingIndex]/NB_RANDOM_DRAWINGS;
                }
                
                System.out.println("Valeur moyenne calculée de Cb["+(takingIndex+1)+"] = "+meanCb[takingIndex]);
            }    
            
            

            //Tirages aléatoires des valeurs de fdg sur les images
            System.out.println("ZONE SELECTIONNEE");
            System.out.println("X ["+startX+" "+endX+"]");
            System.out.println("Y ["+startY+" "+endY+"]");
            System.out.println("Width : " + (endX - startX) +" Height : " + (endY-startX));
            
            buildKiAndVbImages();
            
            
            
            long fin = System.currentTimeMillis();
            
            System.out.println("###########################################################");
            float time = (float)(fin - debut)/1000;
            System.out.println("## Temps de calcul = " + time + " s -> "+time/60+" min ##");
            System.out.println("###########################################################");
            
            
            //TRAITEMENT DES RESULTATS
            //saveImages(meanImages[0], "tmp\\meanImagesTaking1\\");
            
            
            
            display(imagesKi, "Images des Ki Barbolosi avec "+NB_RANDOM_DRAWINGS+" tirages!!");
            
            
            
            
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
        String[] timesStr = {"085600", "090100", "090600", "091100"};
        TEPSerie serie = pms.getStartDynSerie();
        for (int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {
            timesStr[takingIndex] += ".000000";
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
                Logger.getLogger(Barbolosi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        return MathUtils.getMinIndex(delay);
    }
    
    /**
     * On calcule les images moyenne entre les frames -1 0 +1
     * @param frameIndex frame centrale
     * @return 
     */
    private FloatProcessor[] getMeanFrame(int frameIndex) {
        TEPSerie serie = pms.getEndDynSerie();
        FloatProcessor[] resultImages = new FloatProcessor[stackSize];
        
        float[][] summSlices = serie.summSlices(frameIndex-1, frameIndex+1);
        
        for (int stackIndex = 0; stackIndex < summSlices.length; stackIndex++) {
            for (int pixelIndex = 0; pixelIndex < summSlices[0].length; pixelIndex++) {
                summSlices[stackIndex][pixelIndex] /=3;
            }
            
            resultImages[stackIndex] = new FloatProcessor(width, height, summSlices[stackIndex]);
        }
        
        return resultImages;
    }
    
    /**
     * On calcule les images écart type entre -1 0 +1
     * @param takingIndex Indice du prélèvement
     * @return une stack d'images sigma
     */
    private FloatProcessor[] getSigmaImages(int takingIndex, int frameIndex) {
        TEPSerie serie = pms.getEndDynSerie();
        FloatProcessor[] resultImages = new FloatProcessor[stackSize];
        
        //on récupère les images des moyennes pour ce prélèvement
        System.out.println("Calcul de sigma -> Indice du prélèvement :"+takingIndex);
        
        FloatProcessor[] meanImage = meanImages[takingIndex];
        
        //Images sur lesquelles on calcule la variance 
        FloatProcessor[][] imagesPatient = new FloatProcessor[3][stackSize];
        for (int i = 0; i < 3; i++) {
            imagesPatient[i] = patientImages[frameIndex + i - 1];
        }
        
        for (int stackIndex = 0; stackIndex < stackSize; stackIndex++) {
            //Valeur de pixels de l'image sigma
            float[] sigmaPixels = new float[width*height];
            //Les 3 images sur lesquels on vas travaille
            FloatProcessor[] images = {imagesPatient[0][stackIndex], imagesPatient[1][stackIndex], imagesPatient[1][stackIndex]};
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    //On calcule l'écart type pour chaque pixel
                    float m = meanImage[stackIndex].getPixelValue(x, y); //moyenne
                    float variance = 0; //variance
                    for (int i = 0; i < 3; i++) {
                        variance += Math.pow(images[i].getPixelValue(x, y) - m, 2);
                    }
                    variance /= 3;
                    
                    sigmaPixels[y*width + x] = (float) Math.sqrt(variance);
                }
            }
            
            resultImages[stackIndex] = new FloatProcessor(width, height, sigmaPixels);
        }
        
        
        return resultImages;
        
    }
    
    /**
     * On récupère les images de la série dynamique de fin
     */
    private void getPatientImages() {
        TEPSerie serie = pms.getEndDynSerie();
        for (int frameIndex = 0; frameIndex < this.patientImages.length; frameIndex++) {
            for (int stackIndex = 0; stackIndex < this.stackSize; stackIndex++) {
                try {
                    DicomImage dcm = serie.getBlock(frameIndex).getDicomImage(stackIndex); 
                    if (dcm == null)
                        patientImages[frameIndex][stackIndex] = new FloatProcessor(width, height);
                    else {
                        patientImages[frameIndex][stackIndex] = dcm.getImageProcessor();
                    }
                    } catch (BadParametersException ex) {
                    Logger.getLogger(Barbolosi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void display(FloatProcessor[] images, String title) {
        
        BufferedImage[] buffs = new BufferedImage[images.length];
        for (int i = 0; i < images.length; i++) {
            buffs[i] = images[i].getBufferedImage();
        }
        
        PatientSerieViewer.setDisplayedImage(buffs, "title");
    }
    
    
    
    /**
     * Effectue les tirages aléatoires pour chaque pixel et renvoie l'image moyenne résultant
     * @param takingIndex Indice du prélèvement
     * @param stackIndex Indice spacial de l'image
     * @return 
     */
    private void randomDrawingsImageResult(int takingIndex, int stackIndex) {
        float[] pixelsAfterDrawings = new float[width*height];
        
        
        FloatProcessor meanImage = meanImages[takingIndex][stackIndex];
        FloatProcessor sigmaImage = sigmaImages[takingIndex][stackIndex];
        float[] pixelsTest = (float[]) meanImage.getPixels();
        
            double fdg;
            double meanFdg;
            double epsilon;
            
            for (int x = 0; x < width; x++) {
                if (x >= startX && x <= endX) {
                    for (int y = 0; y < height; y++) {
                        if (y >= startY && y <= endY) {
                            Random rand = new Random();
                            meanFdg = 0;
                            fdg = pixelsTest[y*width + x];

                            
                            

                            for (int drawingIndex = 0; drawingIndex < NB_RANDOM_DRAWINGS; drawingIndex++) { //On limite les le calcul à la zone de la ROI
                                epsilon = rand.nextGaussian()*sigmaImage.getPixelValue(x, y);
                                
                                meanFdg += (fdg + epsilon)/NB_RANDOM_DRAWINGS;


                            }

                            pixelsAfterDrawings[y*width + x] = (float) meanFdg;
                        }   
                    }
                }
            }
            
            
            
        //System.out.println("Indice de prélèvement ->" + (takingIndex+1)+ " & StackIndex ->" +(stackIndex+1)+ " [TRAITE]");
        //System.out.println(Arrays.toString(pixelsAfterDrawings));
        FloatProcessor fp = new FloatProcessor(width, height, pixelsAfterDrawings);
        FloatProcessor nfp = new FloatProcessor(width, height, pixelsTest);
        /*
        if (stackIndex == 35) {
           
            System.out.println("Avant tirages -> Fdg = " + pixelsTest[86*width + 102]);
            System.out.println("Après tirages -> Fdg = " + pixelsAfterDrawings[86*width + 102]);

                    
            new ImagePlus("Avant tirage : " +takingIndex +"x"+stackIndex,nfp).show();
            new ImagePlus("Apres tirage : " +takingIndex +"x"+stackIndex,fp).show();
           
        }
        */  
        meanImagesAfterRandomDrawings[takingIndex][stackIndex] = fp;
        
    }
    
    /**
     * Effectue les tirages aléatoires pour chaque pixel et calcule les Ki et Vb
     * @param stackIndex Indice spacial de l'image
     * @return 
     */
    private void buildKiAndVbImages(int stackIndex) {
        float[] kiPixels = new float[width*height];
        float[] vbPixels = new float[width*height];
        
        float n1 = 0;
        float nParses = 0;
        for (int col = startX; col < endX; col++) { //colonnes <-> x
            
            for (int row = startY; row < endY; row++) { //lignes <-> y


                double[] aj = new double[NB_TAKINGS]; //Aires sous la courbes pour les différents temps de prélèvement
                double[] bj = new double[NB_TAKINGS]; //Valeurs de Cp(tj)
                double[] cj = new double[NB_TAKINGS]; //Valeurs de FDG(tj)

                double meanKi = 0;
                double meanVb = 0;

                //Kh maximisant les ki, récupéré sur l'image des KH de Hunter
                double kh = hunter.getKh(stackIndex, col, row);

                for (int drawIndex = 0; drawIndex < NB_RANDOM_DRAWINGS; drawIndex++) {
                    Random rand = new Random();
                    for (int takingIndex  = 0; takingIndex < NB_TAKINGS; takingIndex++) {
                        aj[takingIndex] = AUC[takingIndex];
                        bj[takingIndex] = CbValues[takingIndex][drawIndex];

                        double oldFdg = meanImages[takingIndex][stackIndex].getPixelValue(col, row);
                        double epsilon = rand.nextGaussian()*sigmaImages[takingIndex][stackIndex].getPixel(col, row);
                        cj[takingIndex] = oldFdg + epsilon;
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
                        Logger.getLogger(Barbolosi.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    bestX = resultEquations[0];
                    bestY = resultEquations[1];
                    double bestf = f(drawIndex, cj, bestX, bestY);

                    double minf = Double.POSITIVE_INFINITY;
                    //Si les solutions ne respectent pas les contraintes, 
                    //on les trouve en parcourant l'ensemble des valeurs posssiblse
                    if ((bestX < 0) || (bestX > kh) || (bestY < 0)) {
                        nParses ++;
                        bestX = 0;
                        bestY = 0;
                        //System.out.println("Need to parse!!");
                        for (int i = 0; i<=NB_KH_STEPS; i++) {
                            double x = i*kh/NB_KH_STEPS;
                            double y = (gamma[1] - x*alpha[1])/beta[1];
                            if (y >= 0) {
                                double f = f(drawIndex, cj, x, y);
                                if (minf > f) {
                                    minf = f;
                                    bestX = x;
                                    bestY = y;
                                }
                            }
                        }
                    }
                    else {
                        n1++;
                    }
                    
                    if (bestX < 0) {
                        System.out.println("SHHHHHHHHHHHHAAAAAAAAAAAAMMMMMMMME ON X!!");
                    }
                    
                    if (bestY < 0) {
                        System.out.println("SHHHHHHHHHHHHAAAAAAAAAAAAMMMMMMMME ON Y!!");
                    }


                    meanKi += bestX/NB_RANDOM_DRAWINGS;
                    meanVb += bestY/NB_RANDOM_DRAWINGS;
                }

                kiPixels[row*width + col] = (float) meanKi;
                vbPixels[row*width + col] = (float) meanVb;
            }
          
        }
        
        
        
            //System.out.println("        Parse"+stackIndex+"/"+stackSize+" : " + 100*nParses/(nParses+n1) + "%");
            //System.out.println("        Equations"+stackIndex+"/"+stackSize+" : " + 100*n1/(nParses+n1) + "%");
        imagesKi[stackIndex] = new FloatProcessor(width, height, kiPixels);
        imagesVb[stackIndex] = new FloatProcessor(width, height, vbPixels);
    }
    
    /**
     * On calcule les aires sous la courbe des différents prélèvements
     */
    private void setAreaUnderCurve() {
        int N = 100; //nombres d'intervalles de calculs
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
                Logger.getLogger(Barbolosi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * On construit les images Ki et Vb
     */
    private void buildKiAndVbImages() {
        System.out.println("*************DEBUT TIRAGES ALEATOIRES IMAGES**************");
        Thread[] takingThreads = new Thread[stackSize];
        for (int i = 0; i < stackSize; i++) {
            int stackIndex = i;

            takingThreads[stackIndex] = new Thread("Calcul Image "+stackIndex) {
                @Override
                public void run() {
                    System.out.println("Début image N° "+stackIndex+"/"+stackSize);
                    buildKiAndVbImages(stackIndex);
                    System.out.println("Fin image N° "+stackIndex+"/"+stackSize);


                }
            };
            takingThreads[stackIndex].start();

        }
        
        //On attends que toute se termine
        for (int stackIndex = 0; stackIndex < stackSize; stackIndex++) {
            try {
                takingThreads[stackIndex].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Barbolosi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        DicomUtils.saveImages(imagesKi, "tmp\\Barbolosi_ImagesKi\\");
        DicomUtils.saveImages(imagesVb, "tmp\\Barbolosi_ImagesVb\\");
        System.out.println("*************FIN TIRAGES ALEATOIRES IMAGES**************");
    }
    
    /**
     * Fonction f(x,y)
     * @param drawingIndex
     * @param FDG
     * @param x
     * @param y
     * @return 
     */
    private double f(int drawingIndex, double[] FDG, double x, double y) {
        double result = 0;
        for (int takingIndex = 0; takingIndex < NB_TAKINGS; takingIndex++) {
            double v = x*AUC[takingIndex] + y*CbValues[takingIndex][drawingIndex] - FDG[takingIndex];
            result += v*v;
        }
        
        return result;
    }
    
   
    
}
