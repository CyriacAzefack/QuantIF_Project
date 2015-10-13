/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author Cyriac
 */
public class testDisplay extends javax.swing.JFrame {
    private static ShortProcessor sp;

    private  void display() {
        ImageIcon ii = new ImageIcon(sp.getBufferedImage());        
        
   
        label.setIcon(ii);
    }
    /**
     * Creates new form testDisplay
     */
    public testDisplay() {
       
            initComponents();
            
        try {
            String path = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\TEST recon choline\\PA0\\ST0\\TEPinjection\\IM220";
            DicomImage di = new DicomImage(new File(path));
            BufferedImage buff = new BufferedImage(di.getWidth(), di.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
            Graphics2D g = buff.createGraphics();
            g.drawImage(di.getBufferedImage(), 0, 0, null);
            
            short[] pixels;
            pixels = ((DataBufferUShort) buff.getRaster().getDataBuffer()).getData();
            
            sp = new ShortProcessor(buff);
            display();
        } catch (DicomException | IOException ex) {
            Logger.getLogger(testDisplay.class.getName()).log(Level.SEVERE, null, ex);
        }

   
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label = new javax.swing.JLabel();
        sliderMin = new javax.swing.JSlider();
        sliderMax = new javax.swing.JSlider();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sliderMin.setMaximum(50000);
        sliderMin.setPaintLabels(true);
        sliderMin.setPaintTicks(true);
        sliderMin.setSnapToTicks(true);
        sliderMin.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        sliderMin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMinStateChanged(evt);
            }
        });

        sliderMax.setMaximum(50000);
        sliderMax.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMaxStateChanged(evt);
            }
        });

        jButton1.setText("Launch");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sliderMax, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(sliderMin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(194, 194, 194)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addComponent(sliderMin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sliderMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        display();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void sliderMinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMinStateChanged
       sp.setMinAndMax(sliderMin.getValue(), 32639);
       display();
    }//GEN-LAST:event_sliderMinStateChanged

    private void sliderMaxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMaxStateChanged
         sp.setMinAndMax(sliderMin.getValue(), 32639);
       display();
    }//GEN-LAST:event_sliderMaxStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

      
            /* Set the Nimbus look and feel */
            //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
            /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
            * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
            */
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                java.util.logging.Logger.getLogger(testDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(testDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(testDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(testDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            //</editor-fold>
            
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(() -> {
                new testDisplay().setVisible(true);
            });
            
            
        //</editor-fold>
        
        //</editor-fold>
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel label;
    private javax.swing.JSlider sliderMax;
    private javax.swing.JSlider sliderMin;
    // End of variables declaration//GEN-END:variables
}
