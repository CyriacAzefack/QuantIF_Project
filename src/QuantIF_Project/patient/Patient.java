package QuantIF_Project.patient;

import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;




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
	 * Liste des images li�es au patient
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
		//On doit v�rifier quer le chemin fourni est un repertoire de fichier
		
		this.dicomImages = analyseDirectory(dirPath);
		
		//On suppose que tous les fichiers .dcm de ce r�pertoire sont li�s au m�me patient
		
		this.name = this.dicomImages.get(0).searchInfoByKey("PatientsName");
		
		this.id = this.dicomImages.get(0).searchInfoByKey("PatientID");
		
		this.sex = this.dicomImages.get(0).searchInfoByKey("PatientsSex");
		
		//age sous la forme 045Y, on doit enlever le Y
		this.age = Integer.parseInt(this.dicomImages.get(0).searchInfoByKey("PatientsAge").replace("Y", ""));
		
		this.weight = Integer.parseInt(this.dicomImages.get(0).searchInfoByKey("PatientsWeight"));
	}
	
	
	
	
	
	/**
	 * Retourne l'image DICOM ayant cet index.
	 * L'image ayant l'index 0 n'existe pas
	 * @param index
         * @return 
	 * @throws BadParametersException
	 * 		Lev�e quand l'index demand� est soit trop grand ou trop petit 
	 */
	public DicomImage getDicomImage(int index) throws BadParametersException {
		if (index < 1) 
			throw new BadParametersException("Il n'y a pas d'image à cet index. L'index doit être superieur ou égal à 1!");
		if (index > this.dicomImages.size())
			throw new BadParametersException("Il n'y a pas d'image à cet index. L'index est trop grand!");
		return this.dicomImages.get(index-1);
	}
	
        /**
         * Retourne le nombre d'images sur le patient
         * @return 
        */
	public int getMaxDicomImage() {
            return this.dicomImages.size();
        }
	/**
	 * Retourne la pr�sentation d'un patient
	 */
	public String toString() {
		String str = "Présentation du patient " + this.id + "\n";
		str += "-Nom : " + this.name + "\n";
		str += "-Sexe : " + this.sex + "\n";
		str += "-Age : " + this.age + " ans\n";
		str += "-Poids : " + this.weight + " Kg\n";
		
		return str;
	}
	
	
	/**
	 * Parcourt le r�pertoire de fichiers et cr�e les instances de DicomImage
	 * � l'aide des fichier '.dcm' et les range dans l'ordre croissant des index des images
	 * @param path
	 * @return Une ArrayList de DicomImage : la liste des fichiers '.dcm' image li�s au patient
	 * @throws NotDirectoryException 
	 * 		Lev�e quand le chemin fourni ne correspond pas � un repertoire
	 * @throws DicomFilesNotFoundException
	 * 		Lev�e quand aucun fichier DICOM n'a �t� trouv� dans le r�pertoire
	 */
	
	private ArrayList<DicomImage> analyseDirectory(String path) throws NotDirectoryException, DicomFilesNotFoundException {
		ArrayList<DicomImage> listDI = new ArrayList<DicomImage>();
		File dir = new File(path);
		if (!dir.isDirectory()) {
			throw new NotDirectoryException("Le chemin '" + path + "' n'est pas un r�pertoire");
		}
		File[] files = dir.listFiles();
		
		//On parcourt le dossier de fichiers
		if (files != null) {
			for (int i=0; i<files.length; i++) {
				if (files[i].isFile()) { // Si c'est un fichier on v�rifie l'extension
					
					// On v�rifie l'extension du fichier
				
					int extensionIndex = files[i].getName().lastIndexOf(".");
					String fileExtension = files[i].getName().substring(extensionIndex + 1);
					
					if (fileExtension.equals("dcm")) {
					
						listDI.add(new DicomImage(files[i].getAbsolutePath()));
						//System.out.println("Fichier: " + files[i].getName() + " Ajout�!");
					}
				}
			}
		}
		
		//On v�rifie si la liste n'est pas vide
		if (listDI.size() == 0) {
			throw new DicomFilesNotFoundException("Aucun fichier '.dcm' n'a été trouvé dans ce repertoire");
		}
		//On classe les �l�ments de listDI dans ordre croissant des index des images
		Collections.sort(listDI);
		return listDI;
	}
}
