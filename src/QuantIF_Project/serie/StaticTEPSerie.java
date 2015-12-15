/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel de Rouen"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.NonStaticSerieException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;

/**
 *
 * @author Cyriac
 */
public class StaticTEPSerie extends TEPSerie{

    public StaticTEPSerie(String dirPath) throws NotDirectoryException, DicomFilesNotFoundException, BadParametersException, NonStaticSerieException {
        super(dirPath);
        checkSerieIsStatic();
        
    }
    
    /*
    @Override
    public int getNbBlocks() {
        return 1;
    }
    */
    /**
     * Renvoie le nombre d'images de la série statique
     *  
     */
    public int getNbImages() {
        return this.timeFrames.get(0).size();
    }
    
    public DicomImage getImage(int imageIndex) throws BadParametersException {
        return this.getBlock(0).getDicomImage(imageIndex);
    }
    
    @Override
    public String toString() {
            String str = "Présentation de la Serie TEP Statique" + this.id + "\n";
            if(name == null)
                name = "N/A";
            if(sex == null)
                sex = "N/A";
            if(age == null)
                age = "N/A";
            if(weight == null)
                weight = "N/A";
            str += "-Nom : " + this.name + "\n";
            str += "-Sexe : " + this.sex + "\n";
            str += "-Age : " + this.age + " ans\n";
            str += "-Poids : " + this.weight + " Kg\n";

            return str;
    }
    
    /**
     * On vérifie que la série est une série Statique
     */
    private void checkSerieIsStatic() throws NonStaticSerieException {
        if (((TEPSerie)this).getNbBlocks() > 1) 
            throw new NonStaticSerieException("La série " + this.getName() + " n'est pas une série statique.");
    }
    
}
