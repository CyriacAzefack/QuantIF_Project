import QuantIF_Project.patient.Patient;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Test {

    public static void main(String[] args) {
        String s = "0000{1{256}(4449434d){1}";
        Pattern p = Pattern.compile(s);
        String absolutePath = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\TEPFDG_initiale_Linque\\TEP_FDG_Linque_anom_24.dcm";
         try {
                //On récupère le contenu du fichier
                File f = new File(absolutePath);
                
                byte[] buffer;
                try (FileInputStream fin = new FileInputStream(f)) {
                    buffer = new byte[(int) f.length()];
                    new DataInputStream(fin).readFully(buffer);
                }
               
                StringBuilder builder = new StringBuilder();
                
                
                for(byte b : buffer) {
                    builder.append(String.format("%02x", b));
                }
                 String textFile = builder.toString();
                
               
                //On vérifie si il matche l'expression régulière
                Matcher matcher = p.matcher(textFile);
                while(matcher.find()) {
                    System.out.println("Trouvé !");
                }
                
                
                //On regarde les 128 premiers
                
                
            } catch (IOException ex) {
                Logger.getLogger(Patient.class.getName()).log(Level.SEVERE, null, ex);
            }
        
       
       
        
    }



}


