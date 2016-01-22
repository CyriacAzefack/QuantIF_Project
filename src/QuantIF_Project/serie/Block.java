/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.exceptions.BadParametersException;

/**
 *
 * @author Cyriac
 */
public abstract class Block {
    protected int width;
    
    protected int height;
    
    protected Block(int width, int height) throws BadParametersException {
        if (width < 0 || height < 0 ) 
            throw new BadParametersException("Les dimensions des images d'une coupe temporelle doivent être supérieur à 0");
        
        this.width = width;
        this.height = height;
    }
    
    /**
     * Renvoie le nombre d'images dans le block
     *
     */
    public abstract int size();
    
    /**
     * Renvoie l'image dicom situé à cette indice
     * @param dicomIndex indice de l'image DICOM
     * @return 
     * @throws QuantIF_Project.patient.exceptions.BadParametersException 
     *      Levée si l'indice est invalide
     * 
     *  
     */
    public abstract DicomImage getDicomImage(int dicomIndex) throws BadParametersException;
    
    /**
     * Retourne l'heure d'acquisition de ce block
     * @return 
     * 
     */
    public abstract String getAcquisitionTime();
    
    /**
     * 
     * @return Le temps de début d'acquisition en secondes
     */
    public abstract double getStartTime();
    
    /**
     * 
     * @return Le temps de fin d'acquisition en secondes
     */
    public abstract double getEndTime();
    
    /**
     * 
     * @return Le temps moyen d'acquisition en secondes
     */
    public abstract double getMidTime();
}
