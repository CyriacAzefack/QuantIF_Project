/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package aa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;








/**
 *
 * @author Cyriac
 */
public class test2 {
   
    /**
    * @param args
    */
    public static void main(String[] args)  {
        try {
            String path = "C:\\Users\\kamelya\\Documents\\NetBeansProjects\\QuantIF_Project\\config.txt";
            String content = new String(Files.readAllBytes(Paths.get(path)));
            System.out.println("File ....");
            System.out.println(content);
            JSONObject root =  new JSONObject(content);
            
            System.out.println(root);
            
            String inputPath = root.getString("Input");
            
            System.out.println(inputPath);
        } catch (IOException ex) {
            Logger.getLogger(test2.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
}