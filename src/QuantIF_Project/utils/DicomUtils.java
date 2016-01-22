/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.utils;

import QuantIF_Project.gui.Main_Window;
import QuantIF_Project.gui.PatientSerieViewer;
import QuantIF_Project.serie.DicomImage;
import QuantIF_Project.serie.TEPSerie;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import QuantIF_Project.serie.TimeFrame;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomFileUtilities;
import com.pixelmed.dicom.OtherFloatAttribute;
import com.pixelmed.dicom.TagFromName;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.LutLoader;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.LUT;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cyriac
 */
public class DicomUtils {
    /**
     * Renvoie true si le fichier est un fichier DICOM
     * @param file fichier à vérifier
     * @return true si c'est un fichier DICOM et false sinon
     */
    public static boolean isADicomFile(File file) {
         return DicomFileUtilities.isDicomOrAcrNemaFile(file);
    }
     
     
   
    
    /**
     * Sauvegarde le tableau d'image dans le format TIFF
     * @param buffs tableau de BufferedImage
     * @param filepath chemin du fichier
     * @throws BadParametersException 
     *      Le tableau doit être non vide
     */
    public static void saveImagesAsTiff(BufferedImage[] buffs, String filepath) throws BadParametersException {
        if (buffs.length < 0)
            throw new BadParametersException("La liste d'images doit être non vide");
        int width = buffs[0].getWidth();
        int height = buffs[0].getHeight();
        
        ImageStack stack = new ImageStack(width, height, null);
        ImageProcessor proc;
        ImagePlus imp;
        
        for (BufferedImage buff : buffs) {
            ImagePlus impTemp = new ImagePlus("", buff);
            proc = impTemp.getProcessor();
            stack.addSlice(proc);
        }
        imp = new ImagePlus("", stack);
        IJ.saveAs(imp, "tiff", filepath);
    }
    
    /**
     * Transfome un tableau de pixels en bufferedImage (pour l'affichage, valeurs pixels [0-255])
     * @param width 
     * @param height
     * @param pixels
     * @return BufferedImage 
     */
    public static BufferedImage pixelsToBufferedImage(int width, int height, float[] pixels) {
         FloatProcessor fp = new FloatProcessor(width, height , pixels );
         return fp.getBufferedImage();
    }
    
    /**
     * Vide un repertoire de ses fichiers
     * @param folder Dossier parent
     */
    public static void emptyDirectory(File folder){
        for(File file : folder.listFiles()){
           if(file.isDirectory()){
               emptyDirectory(file);
           }
            file.delete();
        }
        System.out.println("Dossier \"" + folder.getAbsolutePath() +"\" vidé");
    }
    
