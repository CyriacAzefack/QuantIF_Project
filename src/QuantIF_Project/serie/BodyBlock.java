/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuanttIF of the "Centre Henri Becquerel"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.DicomImage;
import QuantIF_Project.patient.exceptions.BadParametersException;
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
}
