package aa;

import QuantIF_Project.gui.AfficherImages;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import javax.swing.JButton;




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
        
        String path = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\TEST recon choline\\PA0\\ST0\\TEPinjection\\IM220";
        ImagePlus imp = IJ.openImage(path);
        imp.setTitle("test");
        imp.show();
      
        BufferedImage in = imp.getBufferedImage();
        BufferedImage out = AfficherImages.rescale(in, 700, 700);
        
        ImagePlus impOut = new ImagePlus("", out);
        impOut.show();
        
    }
    
}
