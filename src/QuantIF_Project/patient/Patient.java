package QuantIF_Project.patient;

import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dcm4che2.io.DicomInputStream;




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
	private String id;
	
	/**
	 * Sexe du patient : 'M' ou 'F'
	 */
	private String sex;
	
	/**
	 * Age du patient
	 */
	private int age;
	
	/**
	 * Poids du patient (en Kg)
	 */
	private int weight;
	
	/**
	 * Liste des images liés au patient
	 */
	private ArrayList<DicomImage> dicomImages;
        
        
	
	
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
		
                
               
                
                
		
		this.dicomImages = analyseDirectory(dirPath);
		
		//On suppose que tous les fichiers .dcm de ce r�pertoire sont li�s au m�me patient
		
		this.name = this.dicomImages.get(0).searchInfoByKey("PatientsName");
		
		this.id = this.dicomImages.get(0).searchInfoByKey("PatientID");
		
		this.sex = this.dicomImages.get(0).searchInfoByKey("PatientsSex");
		
		//age sous la forme 045Y, on doit enlever le Y
                String ageStr = this.dicomImages.get(0).searchInfoByKey("PatientsAge");
                this.age = 0;
                if (!ageStr.isEmpty())
                    this.age = Integer.parseInt(ageStr.replace("Y", ""));
		
                String weightStr = this.dicomImages.get(0).searchInfoByKey("PatientsWeight");
                if (!weightStr.isEmpty())
                    this.weight = Integer.parseInt(weightStr);
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
         * Retourne la hauteur des images du patient
         * @return 
         */
        public int getImagesHeight(){
            return this.dicomImages.get(1).getHeight();
        }
        
        /**
         * Retourne la largeur des images du patient
         * @return 
         */
        public int getImagesWidth(){
            return this.dicomImages.get(1).getWidth();
        }
	/**
	 * Retourne la pr�sentation d'un patient
         * @return 
	 */
        @Override
	public String toString() {
		String str = "Présentation du patient " + this.id + "\n";
		str += "-Nom : " + this.name + "\n";
		str += "-Sexe : " + this.sex + "\n";
                if (this.age == 0) {
                    str += "-Age : N/A ans\n";
                } else {
                    str += "-Age : " + this.age + " ans\n";
                }
		
                if (this.weight == 0) {
                    str += "-Poids : " + this.weight + " Kg\n";
                } else {
                    str += "-Poids : N/A Kg\n";
                }
		
		
		return str;
	}
	
	
	/**
	 * Parcourt le repertoire de fichiers et cree les instances de DicomImage
	 * �à l'aide des fichier '.dcm' et les range dans l'ordre croissant des index des images
	 * @param path
	 * @return Une ArrayList de DicomImage : la liste des fichiers '.dcm' image li�s au patient
	 * @throws NotDirectoryException 
	 * 		Levée quand le chemin fourni ne correspond pas � un repertoire
	 * @throws DicomFilesNotFoundException
	 * 		Levée quand aucun fichier DICOM n'a été� trouvé�dans le répertoire
	 */
	
	private ArrayList<DicomImage> analyseDirectory(String path) throws NotDirectoryException, DicomFilesNotFoundException {
		ArrayList<DicomImage> listDI = new ArrayList<DicomImage>();
		File dir = new File(path);
		if (!dir.isDirectory()) {
			throw new NotDirectoryException("Le chemin '" + path + "' n'est pas un répertoire");
		}
		File[] files = dir.listFiles();
	        
		//On parcourt le dossier de fichiers
		if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            // Si c'est un fichier on v�rifie l'extension
                            // On v�rifie l'extension du fichier
                            
                            if (isADicomFile(file.getAbsolutePath())) {
                                listDI.add(new DicomImage(file.getAbsolutePath()));
                                //System.out.println("Fichier: " + files[i].getName() + " Ajout�!");
                            }
                        }
                    }
		}
               
		
		//On v�rifie si la liste n'est pas vide
		if (listDI.isEmpty()) {
			throw new DicomFilesNotFoundException("Aucun fichier DICOM n'a été trouvé dans ce repertoire");
		}
		//On classe les �l�ments de listDI dans ordre croissant des index des images
		Collections.sort(listDI);
                
                
		return listDI;
	}
        
        /**
         * Vérifie que le fichier est bien un fichier DICOM
         * @param absolutePath chemin du fichier
         * @return 
         */
        private boolean isADicomFile(String absolutePath) {
            boolean isADCM = false;
            try {
                DicomInputStream dis = new DicomInputStream(new File(absolutePath));
                //Si on arrive à l'ouvrir, ce que c'est bon
                isADCM = true;
            } catch (IOException ex) {
                //Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
            }

            return isADCM;
        }
}
