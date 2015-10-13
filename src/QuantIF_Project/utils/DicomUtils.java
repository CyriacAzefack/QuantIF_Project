/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuantIF_Project.utils;

import com.pixelmed.dicom.DicomFileUtilities;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage;
import java.io.File;

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
      * @param buffs 
      */
     public static void showImages(BufferedImage[] buffs) {
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
    public static Date convertDateTagToDate(AttributeList list) {
        //Exemple de String : 082804.406005 
        //format : hhmmss.frac
        Date date  = null;
        DateFormat df = new SimpleDateFormat("HHmmss");
        
         try {
             date = df.parse(dateString.substring(0, dateString.indexOf(".")));
             //On extrait les différents champ
         } catch (ParseException ex) {
             Logger.getLogger(DicomUtils.class.getName()).log(Level.SEVERE, null, ex);
         }
        
         return date;

    }
    **/
}
