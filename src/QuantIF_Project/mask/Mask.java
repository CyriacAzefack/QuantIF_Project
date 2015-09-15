/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuantIF_Project.mask;

import QuantIF_Project.patient.Patient;
import QuantIF_Project.patient.exceptions.BadMaskStructException;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author kamelya
 */
public class Mask {
    /**
     * Chemin vers le dossier du masque ROI
     */
    private String path;
    
    /**
     * Patient correspondant au masque
     */
    private Patient patient; 
    
    /**
     * Liste des images des masques
     */
    private ArrayList<BufferedImage> maskImages;
    
    /**
     * Liste des images du patient avec le ROI
     */
     private ArrayList<BufferedImage> imagesWithROI;
     
    /**
     * largeur de l'image du masque
     */
    private int width;
    
    /**
     * Hauteur de l'image du masque
     */
    private int height;
    
    private JFrame frame;
            
    /**
     * Cree un masque pour une ROI prédéfinie
     * @param patient
     * @param maskDirPath
     * @throws BadParametersException 
     *      Levée quand le patient ou le chemin du dossier du masque est invalide
     *      Ou quand le
     * @throws NotDirectoryException 
     *      Levée quand le chemin fourni n'est pas un dossier
     */
    public Mask(Patient patient, String maskDirPath) throws BadParametersException, NotDirectoryException, BadMaskStructException {
        if (patient == null)
            throw new BadParametersException("Patient invalide'");
        if (maskDirPath == null)
            throw new BadParametersException("chemin vers le dossier du masque invalide'");
        
        this.path = maskDirPath;
        this.patient = patient;
        this.width = patient.getImagesWidth();
        this.height = patient.getImagesHeight();
        
        this.maskImages = getMaskImages(maskDirPath);
        
        if(maskImages.size() < patient.getMaxDicomImage())
            throw new BadMaskStructException("Cette structure de masque ne peut s'appliquer à ce patient car pas assez de fichiers!!");
        
        
        
        
        
        this.imagesWithROI = applyROI();
        
        
        
        
    }
    
    /**
     * Applique le masque à l'image passé en paramètre
     * @param imageIndex
     * @return 
     * @throws QuantIF_Project.patient.exceptions.BadParametersException 
     *      Levée quand l'index est invalide
     */
    public BufferedImage getImageWithROI(int imageIndex) throws BadParametersException {
       
        if (imageIndex < 0)
            throw new BadParametersException("L'index doit être supérieur ou égal à 0");
        if (imageIndex > imagesWithROI.size()) 
            throw new BadParametersException("L'index doit être inférieur au nombre max d'images");
        
        return this.imagesWithROI.get(imageIndex);
    }
    
    /**
     * Parcourt le dossier du masque et crée des BufferedImage qu'on range dans une liste
     * @param dirPath
     * @return ArrayList<BufferedImage>
     * @throws NotDirectoryException 
     *      Levée quand le chemin fourni n'indique pas un répertoire
     */
    private ArrayList<BufferedImage> getMaskImages(String dirPath) throws NotDirectoryException {
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
        File dir = new File(dirPath);
        
        if (!dir.isDirectory())
            throw new NotDirectoryException("Le chemin '" + path + "' n'est pas un répertoire");
	
        File[] files = dir.listFiles();
        //On range les fichiers dans l'ordre croissant de numero
        Arrays.sort(files, new Comparator<File>(){
            @Override
            //Les fichiers d'images sont rangés dans l'ordre inverse des fichiers images patient
            // c-à-d que l'image mask avec le n° 255 correspond à l'image patient 1
            // donc on range les fichiers masque dans maskImages en commencant par la fin
            public int compare(File f1, File f2) {
                int n1 = extractNumber(f1.getName());
                int n2 = extractNumber(f2.getName());
                
                return n2 - n1;
            }
            
            private int extractNumber(String name) {
               int i = 0;
               try {
                   int s = name.lastIndexOf('_') + 1;
                   String number = name.substring(s, name.length());
                   i = Integer.parseInt(number);
               } catch(Exception e) {
                   i = 0; // if filename does not match the format
                          // then default to 0
               }
               return i;
            }
        });
        
        
        
        //On parcourt le dossier de fichiers
        if (files != null) {
            for (File file : files) {
            
                if (file.isFile()) {
                    
                    //Si c'est un fichier on crée l'image
                    
                    //Les fichiers présents dans ce dossier réprésentent
                    //les matrices des masques.
                    
                    BufferedImage bimg = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = bimg.createGraphics();
                    g.dispose();
                    try {
                        List<String> lines = null;
                        lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
                        int index = 0;
                        int[][] data = new int[this.height][this.width];
                        int curRow = 0; //ligne courante dans la matrice
                        int curCol = 0; //colonne courante dans la matrice
                        
                        //On doit enlever les 3 premières lignes composant 
                        //l'en-tête du fichier
                        
                        lines =lines.subList(3, lines.size());
                        
                        for(String line : lines) { //pour chaque ligne
                            //on récupère les valeurs en string en disant qu'ils sont
                            // séparé par un espace
                           String[] values = line.split(" ");
                           for (String val : values) {
                               //On parcout la ligne et on ajoute les valeurs à la matrice
                               
                               data[curRow][curCol] = Integer.parseInt(val);
                               index += 1;
                               //On recalcule la ligne courante 
                               curRow = index/this.width;
                               //On recalcule la colonne courante
                               curCol = index%this.width;
                            }       
                        }
                        
                        //On remplit la matrice de l'image
                        for (int row = 0; row < this.width; ++row) {
                            //swips the columns
                            for (int col = 0; col < this.height; ++col) {   
                                
                               if (data[row][col] == 1) {
                                    bimg.setRGB(col, row, Color.WHITE.getRGB());
                                    
                               }
                               else {
                                   bimg.setRGB(col, row, 0);
                                   
                               }
                            }
                        }
                       
                        
                    } catch (IOException ex) {
                        Logger.getLogger(Mask.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   
                    images.add(bimg);
                }
    
            }
        }
        
        
        return images;
    }
    /**
     * Applique le masque à tous les images du patient
     * @return un arrayList contenant ces nouvelles images
     */
    private ArrayList<BufferedImage> applyROI() {
        ArrayList<BufferedImage> newImages = new ArrayList<BufferedImage>();
        
        for (int i=0; i<patient.getMaxDicomImage(); i++) {
            try {
                newImages.add(applyROItoImage(patient.getDicomImage(i).getBufferedImage(), i));
            } catch (BadParametersException ex) {
                Logger.getLogger(Mask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return newImages;
    }
    
    /**
     * Applique le masque à une image
     * @param bimg
     * @return 
     */
    private BufferedImage applyROItoImage(BufferedImage bimg, int index) throws BadParametersException {
        BufferedImage newImage = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        
             
        
        
        BufferedImage oldImage = this.patient.getDicomImage(index).getBufferedImage();
       
        BufferedImage maskImage = this.maskImages.get(index);
        for (int row = 0; row < this.width; ++row) {
            //swips the columns
            for (int col = 0; col < this.height; ++col) {
                
                if (maskImage.getRGB(col, row) == Color.WHITE.getRGB()) {
                   newImage.setRGB(col, row, Color.RED.getRGB());
                   
                }
                else {
                    newImage.setRGB(col, row, oldImage.getRGB(col, row));
                   
                }
                        
                //newImage.setRGB(col, row, Color.red.get);
            }
        }
        
        
       
        return newImage;
    }
}
