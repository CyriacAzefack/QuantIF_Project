package QuantIF_Project.patient;

import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.ImageSizeException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import QuantIF_Project.patient.exceptions.TimeFrameOverflowException;
import QuantIF_Project.utils.DicomUtils;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
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
            
           /*
            String outputDirDyn1 = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\Test Multi acquisitions\\TEPDyn1\\";
            String outputDirDyn2 = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\Test Multi acquisitions\\TEPDyn2\\";
            String outputDirStatic = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\Test Multi acquisitions\\TEPStatic\\";
            //On vide les répertoires
            DicomUtils.emptyDirectory(new File(outputDirDyn1));
            DicomUtils.emptyDirectory(new File(outputDirDyn2));
            DicomUtils.emptyDirectory(new File(outputDirStatic));
            try {
                createSubDynAcquisition(outputDirDyn1, 0, 7);
                createSubDynAcquisition(outputDirDyn2, 27, 33);
                createSubDynAcquisition(outputDirStatic, 20);
            } catch (DicomException | IOException ex) {
                Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
            }
            */
            
            
                
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
            this.nbTimeFrames = this.timeFrames.size();
            
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
    
    public float[][] summSlices(int startSlice, int endSlice) {
        
        //Tableau contenant les images sommés
        float[][] summImagesArray = new float[this.nbImagesPerTimeFrame][width*height];
        //Image avec les bonnes dimensions : à re remplir
        
        BufferedImage resultSumm;
        
        float max = 0;
        
        
        //On parcourt toutes les images de la coupe temporelle d'index startSlice
        for (int imageIndex = 0; imageIndex < this.nbImagesPerTimeFrame; imageIndex++) {
            //Pour chacune de ces images on la somme avec ceux des coupes allant de startSlice à endSlice
            
            
            resultSumm = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
            
            //tableau contenant les valeurs des pixels de l'image resultSumm en SHORT
            
            
            //On cree un tableau de mm taille mais en float, car la somme des pixels risque de dépasser
            //la taille max du format SHORT
            float[] pixels = new float[width*height];
            
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
                                if (max < pixels[row * width + col])
                                    max = pixels[row * width + col];
                                
                            }
                            gBuffToSumm.dispose();
                        }
                    }
                }
                catch (BadParametersException ex) {
                    Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
           
            
            
            summImagesArray[imageIndex] = pixels;
        }   
        System.out.println("Max Value : " + max);
        System.out.println("Somme de " + (startSlice + 1)  + " à " + (endSlice + 1) + " faite!!!");
        
        //Affichage de la somme 
        /*
        try {
            DicomUtils.showImages(summImagesArray);
        } catch (BadParametersException ex) {
            Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
        }
                */
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

    public String getPixelUnity() {
        String unit = null;
            try {
                unit = this.timeFrames.get(0).getDicomImage(0).getAttribute(TagFromName.Units);
            } catch (BadParametersException ex) {
                Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
            }
        return unit;
    }
    
    /**
     * Crée une acquisition TEP Dynamique DICOM à partir du patient courant
     * @param outputDir repertoire où seront crées les images DICOM
     * @param startIndex index de TimeFrame de début
     * @param endIndex index de TimeFrame de fin
     * @throws BadParametersException 
     * @throws DicomException
     * @throws IOException 
     */
    private void createSubDynAcquisition(String outputDir, int startIndex, int endIndex) throws BadParametersException, DicomException, IOException  {
        
        AttributeList list;
        TimeFrame tf;
        DicomImage dcm ;
        File file;
        String filePath;
        String transferSyntaxUID = "1.2.840.10008.1.2"; //Implicit VR Endian: Default Transfer Syntax for DICOM
        for (int frameIndex = startIndex; frameIndex < endIndex; frameIndex++) {
            tf = this.timeFrames.get(frameIndex);
            
            for (int imageIndex = 0; imageIndex<tf.size(); imageIndex++) {
                //Pour chaque image on recupére l'entête
               
                dcm = tf.getDicomImage(imageIndex);
                if (dcm != null) {
                    list = dcm.getAttributeList();
                    //On remplace le nombre de time frame dans l'entete des nouveaus images dicom
                    list.replaceWithValueIfPresent(TagFromName.NumberOfTimeSlices, Integer.toString(endIndex-startIndex));
                    //On crée le fichier ou sera sauvé la nouvelle image DICOM
                    filePath = outputDir + "IM"+ (frameIndex+1) +"."+(imageIndex+1);
                    file = new File(filePath);

                    list.write(file, transferSyntaxUID, true, true);
                }
                
            }
        }
        
        System.out.println("Sous Série dynamique de la coupe " + startIndex + " à " + endIndex + " crée avec succès dans \""+outputDir+"\"");
       
    }
    
    /**
     * Crée une acquisition statique à partir du patient courant
     * @param outputDir repertoire où seront crées les images DICOM
     * @param frameIndex index de la time frame 
     * @throws BadParametersException
     * @throws IOException
     * @throws DicomException 
     */
    private void createSubDynAcquisition(String outputDir, int frameIndex) throws BadParametersException, IOException, DicomException {
        AttributeList list;
        TimeFrame tf = this.timeFrames.get(frameIndex);
        DicomImage dcm ;
        File file;
        String filePath;
        String transferSyntaxUID = "1.2.840.10008.1.2"; //Implicit VR Endian: Default Transfer Syntax for DICOM
        for (int imageIndex = 0; imageIndex<tf.size(); imageIndex++) {
            //Pour chaque image on recupére l'entête

            dcm = tf.getDicomImage(imageIndex);
            if (dcm != null) {
                list = dcm.getAttributeList();
                //On remplace le nombre de time frame dans l'entete des nouveaus images dicom
                list.replaceWithValueIfPresent(TagFromName.NumberOfTimeSlices, Integer.toString(0));
                //On crée le fichier ou sera sauvé la nouvelle image DICOM
                filePath = outputDir + "IM"+ (frameIndex+1) +"."+(imageIndex+1);
                file = new File(filePath);

                list.write(file, transferSyntaxUID, true, true);
            }
                
         }
        System.out.println("Sous Série statique de la coupe " + frameIndex + " crée avec succès dans \""+outputDir+"\"");
    }
        
}
