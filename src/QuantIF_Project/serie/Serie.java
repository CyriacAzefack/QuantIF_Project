/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.AortaResults;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.serie.Block;
import ij.gui.Roi;
import java.util.Date;

/**
 *
 * @author Cyriac
 */
public interface Serie {
    /**
     * @return la largeur des images
     */
    public int getWidth();
    
    /**
     *  
     * @return la hauteur des images
     */
    public int getHeight();
    
    /**
     * 
     * @param blockIndex index du block
     * @return le nombre d'images pour un bloc donné (frame ou bodyBlock)
     * @throws QuantIF_Project.patient.exceptions.BadParametersException
     */
    public int getNbImages(int blockIndex) throws BadParametersException;
    
    /**
     
     * @return le nombre de blocks (frame ou bodyBlock)
     */
    public int getNbBlocks();
    
    /**
     * 
     * @return l'unité de la valeur du pixel
     */
    public String getPixelUnity();
    
    /**
       
     * @return le chemin du dossier où sont les images
     */
    public String getSeriePath();
    
    /**
     * 
     * @param index index du block
     * @return le block à cet index
     * @throws QuantIF_Project.patient.exceptions.BadParametersException Si l'index est invalide
     * 
     */
    public Block getBlock(int index) throws BadParametersException ;
    
    /**
     * 
     * @return résultats liés à la ROI déssinnée
     */
    public AortaResults getAortaResults();
    
    /**
     * 
     * @return Le poids du patient en <b>Kg</b>
     */
    public int getPatientWeight();
    
    /**
     * 
     * @return La taille du patient en <b>cm</b>
     */
    public int getPatientHeight();
    
    /**
     * 
     * @return Date de début d'acquisition de la série
     */
    public Date getSerieStartDate();
    /**
     * Dessiner la roi et faire les calculs dessus
     * @param roi roi sur lequel on fait les calculs. Peut prendre la valeur "null"
     * @param startIndex 
     * @param endIndex 
     */
    public void selectAorta(Roi roi, int startIndex, int endIndex);
    
    public void setParent(PatientMultiSeries pms);
    
    public float[][] summSlices(int startSlice, int endSlice);
}
