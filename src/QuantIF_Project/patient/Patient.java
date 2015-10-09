package QuantIF_Project.patient;

import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.ImageSizeException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import QuantIF_Project.utils.DicomUtils;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 * 
 * @author Cyriac
 *
 */
public class Patient {
	/**
	 * Nom anonymis� du patient
	 */
	private final String name;
	
	/**
	 * ID du patient
	 */
	private final String id;
	
	/**
	 * Sexe du patient : 'M' ou 'F'
	 */
	private final String sex;
	
	/**
	 * Age du patient
	 */
	private final String age;
	
	/**
	 * Poids du patient (en Kg)
	 */
	private final String weight;
	
	/**
	 * Liste des images liés au patient
	 */
	private ArrayList<DicomImage> dicomImages;
        
        /**
         * Nombres de coupes temporelles
         */
        private int nbTimeSlices;
        
        /**
         * Nombres d'images par coupes temporelle
         */
        
        private int framesPerTimeSlice;
        
        /**
         * Liste des DicomImages rangés par coupe
         */
        private DicomImage[][] sortedDicomImages;
        
      
        
        /**
         * Dit si c'est une série dynamique ou non
         */
        private boolean isDynamic;
        
        
	
	
	/**
	 * On cr�e une instance de patient � l'aide du chemin vers le dossier o�
	 * sont contenus tous les fichiers .dcm concernant le patient
	 * @param dirPath
	 * @throws NotDirectoryException 
	 * 		Lev�e quand le chemin fourni ne correspond pas � un repertoire
	 * @throws DicomFilesNotFoundException
	 * 		Lev�e quand aucun fichier DICOM n'a �t� trouv� dans le r�pertoire
	 * @throws BadParametersException
	 * 		Lev�e quand les param�tres d'entr�e sont invalides
	 */
	public Patient(String dirPath) throws NotDirectoryException, DicomFilesNotFoundException, BadParametersException {
		
		if (dirPath == null) {
			throw new BadParametersException("Le chemin rentré est invalide.");
		}
		
                this.nbTimeSlices = 0;
                this.framesPerTimeSlice = 0;
                
                this.sortedDicomImages = null;
		this.dicomImages = analyseDirectory(dirPath);
		
		//On suppose que tous les fichiers DICOM de ce répertoire sont tous liés au même patient
		
		this.name = this.dicomImages.get(0).getAttribute(TagFromName.PatientName);
		
		this.id = this.dicomImages.get(0).getAttribute(TagFromName.PatientID);
		
		this.sex = this.dicomImages.get(0).getAttribute(TagFromName.PatientSex);
		
		//age sous la forme 045Y, on doit enlever le Y
                this.age = this.dicomImages.get(0).getAttribute(TagFromName.PatientAge).replace("Y", "");
               
                this.weight = this.dicomImages.get(0).getAttribute(TagFromName.PatientWeight);
                
                
                
	}

    public DicomImage[][] getSortedDicomImages() {
       
        return sortedDicomImages;
    }
	
	
	
	
	
	/**
	 * Retourne l'image DICOM ayant cet index.
	 * 
	 * @param index
         *
         * @return 
	 * @throws BadParametersException
	 * 		Levee quand l'index demand� est soit inférieur ou égal à 0 soit supérieur à getMaxDicomImage()
	 */
	public DicomImage getDicomImage(int index) throws BadParametersException {
		if (index < 0) 
			throw new BadParametersException("Il n'y a pas d'image à cet index. L'index doit être superieur ou égal à 0!");
		if (index > this.dicomImages.size())
			throw new BadParametersException("Il n'y a pas d'image à cet index. L'index est trop grand!");
		return this.dicomImages.get(index);
	}
	
        /**
         * Retourne le nombre d'images sur le patient
         * @return 
        */
	public int getMaxDicomImage() {
            return this.dicomImages.size();
        }
        
       
	/**
	 * Retourne la présentation d'un patient
         * @return 
	 */
        @Override
	public String toString() {
		String str = "Présentation du patient " + this.id + "\n";
		str += "-Nom : " + this.name + "\n";
		str += "-Sexe : " + this.sex + "\n";
		str += "-Age : " + this.age + " ans\n";
		str += "-Poids : " + this.weight + " Kg\n";
		
		return str;
	}
	
	
	/**
	 * Parcourt le repertoire de fichiers et cree les instances de DicomImage
	 * �à l'aide des fichier DICOM et les range dans l'ordre croissant des index des images
         * et attribue à chaque DicomImage un sliceIndex et timeSlice Index pour les séries 
         * dynamiques puis range les BufferedImage dans la variable d'instance sortedImages
	 * @param path Chemin du dossier DICOM
	 * @return Une ArrayList de DicomImage : la liste des fichiers '.dcm' image liés au patient
	 * @throws NotDirectoryException 
	 * 		Levée quand le chemin fourni ne correspond pas � un repertoire
	 * @throws DicomFilesNotFoundException
	 * 		Levée quand aucun fichier DICOM n'a été� trouvé�dans le répertoire
	 */
	
