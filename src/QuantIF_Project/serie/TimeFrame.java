/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.ImageSizeException;
import QuantIF_Project.patient.exceptions.TimeFrameOverflowException;
import com.pixelmed.dicom.TagFromName;

/**
 * Une TimeFrame répresente un ensemble d'images ayant le même temps d'acquisition
 * @author Cyriac
 */
public class TimeFrame extends Block implements Comparable<TimeFrame>{
    
    /**
     * Liste des dicom images contenues dans cette startTime frame
     */
    private DicomImage[] dicomImages;
    
    /**
     * Nb d'images max contenues dans cette coupe
     */
    
    private int nbMaxDicomImages;
        
    /**
     * largeur des images
     */
    private int width;
    
    /**
     * hauteur des images
     */
    private int height;
    
    /**
     * temps en secondes de début d'acquisitionn de cette frame
     */
    private double startTime;
    
    /**
     * temps en secondes de fin d'acquisitionn de cette frame
     */
    private double endTime;
    
    /**
     * Heure d'acquisition de cette frame
     */
    private String acquisitionTime;
    
    /**
     * On crée une TimeFrame vide 
     * @param nbMaxDicomImages nombre de DicomImages max que devra contenir cette TimeFrame
     * @param acquisitionTime heure d'acquisition
     * @param width largeur des images
     * @param height hauteur des images
     * @throws BadParametersException
     *      Levée quand :
     *      - nbDicomImages inférieur à 0
     *      - le temps d'acquisition est nul
     *      - La largeur ou la hauteur de l'image est inférieur à 0
     */
    public TimeFrame(int nbMaxDicomImages, String acquisitionTime, int width, int height) throws BadParametersException {
        super(width, height);
        //On vérifie les paramètres
        if (nbMaxDicomImages < 0)
            throw new BadParametersException("Le nombre d'images dans une Coupe Temporelle doit être supérieur à 0");
        
        if (acquisitionTime.isEmpty())
            throw new BadParametersException("L'heure d'acquisition d'une coupe temporelle ne peut être vide");
        
        this.acquisitionTime = acquisitionTime;
        
        this.nbMaxDicomImages = nbMaxDicomImages;
        this.dicomImages = new DicomImage[this.nbMaxDicomImages];
        
        this.width = width;
        this.height = height;
    }
    
    /**
     * Renvoie le temps moyen de l'acquisition en secondes
     */
    public double getMidTime() {
        return (this.startTime + this.endTime)/2;
    }
    
    public double getStartTime() {
        return this.startTime;
    } 
    
    public double getEndTime() {
        return this.endTime;
    }
    /**
     * Ajoute un DiccomImage à cette coupe temporelle
     * @param di DicomImage à ajouter
     * @throws BadParametersException *
     *      Si le dicomImage est null
     * @throws ImageSizeException
     *      Si les dimensions de l'image à ajouté ne corresponds pas à ceux de la coupe
     * @throws TimeFrameOverflowException
     *      Si on dépasse la taille prévue pour cette startTime frame
     */
    public void addDicomImage(DicomImage di) throws BadParametersException, ImageSizeException, TimeFrameOverflowException {
        if (di == null)
             throw new BadParametersException("Vous ne pouvez pas ajouter une image dicom null");
        
        //on vérifie si les dimensions de l'image correspondent avec ceux de la coupe
        if (di.getWidth() != this.width || di.getHeight() != this.height) 
            throw new ImageSizeException("Les dimensions de l'image Dicom : " 
                    + di.getWidth() + "x" + di.getHeight() + " ne correspondent pas à ceux acceptées par la coupe : " +
                    this.width + "x" + this.height );
        int imageIndex;
        if (this.nbMaxDicomImages != 0) {
            imageIndex = di.getImageIndex()%this.nbMaxDicomImages - 1;
            if (imageIndex == -1)
                imageIndex = this.nbMaxDicomImages - 1;
                
        }
        else {
            imageIndex = di.getImageIndex();
        }
        if (this.dicomImages[imageIndex] != null)
            throw new TimeFrameOverflowException("Depassement de la taille de la TimeFrame");
        
        this.dicomImages[imageIndex] = di;
    }
    
    
    /**
     * Compare deux TimeFrame par  leur temps d'acquisition 
     * 
     * @param tf TimeFrame à comparer
     * @return un nombre négatif si la TimeFrame passée en argument a été acquise après 
     * et un nombre positif sinon.
     */
    @Override
    public int compareTo(TimeFrame tf) {
        if (this.startTime > tf.getMidTime())
            return 1;
        else if (this.startTime < tf.getMidTime())
            return -1;
        
        return 0;
    }
    
    @Override
    public DicomImage getDicomImage(int imageIndex) throws BadParametersException {
        if (imageIndex < 0) 
            throw new BadParametersException("L'indice de l'image doit être supérieur ou égal à 0");
        if (imageIndex >= this.dicomImages.length)
            throw new BadParametersException("Il n'y a pas d'image à cet index. L'index est trop grand!");
        return this.dicomImages[imageIndex];
    }
    
    @Override
    public int size() {
        return this.dicomImages.length;
    }
    
    /**
     * Défini les temps de début et de fin de la frame en secondes
     */
    public void setTime() {
        this.startTime = Double.valueOf(this.dicomImages[0].getAttribute(TagFromName.FrameReferenceTime))/1000;
        this.endTime = this.startTime + Double.valueOf(this.dicomImages[0].getAttribute(TagFromName.ActualFrameDuration))/1000;
    }

    @Override
    public String getAcquisitionTime() {
        return this.acquisitionTime;
    }
    
}
