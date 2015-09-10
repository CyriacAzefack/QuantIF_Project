import QuantIF_Project.patient.DicomImage;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


public class Test {

    public static void main(String[] args) {
        boolean atWork = false;
        String root = atWork ? "C:\\Users\\kamelya\\Documents\\TEPFDG_initiale_Linque\\" 
                : "C:\\Users\\Cyriac\\Google Drive\\QuantIF_Project\\TEPFDG_initiale_Linque\\";
        String path  = root + "TEP_FDG_Linque_anom_200.dcm";

        DicomImage dcm = new DicomImage(path);

        Image img = dcm.getImage();   


        if (img == null) {
            System.err.println("******** L'image n'a pas été ouverte!!! **********");
        }
        int n = img.getWidth(null);
        int m = img.getHeight(null);
        
        BufferedImage buff_img = new BufferedImage(n, m, BufferedImage.TYPE_BYTE_GRAY);

        Graphics gr = buff_img.getGraphics();
        gr.drawImage(img, 0, 0, null);
        gr.dispose();

        //Affichage
        JFrame frame = new JFrame("Image noir et blanc");
        JLabel lblimage = new JLabel(new ImageIcon(buff_img));
        frame.getContentPane().add(lblimage, BorderLayout.CENTER);
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


        
        byte pix[] = new byte[1];
       
        double[][] matrix = new double[n][m];

        //swips the rows
        BufferedImage new_img = new BufferedImage(n, m, BufferedImage.TYPE_INT_RGB);
        Graphics2D gr1 = new_img.createGraphics();
        gr1.drawImage(buff_img, 0, 0, null);
        gr1.dispose();
        short[] data = new short[256];
        for (int i = 0; i<256; ++i ) {
            data[i] = (short) (256 - i);
        }
        
        LookupTable lut = new ShortLookupTable(0, data);
        LookupOp op = new LookupOp(lut, null);
        
        new_img = op.filter(new_img, null);
        /*
        for (int row = 0; row < n; ++row) {
            //swips the columns
            for (int col = 0; col < m; ++col) {   
                //matrix[row][col] = new_img.getRGB(row, col);
                buff_img.getRaster().getDataElements(row, col, pix);
                matrix[row][col] = pix[0];
    
                new_img.setRGB(row, col, 0);
                //System.out.println("row " + row + " col " + col + " pixel " + matrix[row][col]);

            }
        }
        */
        
        
         //Affichage
        JFrame frame1 = new JFrame("Image couleur");
        JLabel lblimage1 = new JLabel(new ImageIcon(new_img));
        frame1.getContentPane().add(lblimage1, BorderLayout.CENTER);
        frame1.setSize(500, 400);
        frame1.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame1.setVisible(true);
        String desktopPath = atWork ? "C:\\Users\\kamelya\\Desktop\\" 
                : "C:\\Users\\Cyriac\\Desktop\\";
        try {
            ImageIO.write(new_img, "jpg", new File(desktopPath + "redhot.jpg"));
            ImageIO.write(buff_img, "jpg", new File(desktopPath + "gray.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
    }



}


