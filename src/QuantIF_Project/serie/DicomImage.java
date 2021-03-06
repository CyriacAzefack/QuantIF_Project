/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.serie;
import QuantIF_Project.utils.DicomUtils;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;
import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.FloatProcessor;
import ij.util.DicomTools;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Ouverture de fichiers DICOM
 * @author Cyriac
 *
 */
public final class DicomImage implements Comparable<DicomImage> {
	
    /***
     * Opener de fichier DICOM
     */
     private Opener opener; 

    /**
     * Metadata contenu dans le fichier DICOM
     */
    private final AttributeList attributeList;

    /**
     * Chemin vers le fichier DICOM
     */
    private final File dicomFile;

    /**
     * Image 
     */
    private ImagePlus imgPlus;

    /**
     * Index de la coupe temporelle dans laquelle se trouve l'image
     */
    private int timeSlice;

    /**
     * Index de l'image dans la coupe temporelle (Position spatiale
     * de l'image dans sa coupe)
     */
    private int stackIndex;

    private SourceImage displayImage;

    /**
     * On cree une instance de DicomImage
     * @param file fichier DICOM
     * @throws com.pixelmed.dicom.DicomException
     *      Quand le fichier n'est pas un fichier DICOM
     * @throws java.io.IOException
     *      Erreur de lecture/Ecriture du fichier
     */
    public DicomImage(File file) throws DicomException, IOException {
        if (!DicomUtils.isADicomFile(file)) 
            throw new DicomException("Le fichier " + file.getName() + " n'est pas un fichier DICOM");


        opener = new Opener();
        this.dicomFile = file;

        this.attributeList = new AttributeList();

        //Lecture de métadonnées
        this.attributeList.read(file.getAbsolutePath());


        this.imgPlus = IJ.openImage(dicomFile.getAbsolutePath());
        this.displayImage = new SourceImage(dicomFile.getAbsolutePath());


        this.timeSlice = 0;
        this.stackIndex = 0;

    }

    /**
     * On cree une instance de DicomImage avec son numéro d'acquisition
     * dans le temps
     * @param file fichier DICOM
     * @param timeSlice Index de la coupe temporelle dans laquelle se trouve l'image
     * @param slice Index de l'image dans la coupe temporelle dans laquelle elle se trouve
     *
     * @throws com.pixelmed.dicom.DicomException
     *      Quand le fichier n'est pas un fichier DICOM
     * @throws java.io.IOException
     *      Erreur de lecture/Ecriture du fichier
     */
    public DicomImage(File file, int timeSlice, int slice) throws DicomException, IOException {
        this(file);
        this.timeSlice = timeSlice;
        this.stackIndex = slice;
    }


    /**
     * @return La valeur du tag "ImageIndex"
     */
    public int getImageIndex() {
        return Integer.parseInt(this.getAttribute(TagFromName.ImageIndex)); 
    }




    /**
     * Cherche l'attibut et renvoies sa valeur
     * @param attrTag tag à cherche
     * @return la valeur du tag 
     */
    public String getAttribute (AttributeTag attrTag) {
        String tagStr = Integer.toHexString(attrTag.getGroup()) + "," + Integer.toHexString(attrTag.getElement());
        String str = Attribute.getDelimitedStringValuesOrEmptyString(attributeList, attrTag);

        //On essaye la deuxième méthode
        if (str.isEmpty()) {
            str = DicomTools.getTag(imgPlus, tagStr);
        }

        if (str != null) {
            str = str.trim();
            if (!str.isEmpty())
                return str;
        }
        return "0";

    }


    /**
     * Compare deux images en fonction de leur numero d'index.
     * Ce qui permet de ranger les images dans le bon ordre.
     * Renvoies un nombre n�gatif si l'image passé en paramètre est aprés l'image objet et un nombre positif sinon.
     * @param dcmImage image à comparer
     * @return un nombre négatif si l'index de l'image passé en paramètre est supérieur et un nombre positif sinon
     */
    @Override
    public int compareTo(DicomImage dcmImage) {

            if (this.getImageIndex() < dcmImage.getImageIndex()) {
                    return -1;
            }
            else {
                    return +1;
            }

    }

    /**
     * Transforme l'image en BufferedImage
     * @return BufferedImage
     */
    public BufferedImage getBufferedImage() {

       return this.displayImage.getBufferedImage();
    }
    
    /**
     * 
     * @return La largeur de l'image
     */
    public int getWidth() {
        return this.imgPlus.getWidth();
    }
    
    /**
     * 
     * @return La hauteur de l'image
     */
    public int getHeight() {
        return this.imgPlus.getHeight();
    }
    
    /**
     * 
     * @return Le chemin absolu vers le fichier DICOM
     */
    public String getAbsolutePath() {
        return this.dicomFile.getAbsolutePath();
    }
    
    /**
     * 
     * @return L'index de la coupe temporelle dans laquelle se trouve l'image
     */
    public int getTimeSlice() {
        return this.timeSlice;
    }
    
    /**
     * 
     * @return Index de l'image dans la coupe temporelle
     */
    public int getStackIndex() {
        return this.stackIndex;
    }
    
    /**
     *  
     * @return l'ensemble des méta-données du fichier DICOM
     */
    public AttributeList getAttributeList() {
        return this.attributeList;
    }
    
    public ImagePlus getImagePlus() {
        return this.imgPlus;
    }
    
    /**
     * 
     * @return l'image en ayant pris en compte le rescale slope et intercept
     */    
    public FloatProcessor getImageProcessor() {
        float rescaleInt = Float.parseFloat(getAttribute(TagFromName.RescaleIntercept));
        float rescaleSlope = Float.parseFloat(getAttribute(TagFromName.RescaleSlope));
        float[] pixels = (float[]) this.imgPlus.getProcessor().convertToFloatProcessor().getPixels();
        
        for (int i = 0; i < pixels.length; i++ ) {
            pixels[i] = pixels[i]*rescaleSlope + rescaleInt;
        }
        
        FloatProcessor fp = new FloatProcessor(getWidth(), getHeight(), pixels);
        return fp;
    }
      
  
	
}

