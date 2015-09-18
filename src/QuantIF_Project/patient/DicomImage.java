package QuantIF_Project.patient;
import QuantIF_Project.utils.DicomUtils;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;
import java.util.ArrayList;
import java.util.Properties;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 * 
 * @author Cyriac
 *
 */
public class DicomImage implements Comparable<DicomImage> {
	
	/**
	 * Metadata contenu dans le fichier DICOM
	 */
	private final AttributeList attributeList;
	
	/**
	 * Chemin vers le fichier DICOM
	 */
	private final File dicomFile;
        
        private SourceImage srcImg;
        
	
	/**
	 * On cree une instance de DicomImage
         * @param file fichier DICOM
         * @throws com.pixelmed.dicom.DicomException
         * @throws java.io.IOException
	 */
	public DicomImage(File file) throws DicomException, IOException {
            if (!DicomUtils.isADicomFile(file)) 
                throw new DicomException("Le fichier " + file.getName() + " n'est pas un fichier DICOM");
            
            this.dicomFile = file;
                    
            this.attributeList = new AttributeList();            
            this.attributeList.read(file.getAbsolutePath());
            
            this.srcImg = new SourceImage(file.getAbsolutePath());
               
		
	}
	
	/**
	 * Renvoies le numero d'id de l'image
	 * @return
	 */
	public int getImageIndex() {
		return Integer.parseInt(this.getAttribute(TagFromName.ImageIndex)); 
	}
	
	
	
	/**
         * Cherche l'attibut et renvoies sa valeur
         * @param attrTag tag à cherche
         * @return 
         */
	public String getAttribute (AttributeTag attrTag) {
		return Attribute.getSingleStringValueOrEmptyString(this.attributeList, attrTag);
	}
	
	
	/**
	 * Compare deux images en fonction de leur numero d'index.
	 * Renvoies un nombre n�gatif si l'image passé en paramètre est aprés l'image objet et un nombre positif sinon.
	 * @param dcmImage
	 * @return
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
	
       
      
	/**
	 * Extrait les diff�rentes informations du fichier image '.dcm'
	 * @return ArrayList de DicomInfo
	 */
	private ArrayList<DicomInfo> extractInfos() {
		ArrayList<DicomInfo> infos = new ArrayList<>();
		
		Properties prop = this.getProperties();
		String infoString = prop.getProperty("Info");
		
		//On doit maintenant parser ce string 'infos' pour pouvoir r�cup�rer tous les tags
		String[] datas = infoString.split("\n");
		for (String data:datas) {
			try {
				infos.add(new DicomInfo(data));
			} catch (Exception e) {
			
				e.printStackTrace();
			}
		}		
		return infos;
		
	}

    private Image toRedHot() {
        throw new UnsupportedOperationException("Pas encore implémenté"); //To change body of generated methods, choose Tools | Templates.
    }
        
        

	
	
	
	
	
	
	
	
	
}