	private ArrayList<DicomImage> analyseDirectory(String path) throws NotDirectoryException, DicomFilesNotFoundException {
		ArrayList<DicomImage> listDI = new ArrayList<>();
		File dir = new File(path);
		if (!dir.isDirectory()) {
			throw new NotDirectoryException("Le chemin '" + path + "' n'est pas un répertoire");
		}
		File[] files = dir.listFiles();
	        
		//On parcourt le dossier de fichiers
		if (files != null) {
                  int frameIndex = 0; //index de l'image dans une coupe temporelle: variant de 1 - framesPerTimeSlice
                  int timeSliceIndex = 0; //Index de la coupe temporelle : variant de 1 - nbTimeSlices
                 
                  boolean first = true;
                    for (File file : files) {
                        
                        if (file.isFile()) {
                            // Si c'est un fichier on vérifie si c'est un fichier DICOM
                            
                            //Puis on récupère la frame à laquelle il appartient et son numéro
                            //de coupe
                            
                            if (DicomUtils.isADicomFile(file)) {
                                try {
                                    
                                    DicomImage dcm = new DicomImage(file);
                                    //On récupère les paramètres de la série sur le premier fichier
                                    if (first) {
                                        this.isDynamic = true;
                                        first = false;
                                        String strNbTimeSlices = dcm.getAttribute(TagFromName.NumberOfTimeSlices);
                                        nbTimeSlices = (strNbTimeSlices.equals("N/A")) ? 0 : Integer.parseInt(strNbTimeSlices);
                                        
                                        String strFramesPTS = dcm.getAttribute(TagFromName.NumberOfSlices);
                                        framesPerTimeSlice = (strFramesPTS.equals("N/A")) ? 0 : Integer.parseInt(strFramesPTS);
                                        System.out.println("Nombre de Coupes : " + nbTimeSlices );
                                        System.out.println("Nombre d'images par coupes  : " + framesPerTimeSlice );
                                        
                                        this.sortedDicomImages = new DicomImage[nbTimeSlices][framesPerTimeSlice];
                                    }
                                    
                                    if (nbTimeSlices != 0) {
                                        
                                        this.isDynamic = false;
                                        
                                        //On trouve le numéro de frame en prenant la partie entière de la division
                                        timeSliceIndex = ((dcm.getImageIndex()-1)/framesPerTimeSlice) + 1;

                                        //si l'index image est un multiple de 74 alors on est à la dernière image d'une coupe
                                        frameIndex = (dcm.getImageIndex()%framesPerTimeSlice == 0) ? 74 : dcm.getImageIndex()%framesPerTimeSlice; 
                                        DicomImage dcmToAdd = new DicomImage(file, timeSliceIndex , frameIndex);
                                        listDI.add(dcmToAdd);
                                        this.sortedDicomImages[timeSliceIndex-1][frameIndex-1] = dcmToAdd;
                                    } 
                                    else { // Si ce n'est pas une série dynamique
                                        this.isDynamic = false;
                                        listDI.add(dcm);
                                    }
                                    
                                       
                                } catch (DicomException | IOException ex) {
                                    Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                    
                    
                    
		}
               
		
		//On vérifie si la liste n'est pas vide
		if (listDI.isEmpty()) {
			throw new DicomFilesNotFoundException("Aucun fichier DICOM n'a été trouvé dans ce repertoire");
		}
		//On classe les �l�ments de listDI dans ordre croissant des index des images
		Collections.sort(listDI);
                
                
		return listDI;
	}    
        
        
    /**
     * Renvoie la largeur de la première image de la série du patient si
     * toutes les images ont la même largeur, sinon envoie une erreur
     * @return 
     * @throws QuantIF_Project.patient.exceptions.ImageSizeException 
     *      Levée quand toutes les images n'ont pas les mêmes tailles
     */
    public int getImagesWidth() throws ImageSizeException {
        int width = this.dicomImages.get(0).getWidth();
        for (DicomImage di : this.dicomImages) {
            if (width != di.getWidth())
                throw new ImageSizeException("Les images de ce patient n'ont pas tous la même taille");
        }
        
        return width;
    }
    
    /**
     * Renvoie la hauteru de la première image de la série du patient si
     * toutes les images ont la même hauteru, sinon envoie une erreur
     * @return
     * @throws ImageSizeException
     *      Levée quand toutes les images n'ont pas les mêmes tailles
     */
    public int getImagesHeight() throws ImageSizeException {
        int height = this.dicomImages.get(0).getHeight();
         for (DicomImage di : this.dicomImages) {
            if (height != di.getHeight())
                throw new ImageSizeException("Les images de ce patient n'ont pas tous la même taille");
        }
         
        return height;
    }
    
    /**
     * Somme les images d'une coupe temporelle à l'autre
     * @param startSlice indice de la coupe temporellle de déaprt
     * @param endSlice indice de la coupe temporelle de fin
     * @return 
     */
    
    public BufferedImage[] summSlices(int startSlice, int endSlice) {
        
        //Tableau contenant les images sommés
        BufferedImage[] summImagesArray = new BufferedImage[this.framesPerTimeSlice];
        //Image avec les bonnes dimensions : à re remplir
        
        BufferedImage resultSumm;
        
        int width = 0;
        int height = 0;
            try {
                width = this.getImagesWidth();
                height = this.getImagesHeight();
            } catch (ImageSizeException ex) {
                Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        int maxGraylvl = 0;
        //On parcourt toutes les images de la coupe temporelle d'index startSlice
        for (int frameIndex = 0; frameIndex < this.framesPerTimeSlice; frameIndex++) {
            //Pour chaque de ces images on la somme avec ceux des coupes allant de startSlice à endSlice
            
            resultSumm = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
            Graphics2D gResultSumm = resultSumm.createGraphics();
            
            short[] resultPix = ((DataBufferUShort) resultSumm.getRaster().getDataBuffer()).getData();
            
            
            for (int sliceIndex = startSlice ; sliceIndex <= endSlice; sliceIndex++) {
                DicomImage dcmToSumm = this.sortedDicomImages[sliceIndex][frameIndex];
                if (dcmToSumm != null) {
                    
                    //Comme on ne connait pas le type de l'image à sommer, à la dessine dans un 
                    //BufferedImage de type USHORT_GRAY
                    BufferedImage buffToSumm = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
                    Graphics2D gBuffToSumm = buffToSumm.createGraphics();
                    gBuffToSumm.drawImage(dcmToSumm.getBufferedImage(), null, 0, 0);
                    
                    short[] buffPix = ((DataBufferUShort) buffToSumm.getRaster().getDataBuffer()).getData();
                    
                    //System.out.println("Type de l'image à sommer : " + buffToSumm.getType());
                    //Raster rast = buffToSumm.getRaster();
                    for (int row = 0; row<width; ++row) {
                        for (int col = 0; col < height; ++col ) {
                            resultPix[row * width + col] += (buffPix[row * width + col]/(endSlice - startSlice + 1));
                            
                          
                            
                            if (maxGraylvl <  resultPix[row * width + col]) {
                                maxGraylvl =  resultPix[row * width + col];
                            }
                            
                           
                        }
                        gBuffToSumm.dispose();
                    }
                }
                
            }
            //On doit recuperer le max
           
            // on remplit la l'image resulat de la somme
            
            for (int r = 0; r<width; ++r) {
                for (int c =0; c < height; ++c ) {
                        //resultPix[r * width + c] =  (short) (resultPix[r * width + c] * 65535/maxGraylvl); //65535 short max value
                        
                   
                    //System.out.println(data[r][c]);
                }
            }
            WritableRaster raster = resultSumm.getRaster();
            raster.setDataElements(0, 0, width, height, resultPix);
            resultSumm.setData(raster);
                    
            gResultSumm.dispose();
            summImagesArray[frameIndex] = resultSumm;
        }   
        System.out.println("Max gray level = " + maxGraylvl);
        System.out.println("Somme de " + (startSlice + 1)  + " à " + (endSlice + 1) + " faite!!!");
        return summImagesArray;
    }
    
   
    
   
   
    public int getNbTimeSlices() {
        return this.nbTimeSlices;
    }
    
    public int getFramesPerTimeSlice() {
        return this.framesPerTimeSlice;
    }
    
    
        
}
