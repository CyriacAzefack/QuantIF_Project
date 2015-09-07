import Quantif_project.exceptions.BadParametersException;
import Quantif_project.exceptions.DicomFilesNotFoundException;
import Quantif_project.exceptions.NotDirectoryException;
import Quantif_project.patient.DicomImage;
import Quantif_project.patient.Patient;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//String path = "file.dcm";
		/**
		 * Dossier contenant toutes les images .dcm d'un patient
		 */
		String path = "C:\\Users\\Cyriac\\Google Drive\\QuantIF_Project\\TEPFDG_initiale_Linque";
		//String path = "C:\\Users\\Cyriac\\Google Drive\\sms.xsl";
		Patient p = null;
		try {
			p = new Patient(path);
		} catch (NotDirectoryException | DicomFilesNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BadParametersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(p);
		
		DicomImage dcm;
		try {
			dcm = p.getDicomImage(150);
			System.out.println(dcm.getImageIndex());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	
		
	}

}
