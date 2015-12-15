/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.process;

import QuantIF_Project.gui.PatientSerieViewer;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.serie.TEPSerie;
import QuantIF_Project.utils.DicomUtils;
import QuantIF_Project.utils.MathUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import ij.util.Tools;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**                   
 * Implémentation de la méthode de Hunter.
 * On utilise comme images de départ la somme totale sur la série dynamique de fin.
 * @author Cyriac
 */
public class Hunter {
    
    /**
     * Valeur de concentration du prélèvement tardif
     */
    private double takingValue;
    
    /**
     * Heure du prélèvement tardif en <b>min</b>
     */
    private double takingTime;
    
    /**
     * Constante b1 de la courbe artérielle en min<sup>-1</sup>
     */
    private  double b1 = 9.33;
    
    /**
     * Constante b2 de la courbe artérielle en min<sup>-1</sup>
     */
    private  double b2 = 0.289;
   
    /**
     * Constante b3 de la courbe artérielle en min<sup>-1</sup>
     */
    private double b3 = 0.0125;
    
    /**
     * Intervalle de variation de b1 en min<sup>-1</sup>
     */
    private final static double b1Var = 0.92;
    
    /**
     * Intervalle de variation de b2 en min<sup>-1</sup>
     */
    private final static double b2Var = 0.022;
   
    /**
     * Intervalle de variation de b3 en min<sup>-1</sup>
     */
    private final static double b3Var = 0.0007;
    
    /**
     * Constante de la courbe artérielle
     */
    private final double A1, A2, A3;
    
    /**
     * Serie Dynamique de fin
     */
    private final TEPSerie serie;
    
    /**
     * Images KH
     */
    private final FloatProcessor[] khImages;
    
    /**
     * Images de la série
     */
    private final FloatProcessor[] patientImages;
    
    /**
     * Dimension des images
     */
    private final int width, height;
    
    /**
     * Nombre d'images à traiter
     */
    private final int nbImages;
    
    /**
     * On annule le déroulement du calcul
     */
    private boolean cancelled;
    
    
    
    /**
     * On lance la méthode de hunter sur l'acquisition en cours.
     * 
     * <b>Cb(t) = A1*exp(-b1*t) + A2*exp(-b2*t) + A3*exp(-b3*t) </b>  
     * @param pms Acquisition multiple
     */
    public Hunter(PatientMultiSeries pms, double takingValue, double takingTime)  {
        
        this.serie = pms.getEndDynSerie();
        
        this.cancelled = false;
        
        System.out.println("************DEBUT HUNTER*************");
       //On instancie les constantes
        Random rand = new Random();
        b1 += -b1Var + (2*b1Var)*rand.nextDouble();
        System.out.println("b1 = " + b1 + " min^-1");
        b2 += -b2Var + (2*b2Var)*rand.nextDouble();
        System.out.println("b2 = " + b2 + " min^-1");
        b3 += -b3Var + (2*b3Var)*rand.nextDouble();
        System.out.println("b3 = " + b3 + " min^-1");
        
        double dose = serie.getPatientInjectedDose(); //Mbq
        
        System.out.println("Dose injectée au patient  = " + dose + " Mbq");
        
        //lean body mass  
        double lbm = 0; //kg
        double weight = serie.getPatientWeight(); //Kg
        
        weight = 75;
        System.out.println("Poids du patient : " + weight + " Kg");
        
        double patientHeight = serie.getPatientHeight(); //cm
        patientHeight = 180;
        System.out.println("Taille du patient : " + patientHeight + " cm");
        
        if (serie.isAMale()) {
            lbm = (1.1 * weight) - 128 * Math.pow((weight/patientHeight),2);
        } else {
            lbm = (1.07 * weight) - 148 * (weight/patientHeight);
        }
        
        System.out.println("Masse maigre = " + lbm + " Kg");
        double bloodVolume = 70 * lbm;
        
        System.out.println("Volume sanguin = " + bloodVolume + " mL");
        
        A1 = dose/bloodVolume;
        A2 = A1;
        
        System.out.println("A1 = A2 = " + A1 + " Mbq/mL");
        
        
        
        width = serie.getWidth();
        
        height = serie.getHeight();
        
        nbImages = serie.getNbImages();
        
        
        patientImages = new FloatProcessor[nbImages];
        khImages = new FloatProcessor[nbImages];
        
        this.takingTime = 0;
        this.takingValue = 0;
        //on recupere les valeurs de prélèvement tardif
        if (takingValue == 0 && takingTime == 0)
            getLateTaking(pms);
        else {
            this.takingValue = takingValue;
            this.takingTime = takingTime;
        }
        
        //Une fois qu'on a les données du prélèvement tardif, on calcule A3
        
        A3 = (takingValue - (A1*Math.exp(-b1*takingTime) + A2*Math.exp(-b2*takingTime)))/(Math.exp(-b3*takingTime));
        
        System.out.println("A3 = " + A3 + " MBq/mL");
        
        if (!cancelled) {
            getPatientImages();


            buildKhImages();

            displayAndSaveKhImages();
        }
        System.out.println("************FIN HUNTER*************");
    }
    
