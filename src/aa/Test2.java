package aa;


import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Cyriac
 */
public class Test2 {
    public static void main(String[] args) {
        
       
        int p = (short)(-125 +65536) ;
        short s = -125;
        System.out.println(p);
        BufferedImage b = new BufferedImage(512, 512, BufferedImage.TYPE_USHORT_GRAY);
        short[] pixels = ((DataBufferUShort) b.getRaster().getDataBuffer()).getData();

         for (int row = 0; row<512; ++row) {
            for (int col = 0; col < 512; ++col ) {
               if (row > 168 && col > 168) {
                    pixels[row * 512 + col] = (short) (-0);
               }
               else {
                   pixels[row * 512 + col] = (short) 10000;
               }
            }

        }

         WritableRaster raster = b.getRaster();
        raster.setDataElements(0, 0, 512, 512, pixels);
        b.setData(raster);

        JFrame frame = new JFrame("Test");
        frame.setSize(1024, 1024);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new JLabel(new ImageIcon(b)));
        frame.setVisible(true);

    }
    
}
