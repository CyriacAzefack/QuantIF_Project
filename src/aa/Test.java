package aa;


import QuantIF_Project.patient.DicomImage;
import com.pixelmed.dicom.DicomException;
import ij.process.ShortProcessor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;








public class Test {
    
    
    public static void main(String[] args) {
        try {
            String path = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\TEST recon choline\\PA0\\ST0\\TEPinjection\\IM220";
            
            JFrame frame = new JFrame("Test");
            frame.setSize(1024, 1024);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            DicomImage di = new DicomImage(new File(path));
            BufferedImage buff = new BufferedImage(di.getWidth(), di.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
            Graphics2D g = buff.createGraphics();
            //g.drawImage(di.getBufferedImage(), 0, 0, null);
            
            short[] pixels;
            pixels = ((DataBufferUShort) buff.getRaster().getDataBuffer()).getData();
            
            
            
            
           
            
            int width = buff.getWidth();
            int height = buff.getHeight();
            int max = 0;
           
            for (int row = 0; row<width; ++row) {
                for (int col = 0; col < height; ++col ) {
                    pixels[row * width + col] = (short) 40000;
                   
                }
            
            }
            
            
            ShortProcessor sp = new ShortProcessor(buff);
            System.out.println("ShortProc MAX : " + sp.getMax());
            //sp.setMinAndMax(10000, max);
            buff = sp.getBufferedImage();
            ImageIcon ii = new ImageIcon(buff);
            JLabel imageLabel = new JLabel(ii);
            frame.add(imageLabel);
            frame.setVisible(true);
            
            g.dispose();
        } catch (DicomException | IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

}