    /**
     * On récupère les valeurs de prélèvement tardif
     */
    private void getLateTaking(PatientMultiSeries pms) {
        
        //On récupère l'heure et la valeur du prélèvement
        String timeStr;
        double takingVal;
        //On recommence tant que les donénes rentrées ne sont pas valable
        
        Matcher m;
        
        String timePattern = "[0-9]{6}";
            Pattern pat = Pattern.compile(timePattern);
            
            JPanel subPanel = new JPanel();
            JPanel panel = new JPanel();
            
            InputDataField hour = new InputDataField(2, "[0-1][0-9]|2[0-3]");
            
            InputDataField minutes = new InputDataField(2, "[0-5][0-9]");
            InputDataField secondes = new InputDataField(2, "[0-5][0-9]");
            InputDataField value = new InputDataField(8, "[0-9]+");
            
            //subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.LINE_AXIS));
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
            panel.add(new JLabel("Entrez l'heure du prélèvement :"));
            subPanel.add(hour);
            subPanel.add(new JLabel("h "));
            subPanel.add(minutes);
            subPanel.add(new JLabel("mn "));
            subPanel.add(secondes);
            subPanel.add(new JLabel("s "));
            panel.add(subPanel);
            panel.add(new JLabel("Entrez la valeur du prélèvement en MBq : "));
            panel.add(value);
        do {
            
            int showConfirmDialog = JOptionPane.showConfirmDialog(null, panel, "Données du prélèvement ", JOptionPane.DEFAULT_OPTION);
            
            if (showConfirmDialog == JOptionPane.CLOSED_OPTION) {
                cancelled = true;
                System.out.println("HUNTER CANCELLED");
            }
            timeStr = hour.getText()+ minutes.getText() + secondes.getText();
            
            takingVal = Tools.parseDouble(value.getText());
            
            
            m = pat.matcher(timeStr);
            
            
        } while ((!m.matches() || (takingVal == Double.NaN)) && !cancelled);
        
        if (!cancelled) {
            timeStr += ".000000"; //On ajotue les fractions de secondes pour avoir le format dicom
            TEPSerie startDyn = pms.getStartDynSerie();

            this.takingTime = DicomUtils.getMinutesBetweenDicomDates(startDyn.getSeriesTime(), timeStr);    
            this.takingValue = takingVal;
            System.out.println("Temps après l'injection = " + (int)takingTime + " min");


            System.out.println("Concentration prélévée = " + takingValue + "MBq");
        }
       
    }
    
    
    /**
     * On construit les images kh
     */
    private void buildKhImages() {
        
        double integral = getHunterIntegral();
        for (int imageIndex = 0; imageIndex < nbImages; imageIndex++) {
            float[] khArray = new float[width * height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    double fdg = patientImages[imageIndex].getPixelValue(x, y);
                    khArray[y*width + x] = (float) (fdg/integral);
                    
                }
            }
            khImages[imageIndex] = new FloatProcessor(width, height, khArray);
            
        }
    }
    
    /**
     * Renvoie &int<sup>T</sup><sub>0</sub> Cb(t)d&tau
     * @return 
     */
    private double getHunterIntegral() {
        
        System.out.println("On récupère l'intégrale sous la courbe");
        
        int N = 100; //nombres de divisions
        double[] x = new double[N];
        double[] y = new double[N];
        
        for (int i = 0; i < N; i++) {
            if (i > 0)
                x[i] = x[i-1] + takingTime/N;
            y[i] = cb(x[i]);
        }
        
        double result = Double.NaN;
        try {
            result = MathUtils.AreaUnderTheCurve(x, y);
        } catch (BadParametersException ex) {
            Logger.getLogger(Hunter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
    
    /**
     * Renvoie  Cb(t) = A<sub>1</sub>e<sup>(-b<sub>1</sub>*t)</sup> + A<sub>2</sub>e<sup>(-b<sub>2</sub>*t)</sup> + A<sub>3</sub>e<sup>(-b<sub>3</sub>*t)</sup>
     * @param t temps en min
     * @return 
     */
    public double cb(double t) {
        return A1*Math.exp(-b1*t) + A2*Math.exp(-b2*t) + A3*Math.exp(-b3*t);
    }
    
    /**
     * On récupère les images de la série 
     */
    private void getPatientImages() {
        BufferedImage[] summALL = serie.getSummALL();
        
        for (int i = 0; i < patientImages.length; i++) {
            float[] fArray  = new float[width*height];
            
            fArray = summALL[i].getRaster().getPixels(0, 0, width, height, fArray);
            patientImages[i] = new FloatProcessor(width, height, fArray);
        }
    }
    
    /**
     * On sauvegarde et on affiche les images des Ki
     */
    private void displayAndSaveKhImages() {
        String dirPath = "tmp\\imagesKh";
        File file = new File(dirPath);
        //On vide le dossier
        DicomUtils.emptyDirectory(file);
        ImagePlus imp;
        ImageStack is = new ImageStack(width, height);
        BufferedImage[] buffs = new BufferedImage[khImages.length];
        for (int i = 0; i < buffs.length; i++) {
            buffs[i] = khImages[i].getBufferedImage();
            is.addSlice(khImages[i]);
            imp = new ImagePlus("image "+i, khImages[i]);
            //On sauvegarde les images ki
            IJ.save(imp, file.getAbsolutePath()+"\\IM"+i);
        }
        
        System.out.println("Images des Kh sauvegardées dans le dossier \"" 
                + file.getAbsolutePath() + "\"");
        
       PatientSerieViewer.setDisplayedImage(buffs, "Kh Images");
       
      
      
       
    }
}
