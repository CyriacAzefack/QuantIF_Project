/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuantIF_Project.utils;

import QuantIF_Project.patient.exceptions.BadParametersException;
import com.pixelmed.dicom.DicomFileUtilities;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
      * Affiche les images passés en paramètres
      * @param buffs Tableau de bufferedImages
     * @throws QuantIF_Project.patient.exceptions.BadParametersException 
     *      la liste d'images doit être non vide
      */
    public static void showImages(BufferedImage[] buffs) throws BadParametersException {
        if (buffs.length < 0)
            throw new BadParametersException("La liste d'images doit être non vide");
        ImageStack imgStack = new ImageStack(buffs[0].getWidth(), buffs[0].getHeight(), null);

        ImageProcessor imgProc;
        ImagePlus imp;

        
        //On parcout la liste des images pour les chargés dans le stack
        for (BufferedImage buff : buffs) {

            ImagePlus impTemp = new ImagePlus("", buff);

            imgProc = impTemp.getProcessor();
            imgStack.addSlice(imgProc);
        }

        imp = new ImagePlus("", imgStack);
        
        imp.show();
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
     * transfome un tableau de pixels en bufferedImage
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
    
    public static double getMinutesBetweenDicomDates(String early, String late) {
        //Exemple de String : 082804.406005 
        //format : hhmmss.frac
        Date earlyDate = null;
        Date lateDate = null;
        double minutes = 0;
        DateFormat df = new SimpleDateFormat("HHmmss.SSS");
        
        try {
            earlyDate = df.parse(early.substring(0, early.indexOf(".") + 3)); //+3 parceque on garde les milliseconds
            lateDate = df.parse(late.substring(0, late.indexOf(".") + 3));

            minutes = (lateDate.getTime() - earlyDate.getTime()) / (double)(60 * 1000);
        } catch (ParseException ex) {
            Logger.getLogger(DicomUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("Early : " + earlyDate);
        //System.out.println("Late : " + lateDate);
        
        return minutes;
    }
    
    public static double getSecondesBetweenDicomDates(String early, String late) {
        //Exemple de String : 082804.406005 
        //format : hhmmss.frac
        Date earlyDate ;
        Date lateDate;
        double secondes = 0;
        DateFormat df = new SimpleDateFormat("HHmmss");
        
        try {
            earlyDate = df.parse(early.substring(0, early.indexOf(".") + 3));//+3 parceque on garde les milliseconds
            lateDate = df.parse(late.substring(0, late.indexOf(".") + 3));

            secondes = (lateDate.getTime() - earlyDate.getTime())/ (double)1000;
        } catch (ParseException ex) {
            Logger.getLogger(DicomUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("Early : " + earlyDate);
        //System.out.println("Late : " + lateDate);
        
        return secondes;
    }
    
}
