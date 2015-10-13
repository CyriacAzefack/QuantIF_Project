package QuantIF_Project.patient;

import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.ImageSizeException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import QuantIF_Project.patient.exceptions.TimeFrameOverflowException;
import QuantIF_Project.utils.DicomUtils;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import ij.process.FloatProcessor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
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
	private String name;
	
	/**
	 * ID du patient
	 */
	private final String id;
	
	/**
	 * Sexe du patient : 'M' ou 'F'
	 */
	private String sex;
	
	/**
	 * Age du patient
	 */
	private String age;
	
	/**
	 * Poids du patient (en Kg)
	 */
	private String weight;
	
	/**
	 * Liste des timesFrames
	 */
	private ArrayList<TimeFrame> timeFrames;
        
        /**
         * Nombres de coupes temporelles
         */
        private int nbTimeFrames;
        
        /**
         * Nombres d'images par coupes temporelle
         */
        private int nbImagesPerTimeFrame;
        
        /**
         * largeur des images
         */
        private int width;
        
        /**
         * hauteur des images
         */
        private int height;
        
	
	/**
	 * On cree une instance de patient  l'aide du chemin vers le dossier où�
	 * sont contenus tous les fichiers DICOM concernant le patient
	 * @param dirPath chemin du repertoire où se trouve les fichiers DICOM
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

            this.nbTimeFrames = 0;
            this.nbImagesPerTimeFrame = 0;


            ArrayList<DicomImage> dicomImages = checkDicomImages(dirPath);

            //On récupère les paramètres du patient sur le premier fichier

            this.name = dicomImages.get(0).getAttribute(TagFromName.PatientName);

            this.id = dicomImages.get(0).getAttribute(TagFromName.PatientID);

            this.sex = dicomImages.get(0).getAttribute(TagFromName.PatientSex);

            //age sous la forme 045Y, on doit enlever le Y
            this.age = dicomImages.get(0).getAttribute(TagFromName.PatientAge).replace("Y", "");

            this.weight = dicomImages.get(0).getAttribute(TagFromName.PatientWeight);

            this.nbTimeFrames = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.NumberOfTimeSlices));

            this.nbImagesPerTimeFrame = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.NumberOfSlices));
            
            this.width = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.Columns));
            
            this.height = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.Rows));

            this.timeFrames = new ArrayList<>(this.nbTimeFrames);
            checkTimeFrames(dicomImages);

                
	}

    
	
	/**
	 * Retourne la coupe temporelle ayant cet index.
	 * 
	 * @param index index de la coupe
         * @return la TimeFrame à cet index
	 * @throws BadParametersException
	 * 		Levee quand l'index demand� est soit inférieur ou égal à 0 soit supérieur au nombre de coupes
	 */
	public TimeFrame getTimeFrame(int index) throws BadParametersException {
            if (index < 0) 
                    throw new BadParametersException("L'indice doit être supérieur ou égal à 0");
            if (index > this.timeFrames.size())
                    throw new BadParametersException("Il n'y a pas de coupe temporelle à cet index. L'index est trop grand!");

            return this.timeFrames.get(index);
	}
	
       
	
        
	
	
	/**
	 * Parcourt le repertoire de fichiers et cree les instances de DicomImage
	 * �à l'aide des fichier DICOM et les range dans l'ordre croissant des index des images
         * 
	 * @param path Chemin du dossier DICOM
	 * @return Une ArrayList de DicomImage : la liste des fichiers 'DICOM' image liés au patient
	 * @throws NotDirectoryException 
	 * 		Levée quand le chemin fourni ne correspond pas � un repertoire
	 * @throws DicomFilesNotFoundException
	 * 		Levée quand aucun fichier DICOM n'a été� trouvé�dans le répertoire
	 */
	
	private ArrayList<DicomImage> checkDicomImages(String path) throws NotDirectoryException, DicomFilesNotFoundException {
		ArrayList<DicomImage> listDI = new ArrayList<>();
		File dir = new File(path);
		if (!dir.isDirectory()) {
			throw new NotDirectoryException("Le chemin '" + path + "' n'est pas un répertoire");
		}
		File[] files = dir.listFiles();
	        
		//On parcourt le dossier de fichiers
		if (files != null) {
                  
                    for (File file : files) {
                        
                        if (file.isFile()) {
                            // Si c'est un fichier on vérifie si c'est un fichier DICOM
                            
                            if (DicomUtils.isADicomFile(file)) {
                                try {
                                    
                                    DicomImage dcm = new DicomImage(file);
                                    listDI.add(dcm);
                                   
                                    
                                       
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
		//On range  les dicomImages
		Collections.sort(listDI);
                
                System.out.println(listDI.size() + " images DICOM détectées!!");
		return listDI;
	}    
        
        /**
         * Parcourt la liste des dicomImages et on les range dans les time frames
         * @param dicomImages 
         */
        private void checkTimeFrames(ArrayList<DicomImage> dicomImages) {
            //On récupère la liste des acquisition time
            ArrayList<String> ATList = new ArrayList<>();
            String AT;
            for (DicomImage di : dicomImages) {
                AT = di.getAttribute(TagFromName.AcquisitionTime);
                if (!ATList.contains(AT))
                    ATList.add(AT);
            }
            
            //On crée tous les TimeFrame
            ATList.stream().forEach((at) -> {
                try {
                    this.timeFrames.add(new TimeFrame(this.nbImagesPerTimeFrame, at, this.width, this.height));
                } catch (BadParametersException ex) {
                    Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            //On parcout les dicomImages et on les ajoutent à leur TimeFrame respectif
            dicomImages.stream().forEach((di) -> {
                try {
                    int timeFrameIndex = ATList.indexOf(di.getAttribute(TagFromName.AcquisitionTime));
                    this.timeFrames.get(timeFrameIndex).addDicomImage(di);
                } catch (BadParametersException | ImageSizeException | TimeFrameOverflowException ex) {
                    Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            System.out.println(this.timeFrames.size() + " Coupes détectées!!");
        }
    
    
    
    public int getHeight(){
        return height;
    }
    
    public int getWidth() {
        return width;
    }
    
    /**
     * Somme les images d'une coupe temporelle à l'autre
     * @param startSlice indice de la coupe temporellle de déaprt
     * @param endSlice indice de la coupe temporelle de fin
     * @return Un tableau contenant la somme des images
     */
    
    public BufferedImage[] summSlices(int startSlice, int endSlice) {
        
        //Tableau contenant les images sommés
        BufferedImage[] summImagesArray = new BufferedImage[this.nbImagesPerTimeFrame];
        //Image avec les bonnes dimensions : à re remplir
        
        BufferedImage resultSumm;
        
        
        
        //On parcourt toutes les images de la coupe temporelle d'index startSlice
        for (int imageIndex = 0; imageIndex < this.nbImagesPerTimeFrame; imageIndex++) {
            //Pour chacune de ces images on la somme avec ceux des coupes allant de startSlice à endSlice
            
            
            resultSumm = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
            
            //tableau contenant les valeurs des pixels de l'image resultSumm en SHORT
            
            
            //On cree un tableau de mm taille mais en INT, car la somme des pixels risque de dépasser
            //la taille max du format SHORT
            int[] pixels = new int[width*height];
            
            for (int sliceIndex = startSlice ; sliceIndex <= endSlice; sliceIndex++) {
                try {
                    DicomImage dcmToSumm = this.timeFrames.get(sliceIndex).getDicomImage(imageIndex);
                    if (dcmToSumm != null) {
                        
                        //Comme on ne connait pas le type de l'image à sommer, à la dessine dans un
                        //BufferedImage de type USHORT_GRAY
                        BufferedImage buffToSumm = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
                        Graphics2D gBuffToSumm = buffToSumm.createGraphics();
                        gBuffToSumm.drawImage(dcmToSumm.getBufferedImage(), null, 0, 0);
                        
                        //Tableau contenant les valeurs de pixels de l'image a sommer
                        short[] buffPix = ((DataBufferUShort) buffToSumm.getRaster().getDataBuffer()).getData();
                        
                      
                        for (int row = 0; row<width; ++row) {
                            for (int col = 0; col < height; ++col ) {
                                int pix = buffPix[row * width + col];
                                if (buffPix[row * width + col] < 0) {
                                    //On le convertie en valeur INT UNSIGNED
                                    pix = 65536 + pix;
                                }
                                pixels[row * width + col] += pix;
                                
                            }
                            gBuffToSumm.dispose();
                        }
                    }
                }
                catch (BadParametersException ex) {
                    Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
           
            // On crée l'image avec les valeurs de pixels résultant de la somme
            FloatProcessor fp = new FloatProcessor(width, height , pixels );
            
            summImagesArray[imageIndex] = fp.getBufferedImage();
        }   
        
        System.out.println("Somme de " + (startSlice + 1)  + " à " + (endSlice + 1) + " faite!!!");
        //Affichage de la somme par IJ
        DicomUtils.showImages(summImagesArray);
        return summImagesArray;
        
    }
    
   
    public int getNbTimeSlices() {
        return this.nbTimeFrames;
    }
    
    public int getFramesPerTimeSlice() {
        return this.nbImagesPerTimeFrame;
    }
    
    @Override
    public String toString() {
            String str = "Présentation du patient " + this.id + "\n";
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
        
}
