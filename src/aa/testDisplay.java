/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package aa;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.process.FloatProcessor;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseWheelListener;
import javax.swing.JInternalFrame;

/**
 *
 * @author Cyriac
 */
public class testDisplay extends javax.swing.JFrame {

    /**
     * Creates new form testDisplay
     */
    public testDisplay() {
        initComponents();
        String path = "C:\\Users\\kamelya\\Desktop\\dw.png";
        ImagePlus imp = IJ.openImage(path);
        imp.setTitle(path);
        
        //WindowManager.setTempCurrentImage(imp);
        imp.show();
        
        WindowManager.setTempCurrentImage(imp);
        Frame frame = WindowManager.getCurrentWindow();
        frame.dispose();
        
        //window.setVisible(true);
        //window.toFront();
        
        
        //jiFrame.setContentPane(frame.getFocusCycleRootAncestor());
        
        Component[] comps = frame.getComponents();
        
       
        //MouseWheelListener[] mwlisteners = frame.getMouseWheelListeners();
       
        
        for (Component comp : comps) {
            System.out.println(comp.toString());
            this.panel.add(comp);  
        }
        
        imp.setProcessor(new FloatProcessor(600,600));
        /*
        for (MouseWheelListener comp : mwlisteners) {
            jiFrame.addMouseWheelListener(comp);
        }
       */
        /*
        for (ContainerListener list : listeners) {
            jiFrame.addContainerListener(list);
        }
        */
        //jiFrame.setContentPane(((JFrame)frame).getContentPane());
        
        panel.setSize(this.getSize());
        panel.setVisible(true);
        
        
        
        //buff = PatientSerieViewer.rescale(buff, this.getWidth(), this.getHeight());
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Test integration IJ");

        panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0)));
        panel.setToolTipText("");

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 955, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 584, Short.MAX_VALUE)
        );

        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(testDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new testDisplay().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}