    /**
     * Calcule le nombre de minutes entre les deux dates.
     * Si la date de fin est plustôt que celle de début, on suppose que c'est la date de la journée suivante
     * @param early date de début
     * @param late date de fin
     * @return 
     */
    public static double getMinutesBetweenDicomDates(String early, String late) {
        //Exemple de String : 082804.406005 
        //format : hhmmss.frac
        Date earlyDate = null;
        Date lateDate = null;
        double minutes = 0;
        DateFormat df = new SimpleDateFormat("HHmmss");
        
        try {
            earlyDate = df.parse(early.substring(0, 6));
            lateDate = df.parse(late.substring(0, 6));
            
            if (earlyDate.before(lateDate)) {
                minutes = (lateDate.getTime() - earlyDate.getTime()) / (double)(60 * 1000);
            }
            else {
                minutes = (lateDate.getTime() + 24*60*60*1000 - earlyDate.getTime()) / (double)(60 * 1000); // on ajoute une journée de différence
            }
        } catch (ParseException ex) {
            Logger.getLogger(DicomUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("Early : " + earlyDate);
        //System.out.println("Late : " + lateDate);
        
        return minutes;
    }
    
     /**
     * Calcule le nombre de secondes entre les deux dates.
     * Si la date de fin est plus tôt que celle de début, on suppose que c'est la date de la journée suivante
     * @param early date de début
     * @param late date de fin
     * @return 
     */
    public static double getSecondesBetweenDicomDates(String early, String late) {
        //Exemple de String : 082804.406005 
        //format : hhmmss.frac
        Date earlyDate ;
        Date lateDate;
        double secondes = 0;
        DateFormat df = new SimpleDateFormat("HHmmss");
        
        try {
            earlyDate = df.parse(early.substring(0, 6));;
            lateDate = df.parse(late.substring(0, 6));;

            secondes = (lateDate.getTime() - earlyDate.getTime())/ (double)1000;
        } catch (ParseException ex) {
            Logger.getLogger(DicomUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("Early : " + earlyDate);
        //System.out.println("Late : " + lateDate);
        
        return secondes;
    }
    
    /**
     * Transforme une date Dicom en variable Date
     *
     * @param dicomDate date dicom
     * @return 
     */
    
    public static Date dicomDateToDate(String dicomDate) {
         Date date = null;
    
        if (dicomDate.length() > 13) {
            //Exemple de String : "20150914 082153.484000"
            //format : yyyyMMdd hhmmss.frac

            DateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
            //DateFormat newdf = new SimpleDateFormat("HH:mm:ss");
            try {
                date = df.parse(dicomDate.substring(0, 15));



            } catch (ParseException ex) {
                Logger.getLogger(DicomUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }
        else {
             //Exemple de String : "082153.484000"
            //format : hhmmss.frac

            DateFormat df = new SimpleDateFormat("HHmmss");
            //DateFormat newdf = new SimpleDateFormat("HH:mm:ss");
            try {
                date = df.parse(dicomDate.substring(0, 6));



            } catch (ParseException ex) {
                Logger.getLogger(DicomUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return date;
    }
    
    /**
    * Parcourt le repertoire de fichiers et cree les instances de DicomImage
    * �à l'aide des fichier DICOM et les range dans l'ordre croissant des index des images
    * 
    * @param path Chemin du dossier DICOM
    * @return Une ArrayList de DicomImage : la liste des fichiers 'DICOM' image liés au patient
    * @throws NotDirectoryException 
    * 		Levée quand le chemin fourni ne correspond pas � un repertoire
    * @throws DicomFilesNotFoundException
    * 		Levée quand aucun fichier DICOM n'a été� trouvé�dans le répertoire
    */

   public static ArrayList<DicomImage> checkDicomImages(String path) throws NotDirectoryException, DicomFilesNotFoundException {
       ArrayList<DicomImage> listDI = new ArrayList<>();
       File dir = new File(path);
       if (!dir.isDirectory()) {
               throw new NotDirectoryException("Le chemin '" + path + "' n'est pas un répertoire");
       }
       File[] files = dir.listFiles();

       //On parcourt le dossier de fichiers
       if (files != null) {

           for (File file : files) {

               if (file.isFile()) {
                   // Si c'est un fichier on vérifie si c'est un fichier DICOM

                   if (DicomUtils.isADicomFile(file)) {
                       try {

                           DicomImage dcm = new DicomImage(file);
                           listDI.add(dcm);



                       } catch (DicomException | IOException ex) {
                           Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
                       }
                   }
               }
           }

       }


        //On vérifie si la liste n'est pas vide
        if (listDI.isEmpty()) {
                throw new DicomFilesNotFoundException("Aucun fichier DICOM n'a été trouvé dans ce repertoire");
        }
        //On range  les dicomImages
        Collections.sort(listDI);

        System.out.println(listDI.size() + " images DICOM détectées!!");
        
        return listDI;
   }
   
   /**
    * Crée une série TEP d'une seule frame contenant la somme de toutes les frames
    * de cette série TEP.
    * 
    * @param serie Série TEP
    * @param dirPath Chemin du répertoire dans lequel sera sauvé la nouvelle série TEP
    */
   public static void createStaticTEPSerie(TEPSerie serie, String dirPath) {
        try {
            AttributeList list;
            TimeFrame tf;
            DicomImage dcm ;
            OtherFloatAttribute ofa = new OtherFloatAttribute(TagFromName.PixelData);
           
            String filePath;
            String transferSyntaxUID = "1.2.840.10008.1.2"; //Implicit VR Endian: Default Transfer Syntax for DICOM
            BufferedImage[] buffSumm = serie.getBuffSummALL();
            
            //On vide le dossier de sortie
            DicomUtils.emptyDirectory(new File(dirPath));
            
            //On crée un sous repertoire
           
            
            //On récupére la frame pr les méta données
            tf = serie.getBlock(1);
            
            for (int imageIndex = 0; imageIndex < tf.size(); imageIndex++) {
                dcm = tf.getDicomImage(imageIndex);
               
                if (dcm != null) {
                    BufferedImage b = buffSumm[imageIndex];
                    File f = new File(dirPath + "\\IM."+(imageIndex+1));


                    list = dcm.getAttributeList();

                    //On remplace le nombre de time frame dans l'entete des nouveaus images dicom
                    list.replaceWithValueIfPresent(TagFromName.NumberOfTimeSlices, Integer.toString(0));

                   

                   
                    ofa.setValues((float[]) dcm.getImageProcessor().getPixels());
                    list.replace(TagFromName.PixelData, ofa);
                    list.write(f, transferSyntaxUID, false, true);

                }
            }
        } catch (BadParametersException | DicomException | IOException ex) {
            Logger.getLogger(DicomUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   
   /**
     * Sauvegarde les images
     * @param images (ImageProcessor valeur réelle du pixel)
     * @param dirPath (crée le dossier si il n'existe pas et le vide avant de sauver les images)
     */
    public static void saveImages(ImageProcessor[] images, String dirPath) {
        
        dirPath = Main_Window.outputDir() + dirPath;
        ImageStack is = new ImageStack(images[0].getWidth(), images[0].getHeight());
        File file = new File(dirPath);
        file.mkdirs();
        //On vide le dossier
        DicomUtils.emptyDirectory(file);
        for (int i = 0; i < images.length; i++) {
            ImageProcessor image = images[i];
            is.addSlice(image);
            ImagePlus imp = new ImagePlus("image "+i, image);
            //On sauvegarde les images ki
            IJ.save(imp, file.getAbsolutePath()+"\\IM"+i);
        }
        
        ImagePlus imp = new ImagePlus("", is);
    }
    
    /**
     * On affiche les images
     * @param images images à afficher
     * @param title Titre des images
     */
    public static void display(ImageProcessor[] images, String title) {

       BufferedImage[] buffs = new BufferedImage[images.length];
       for (int i = 0; i < images.length; i++) {
           buffs[i] = images[i].getBufferedImage();
       }

       PatientSerieViewer.setDisplayedImage(buffs, title);
    }
    
    /**
     * On affiche les images
     * @param images1
     * @param images2
     * @param title Titre des images
     */
    public static void display(ImageProcessor[] images1, ImageProcessor[] images2, String title) {

       BufferedImage[] buffs1 = new BufferedImage[images1.length];
       BufferedImage[] buffs2 = new BufferedImage[images2.length];
       for (int i = 0; i < images1.length; i++) {
           buffs1[i] = images1[i].getBufferedImage();
           buffs2[i] = images2[i].getBufferedImage();
       }

       PatientSerieViewer.setDisplayedImage(buffs1, buffs2, title);
    }
    
    
    
     /**
     * Dessine une image sur une autre avec les poids respectifs f1 et f2
     * @param mainImage
     * @param secondImage
     * @param alpha Poids de l'image principale ( &lt; ou = à 1)
     * @param beta Poids de l'image secondaire ( &lt; ou = à 1)
     * @return une nouvelle image contenant les deux images de départ
     */
    public static ImageProcessor drawImageOnImage(FloatProcessor mainImage, FloatProcessor secondImage, float alpha, float beta) {
        
        int width = mainImage.getWidth();
        int height = mainImage.getHeight();
        
        FloatProcessor result = new FloatProcessor(width, height);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float spix = secondImage.getf(x, y);
                
                if (spix == 0) {
                    float mpix = mainImage.getf(x, y);
                    result.setf(x, y, mpix*alpha);
                }
                
                else {
                    result.setf(x, y, spix*beta);
                }
            }
        }
        
        
       
        
        return result;
    }
   
}
