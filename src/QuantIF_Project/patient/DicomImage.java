package QuantIF_Project.patient;
import QuantIF_Project.patient.exceptions.BadParametersException;
import java.util.ArrayList;
import java.util.Properties;

import ij.plugin.DICOM;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
/**
 * 
 * @author Cyriac
 *
 */
public class DicomImage extends DICOM implements Comparable<DicomImage> {
	
	/**
	 * Liste des informations sur une image
	 */
	private final ArrayList<DicomInfo> dicomInfos;
	
	/**
	 * Chemin vers le fichier dcm
	 */
	private final String path;
        
        /**
         * si lut = LUT_GRAY, l'image sera affiché en gris
         */
        public final static int LUT_GRAY = 0;
        
        /**
         * si lut = LUT_RED_HOT, l'image sera affiché en red hot
         */
        public final static int LUT_RED_HOT = 1;
        
        /**
         * Défini le type d'affichage de l'image
         */
	private int lut;
	
      
     
	
	
	/**
	 * On cr�e une instance de DicomImage
	 * @param path
	 */
	public DicomImage(String path) {
		super();
		this.getImageStackSize();
		this.path = path;
		
                //Si le fichier n'est pas un fichier DICOM, le reader DICOM vas 
                // quand même l'ouvrir sans erreur
		this.open(path);
                
		
		this.dicomInfos = extractInfos();
                
                this.lut = DicomImage.LUT_GRAY;
		
	}
	
	/**
	 * Renvoies le numero d'id de l'image
	 * @return
	 */
	public int getImageIndex() {
		return Integer.parseInt(this.searchInfoByKey("ImageNumber")); 
	}
	
	/**
	 * Renvoies le chemin du fichier
	 * @return
	 */
	public String getPath() {
            return this.path;
	}
	
	/**
	 * Cherche une info en fonction de sa cl�
	 * @param key
	 * @return la valeur de l'info
	 */
	public String searchInfoByKey(String key) {
		String value = null;
		for (DicomInfo info : dicomInfos ){
			if (info.getKey().equals(key))
				value = info.getValue();
		}
		return value;
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
            Image image = this.getImage();
            BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null),
        BufferedImage.TYPE_INT_RGB);
            Graphics gr = buffImg.getGraphics();
            gr.drawImage(image, 0, 0, null);
            
            
            return buffImg;            
        }
	
       
        /**
         * Renvoies l'image du fichier DICOM selon le Lookup Table demandé
         * @return 
         */
        @Override
        public Image getImage() {
            Image image = null;
            
            if (this.lut == LUT_GRAY) {
                image = super.getImage();
            }
            else if (this.lut == LUT_RED_HOT) {
                image = toRedHot();
            }
            
            return image;
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

