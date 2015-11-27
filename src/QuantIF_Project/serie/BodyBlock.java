/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.exceptions.BadParametersException;
import com.pixelmed.dicom.TagFromName;
import java.util.ArrayList;

/**
 *
 * @author Cyriac
 */
public class BodyBlock extends Block{
    /**
     * Liste des images de ce block
     */
    private final ArrayList<DicomImage> dicomImages;
    
    private final int acquisitionNumber;
    private double startTime;
    private double endTime;
    
    public BodyBlock(int acquisitionNumber, int width, int height) throws BadParametersException {
        super (width, height);
        this.dicomImages = new ArrayList<>();
        this.acquisitionNumber = acquisitionNumber;
    }
    
    public void addDicomImage(DicomImage di) {
        dicomImages.add(di);
    }

    @Override
    public int size() {
        return this.dicomImages.size();
    }

    @Override
    public DicomImage getDicomImage(int dicomIndex) {
        return this.dicomImages.get(dicomIndex);
    }
    
    public void setTime() {
        this.startTime = Double.valueOf(this.dicomImages.get(0).getAttribute(TagFromName.FrameReferenceTime))/1000;
        this.endTime = this.startTime + Double.valueOf(this.dicomImages.get(0).getAttribute(TagFromName.ActualFrameDuration))/1000;
    }
    
    /**
     * Renvoie le temps moyen de l'acquisition en secondes
     */
    public double getMidTime() {
        return (this.startTime + this.endTime)/2;
    }
    
    /**
     * Renvoie le temps de début d'acquisition en secondes
     */
    public double getStartTime() {
        return this.startTime;
    } 
    
    /**
     * Renvoie le temps de fin d'acquisition en secondes
     */
    public double getEndTime() {
        return this.endTime;
    }
}
