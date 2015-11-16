/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuanttIF of the "Centre Henri Becquerel"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.DicomImage;
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
     * @param dicomIndex indice de l'image dicom
     * @throws QuantIF_Project.patient.exceptions.BadParametersException 
     *      Levée si l'indice est invalide
     * 
     *  
     */
    public abstract DicomImage getDicomImage(int dicomIndex) throws BadParametersException;
}
