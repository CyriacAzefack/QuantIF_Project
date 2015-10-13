package QuantIF_Project.patient;
import QuantIF_Project.utils.DicomUtils;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;
import ij.io.Opener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 * 
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
        private SourceImage srcImg;
        /**
         * Index de la coupe temporelle dans laquelle se trouve l'image
         */
        private int timeSlice;
        
        /**
         * Index de l'image dans la coupe temporelle dans laquelle elle se trouve
         */
	private int slice;
        
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
            this.attributeList.read(file.getAbsolutePath());
            
           
            this.srcImg = new SourceImage(file.getAbsolutePath());
            
         
           
            this.timeSlice = 0;
            this.slice = 0;
            
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
            this.slice = slice;
        }
        
       
	
	public int getImageIndex() {
		return Integer.parseInt(this.getAttribute(TagFromName.ImageIndex)); 
	}
	
        
	
	
	/**
         * Cherche l'attibut et renvoies sa valeur
         * @param attrTag tag à cherche
         * @return la valeur du tag 
         */
	public String getAttribute (AttributeTag attrTag) {
            String str = Attribute.getSingleStringValueOrEmptyString(this.attributeList, attrTag);
            if (str.isEmpty())
                str = "0";
            return str;
	}
	
	
	/**
	 * Compare deux images en fonction de leur numero d'index.
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
          
           return this.srcImg.getBufferedImage();
        }

    public int getWidth() {
        return this.srcImg.getWidth();
    }

    public int getHeight() {
        return this.srcImg.getHeight();
    }
    
    
    public String getAbsolutePath() {
        return this.dicomFile.getAbsolutePath();
    }
    
    
    public int getTimeSlice() {
        return this.timeSlice;
    }
    
    
    public int getSlice() {
        return this.slice;
    }
    
	
       
  
	
}

