import QuantIF_Project.patient.DicomImage;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//String path = "file.dcm";
		/**
		 * Dossier contenant toutes les images .dcm d'un patient
		 */
		String path = "C:\\Users\\kamelya\\Documents\\TEPFDG_initiale_Linque\\TEP_FDG_Linque_anom_1.dcm";
		
		DicomImage dcm = new DicomImage(path);
                BufferedImage buff = dcm.getBufferedImage();
                
                int width = buff.getWidth();
                int height = buff.getHeight();
                byte[] dstBuff = ((DataBufferByte) buff.getRaster().getDataBuffer()).getData();
                
                for (int i=0; i<width/2; i++) {
                    for (int j=0; j<width/2; j++) {
                        System.out.println(dstBuff[i+j*width] & 0xFF);
                    }
                  
                }
		
		
	
		
	}

}
