/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package aa;

import QuantIF_Project.gui.PatientSerieViewer;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author Cyriac
 */
public class test2 {
   
   
    /**
    * @param args
    */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Frame");
        frame.setSize(500, 500);
        JButton b = new JButton("test");
        
        String path = "C:\\Users\\kamelya\\Documents\\NetBeansProjects\\QuantIF_Project\\tmp\\Barbolisi_FullImage_1_Taking\\IM35.tif";
        ImagePlus imp = IJ.openImage(path);
        BufferedImage buff = imp.getBufferedImage();
        buff = PatientSerieViewer.rescale(buff, 500, 500);
        ImagePlus imp2 = new ImagePlus("test", buff);
        imp2.show();
        
        int width = 500;
        int height = 500;
        
        
        b.addActionListener((ActionEvent e) -> {
            Roi roi = imp2.getRoi();
            Polygon p = roi.getPolygon();
            int x = p.xpoints[0]*168/width;
            int xx = p.xpoints[1]*168/width;
            int y = p.ypoints[0]*168/width;
            int yy = p.ypoints[2]*168/width;
            System.out.println("X ["+x+" "+xx+"]");
            System.out.println("Y ["+y+" "+yy+"]");
        });
        
        frame.add(b);
        frame.setVisible(true);
        
        
                
        
    }
}

    
