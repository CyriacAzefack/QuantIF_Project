/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuanttIF of the "Centre Henri Becquerel"
 *   * 
 */
package aa;

import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.NoTAPSerieFoundException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import QuantIF_Project.serie.BodyBlock;
import QuantIF_Project.serie.TAPSerie;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cyriac
 */
public class test2 {
   
    /**
    * @param args
    */
    public static void main(String[] args) {
        try {
            String path = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\PET_5-405";
            TAPSerie tap = new TAPSerie(path);
            BodyBlock bb = tap.getBlock(2);
            
            System.out.println("START Time : " + bb.getStartTime());
            System.out.println("MID Time : " + bb.getMidTime());
            System.out.println("END Time : " + bb.getEndTime());
        } catch (NotDirectoryException | DicomFilesNotFoundException | BadParametersException | NoTAPSerieFoundException ex) {
            Logger.getLogger(test2.class.getName()).log(Level.SEVERE, null, ex);
        }

        
       
              
       
    }
}

    
