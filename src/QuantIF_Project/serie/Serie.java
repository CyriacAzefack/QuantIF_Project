/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuanttIF of the "Centre Henri Becquerel"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.AortaResults;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import ij.gui.Roi;
import java.util.Date;

/**
 *
 * @author Cyriac
 */
public interface Serie {
    /**
     * Renvoie la largeur des images
     */
    public int getWidth();
    
    /**
     * Renvoie la hauteur des images
     */
    public int getHeight();
    
    /**
     * Renvoie le nombre d'images pour un bloc donné (frame ou bodyBlock)
     * @param blockIndex index du block
     * @throws QuantIF_Project.patient.exceptions.BadParametersException
     */
    public int getNbImages(int blockIndex) throws BadParametersException;
    
    /**
     * Renvoie le nombre de blocks (frame ou bodyBlock)
     *  
     */
    public int getNbBlocks();
    
    /**
     * Retourne l'unité de la valeur du pixel 
     */
    public String getPixelUnity();
    
    /**
     * 
     * @param index index du block
     * @return le block à cet index
     * @throws QuantIF_Project.patient.exceptions.BadParametersException Si l'index est invalide
     * 
     */
    public Block getBlock(int index) throws BadParametersException ;
    
    /**
     * Calcule et retourne les résultats liés à la ROI déssinnée
     */
    public AortaResults getAortaResults();
    
    /**
     * Date de début d'acquisition de la série
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
