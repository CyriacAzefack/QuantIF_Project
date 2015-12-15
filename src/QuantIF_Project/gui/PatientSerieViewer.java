/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuantIF_Project.gui;



import QuantIF_Project.serie.DicomImage;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.serie.Block;
import QuantIF_Project.serie.Serie;
import QuantIF_Project.serie.TAPSerie;
import QuantIF_Project.utils.DicomUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.Roi;
import ij.plugin.LutLoader;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.LUT;
import ij.process.ShortProcessor;
import ij.process.StackConverter;
import ij3d.Content;
import ij3d.Image3DUniverse;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;






/**
 *
 * @author Cyriac
 */
public class PatientSerieViewer extends javax.swing.JInternalFrame {
    
    /**
     * TEPSerie à afficher
     */
    private static Serie patient;
    
    /**
     * PatientMultiSeries à afficher
     */
    private PatientMultiSeries patientMultiSeries;
    
    
    /**
     * Nombre total d'images à afficher
     */
    private int nbImages;
    
    /**
     * ID de l'image en cours d'affichage
     */
    private static int currentImageID;
    
    
    
    /**
     * LUT en cours
     */
    private LUT currentLUT;
    
    /**
     * Luminosité des images
     */
    private static int maxBrightness;
    private static int minBrightness;
   
    
      
    /**
     * Somme d'images a afficher
     */
    private static BufferedImage[] displayedImages;
    
    /**
     * nombre d'images par coupe temporelle
     */
    private int imagesPerBlock;
    
    /**
     * Nombre de coupes temporelle
     */
    private int nbTimeSlices;
    
    /**
     * Unité de la valeur du pixel
     */
    private String pixelUnity;
    
    /**
     * 
     */
    private static String currentImageTitle;
    
    
    private static ImagePlus currentImagePlus;
    
    /**
     * Taille de l'image affichée
     */
    public final static int IMAGE_SIZE = 600;
    
    
    /**
     * Creates new form AfficherImages
     * @param p patient en cours
     * 
     */
    public PatientSerieViewer(Serie p) {
        initComponents();
        
        currentImageTitle = "Frame 1";
        ImageProcessor ip = new FloatProcessor(p.getWidth(), p.getHeight());
        currentImagePlus = new ImagePlus(currentImageTitle, ip);
        
        patient = p;
        this.patientMultiSeries = null;
        
        this.radioButtonDyn1.setVisible(false);
        this.radioButtonStat.setVisible(false);
        this.radioButtonDyn2.setVisible(false);
        
        //Image Panel settings
        imagePanel.removeAll();
        imagePanel.repaint();
        
       
        currentImagePlus.show();
        
        
        
        WindowManager.setTempCurrentImage(currentImagePlus);
        Frame frame = WindowManager.getCurrentWindow(); //getFrame(currentImageTitle);
        
        frame.dispose();
        ImageCanvas canvas = (ImageCanvas) frame.getComponent(0);
       
        
        //On affiche l'image dans le panel
        //System.out.println(comp.toString());
        this.imagePanel.add(canvas, BorderLayout.CENTER);
        
        
         //On y ajoute la gestion du ZOOM
        
        imagePanel.addMouseWheelListener((MouseWheelEvent e) -> {
            int zoom = e.getWheelRotation();
            
            if (zoom < 0 && e.isControlDown()) {
                //Zoom in
                //IJ.run("In"); 
                canvas.zoomIn(e.getX(), e.getY());
            } 
            else if (zoom > 0 && e.isControlDown()){
                canvas.zoomOut(e.getX(), e.getY());
            }
        });
        
        imagePanel.validate();
        
        updateComponents();
        
        
        display(currentImageID);
        
        //On empeche la fenêtre interne de pouvoir être déplacée
        BasicInternalFrameUI bifui = (BasicInternalFrameUI) this.getUI();
        Component northPane = bifui.getNorthPane();
        MouseMotionListener[] motionListeners = (MouseMotionListener[]) northPane.getListeners(MouseMotionListener.class);

        for (MouseMotionListener listener: motionListeners)
            northPane.removeMouseMotionListener(listener);
   
        
        
     
        
    }
    
    public PatientSerieViewer(PatientMultiSeries pms) {
        this(pms.getStartDynSerie());
        this.patientMultiSeries = pms;
        //On gère les radioButton
            //On les rends visible
        this.radioButtonDyn1.setVisible(true);
        this.radioButtonDyn1.setEnabled(true);
        
        this.radioButtonStat.setVisible(true);
         this.radioButtonStat.setEnabled(true);
         
        this.radioButtonDyn2.setVisible(true);
        this.radioButtonDyn2.setEnabled(true);
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.radioButtonDyn1);
        buttonGroup.add(this.radioButtonStat);
        buttonGroup.add(this.radioButtonDyn2);
        
        this.radioButtonDyn1.setSelected(true);
       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        maskFileChooser = new javax.swing.JFileChooser();
        labelSliceList = new javax.swing.JLabel();
        frameList = new javax.swing.JComboBox();
        summ1 = new javax.swing.JComboBox();
        summ2 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        summButton = new javax.swing.JToggleButton();
        sliderMin = new javax.swing.JSlider();
        jLabel7 = new javax.swing.JLabel();
        saveSummsButton = new javax.swing.JButton();
        radioButtonDyn2 = new javax.swing.JRadioButton();
        radioButtonDyn1 = new javax.swing.JRadioButton();
        radioButtonStat = new javax.swing.JRadioButton();
        imagePanel = new javax.swing.JPanel();
        imageOptionsPanel = new javax.swing.JPanel();
        nextButton = new javax.swing.JButton();
        prevButton = new javax.swing.JButton();
        imageSlider = new javax.swing.JSlider();
        imageIDTextField = new javax.swing.JTextField();
        acquisitionTimeTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        selectAorta = new javax.swing.JButton();
        tridimObsButton = new javax.swing.JButton();
        sliderMax = new javax.swing.JSlider();

        maskFileChooser.setDialogTitle("Choisir le dossier du masque");
        maskFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setBorder(null);
        setTitle("Patient Viewer");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFocusable(false);
        setPreferredSize(new java.awt.Dimension(800, 800));
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelSliceList.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        labelSliceList.setText("Coupe temporelle");
        getContentPane().add(labelSliceList, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 110, 140, 20));

        frameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameListActionPerformed(evt);
            }
        });
        getContentPane().add(frameList, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 110, 110, 30));

        summ1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summ1ActionPerformed(evt);
            }
        });
        getContentPane().add(summ1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 190, -1, -1));

        getContentPane().add(summ2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1340, 190, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel8.setText("à");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1310, 190, 10, 20));

        summButton.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        summButton.setText("Sommer");
        summButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summButtonActionPerformed(evt);
            }
        });
        getContentPane().add(summButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1051, 183, 110, 40));

        sliderMin.setMaximum(100000);
        sliderMin.setSnapToTicks(true);
        sliderMin.setValueIsAdjusting(true);
        sliderMin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMinStateChanged(evt);
            }
        });
        getContentPane().add(sliderMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 40, 360, -1));

        jLabel7.setText("Min");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 40, -1, -1));

        saveSummsButton.setText("Sauvegarder toutes les images sommées");
        saveSummsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSummsButtonActionPerformed(evt);
            }
        });
        getContentPane().add(saveSummsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 630, 320, 100));

        radioButtonDyn2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        radioButtonDyn2.setText("Série Dynamique de fin");
        radioButtonDyn2.setEnabled(false);
        radioButtonDyn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonDyn2ActionPerformed(evt);
            }
        });
        getContentPane().add(radioButtonDyn2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 310, 250, 40));

        radioButtonDyn1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        radioButtonDyn1.setText("Serie Dynamique de départ");
        radioButtonDyn1.setEnabled(false);
        radioButtonDyn1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                radioButtonDyn1StateChanged(evt);
            }
        });
        radioButtonDyn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonDyn1ActionPerformed(evt);
            }
        });
        getContentPane().add(radioButtonDyn1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 250, 270, 40));

        radioButtonStat.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        radioButtonStat.setText("Série Statique");
        radioButtonStat.setEnabled(false);
        radioButtonStat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                radioButtonStatStateChanged(evt);
            }
        });
        radioButtonStat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonStatActionPerformed(evt);
            }
        });
        getContentPane().add(radioButtonStat, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 280, 210, 40));

        imagePanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 153), 3, true));
        imagePanel.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        imagePanel.setRequestFocusEnabled(false);
        imagePanel.setLayout(new javax.swing.BoxLayout(imagePanel, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(imagePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 600, 600));

        nextButton.setText(">>");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        prevButton.setText("<<");
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });

        imageSlider.setPaintLabels(true);
        imageSlider.setPaintTicks(true);
        imageSlider.setSnapToTicks(true);
        imageSlider.setValue(0);
        imageSlider.setPreferredSize(new java.awt.Dimension(512, 23));
        imageSlider.setValueIsAdjusting(true);
        imageSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                imageSliderStateChanged(evt);
            }
        });

        imageIDTextField.setEditable(false);
        imageIDTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        imageIDTextField.setText("               ");
        imageIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageIDTextFieldActionPerformed(evt);
            }
        });

        acquisitionTimeTextField.setEditable(false);
        acquisitionTimeTextField.setBackground(new java.awt.Color(204, 255, 255));
        acquisitionTimeTextField.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        acquisitionTimeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acquisitionTimeTextFieldActionPerformed(evt);
            }
        });

        jLabel4.setText("Temps d'acquisition (secondes)");

        javax.swing.GroupLayout imageOptionsPanelLayout = new javax.swing.GroupLayout(imageOptionsPanel);
        imageOptionsPanel.setLayout(imageOptionsPanelLayout);
        imageOptionsPanelLayout.setHorizontalGroup(
            imageOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imageOptionsPanelLayout.createSequentialGroup()
                .addComponent(prevButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(imageIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(234, 234, 234)
                .addComponent(nextButton))
            .addGroup(imageOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(imageOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(imageOptionsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(acquisitionTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(imageSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE))
                .addContainerGap())
        );
        imageOptionsPanelLayout.setVerticalGroup(
            imageOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imageOptionsPanelLayout.createSequentialGroup()
                .addGroup(imageOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imageIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nextButton)
                    .addComponent(prevButton))
                .addGap(18, 18, 18)
                .addComponent(imageSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(imageOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(acquisitionTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(imageOptionsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 610, 640, 120));

        jLabel5.setText("Max");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 10, 30, -1));

        selectAorta.setText("<html>Sélectionner Aorte</html>");
        selectAorta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectAorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAortaActionPerformed(evt);
            }
        });
        getContentPane().add(selectAorta, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 230, 50));

        tridimObsButton.setText("Observation 3D");
        tridimObsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tridimObsButtonActionPerformed(evt);
            }
        });
        getContentPane().add(tridimObsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 230, 60));

        sliderMax.setMaximum(100000);
        sliderMax.setMinorTickSpacing(1);
        sliderMax.setPaintLabels(true);
        sliderMax.setSnapToTicks(true);
        sliderMax.setToolTipText("");
        sliderMax.setValue(5);
        sliderMax.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sliderMax.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        sliderMax.setValueIsAdjusting(true);
        sliderMax.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMaxStateChanged(evt);
            }
        });
        getContentPane().add(sliderMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 10, 360, 24));

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    /**
     * Selection de l'aorte
     * @param evt 
     */
    private void selectAortaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAortaActionPerformed
        IJ.run("Close All");
        int startFrame, endFrame;
       
        // On récupère la liste des frames
        ComboBoxModel model = this.frameList.getModel();
         String[] frames = new String[model.getSize()];
        for (int i = 0; i < model.getSize(); i++) {
            frames[i] = "" + (i + 1);
        }
        
        //On demande à l'utilisateur de choisir la frame de début et celle de fin pour la somme
        startFrame = -1 + Integer.valueOf((String) JOptionPane.showInputDialog(null, "Choisir la coupe de début", "Coupes temporelles à sommer", JOptionPane.INFORMATION_MESSAGE, null, frames, frames[0]));
        String[] frames2 = Arrays.copyOfRange(frames, startFrame + 1, frames.length);
        endFrame = -1 + Integer.valueOf((String)JOptionPane.showInputDialog(null, "Choisir la coupe de fin", "Coupes temporelles à sommer", JOptionPane.INFORMATION_MESSAGE, null, frames2, frames2[0]));
        JOptionPane.showMessageDialog(null, "Somme de la coupe " + (startFrame+1) + " - " + (endFrame+1));
        if (this.patientMultiSeries == null) {        
            this.patient.selectAorta(null, startFrame, endFrame);
        }
        else {
            
            this.patientMultiSeries.selectAorta(startFrame, endFrame);
        }
        
       
        
        
    }//GEN-LAST:event_selectAortaActionPerformed

    private void sliderMaxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMaxStateChanged
        this.maxBrightness = this.sliderMax.getValue();
        this.currentLUT.max = this.sliderMax.getValue();
        display(this.currentImageID);
    }//GEN-LAST:event_sliderMaxStateChanged

    private void frameListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameListActionPerformed
        try {
            if (frameList.getSelectedIndex() >= 0) {
                int frameIndex = frameList.getSelectedIndex();
                currentImageTitle = "Frame " + (frameIndex + 1);
                displayedImages = getImagesToDisplay(frameIndex);
                this.imagesPerBlock = displayedImages.length;
                imageSlider.setMaximum(imagesPerBlock);
                display(currentImageID);

                //On affiche l'acquisition time
                //this.acquisitionTimeTextField.setText("" + patient.getBlock(frameIndex).getMidTime());
                //this.tridimObsButtonActionPerformed(evt);
            }
            
        } catch (BadParametersException ex) {
            Logger.getLogger(PatientSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }//GEN-LAST:event_frameListActionPerformed

    private void summ1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summ1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_summ1ActionPerformed

    private void summButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summButtonActionPerformed
        if (this.summButton.isSelected()) {
            int s1 = this.summ1.getSelectedIndex();
            int s2 = this.summ2.getSelectedIndex();
            float[][] imagePixels = this.patient.summSlices(s1, s2); 
            this.currentImageTitle = "Somme de la frame " + (s1 + 1) + " à " + (s2 + 1);
            for (int frame = 0; frame < this.imagesPerBlock; frame++) {
                  this.displayedImages[frame] = DicomUtils.pixelsToBufferedImage(this.patient.getWidth(), this.patient.getHeight(), imagePixels[frame]);
            }
        }
        else {
            try {
                //On affiche les frames
                PatientSerieViewer.displayedImages = getImagesToDisplay(this.frameList.getSelectedIndex());
            } catch (BadParametersException ex) {
                Logger.getLogger(PatientSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        display(PatientSerieViewer.currentImageID);
        
    }//GEN-LAST:event_summButtonActionPerformed

    private void sliderMinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMinStateChanged
        PatientSerieViewer.minBrightness = this.sliderMin.getValue();
         this.currentLUT.min = this.sliderMin.getValue();
        display(PatientSerieViewer.currentImageID);
    }//GEN-LAST:event_sliderMinStateChanged

    private void saveSummsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSummsButtonActionPerformed
        BufferedImage[] arrayToSave;
        String filename;
        for (int timeSliceIndex = 0; timeSliceIndex < this.nbTimeSlices; timeSliceIndex++) {
            try {
                
                arrayToSave = getSummSlices(0, timeSliceIndex);
                filename = "tmp\\summ\\1_"+(timeSliceIndex+1)+".tiff";
                DicomUtils.saveImagesAsTiff(arrayToSave, filename);
            } catch (BadParametersException ex) {
                Logger.getLogger(PatientSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_saveSummsButtonActionPerformed

    private void radioButtonDyn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonDyn1ActionPerformed
        this.patient = this.patientMultiSeries.getStartDynSerie();
        updateComponents();
        display(currentImageID);
    }//GEN-LAST:event_radioButtonDyn1ActionPerformed

    private void radioButtonStatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonStatActionPerformed
        this.patient = this.patientMultiSeries.getStaticSerie();
        updateComponents();
        display(currentImageID);
    }//GEN-LAST:event_radioButtonStatActionPerformed

    private void radioButtonDyn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonDyn2ActionPerformed
        this.patient = this.patientMultiSeries.getEndDynSerie();
        updateComponents();
        display(currentImageID);
    }//GEN-LAST:event_radioButtonDyn2ActionPerformed

    private void radioButtonDyn1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_radioButtonDyn1StateChanged
       
    }//GEN-LAST:event_radioButtonDyn1StateChanged

    private void radioButtonStatStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_radioButtonStatStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_radioButtonStatStateChanged

    private void tridimObsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tridimObsButtonActionPerformed
        ImageStack imgStack = new ImageStack(this.patient.getWidth(), this.patient.getHeight(), null);
        ImageProcessor imProc;
       
        
        for (BufferedImage buff : this.displayedImages) {
            BufferedImage newBuff = new BufferedImage(buff.getWidth(), buff.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
            Graphics2D g = newBuff.createGraphics();
            g.drawImage(buff, null, 0, 0);


            ImagePlus impTemp = new ImagePlus("", newBuff);
            //impTemp.setLut(currentLUT);
            imProc = impTemp.getProcessor();
            imgStack.addSlice(imProc);
        }
        ImagePlus imgPlus = new ImagePlus("", imgStack);
       
       
        //imgPlus.show();
        //On stoppe le thread si déjà ouvert
        Image3DUniverse univ = new Image3DUniverse();
        Thread thread;
        thread = new Thread("Affichage 3D") {
            public void run() {
               
                new StackConverter(imgPlus).convertToGray8();

                // Create a universe and show it
                
                univ.show();
                univ.getWindow().setTitle(currentImageTitle);
                // Add the image as a volume rendering
                Content c = univ.addVoltex(imgPlus);
                
                
            }
        };
        thread.start();
        
         
    }//GEN-LAST:event_tridimObsButtonActionPerformed

    private void acquisitionTimeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acquisitionTimeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_acquisitionTimeTextFieldActionPerformed

    private void imageIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageIDTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_imageIDTextFieldActionPerformed

    private void imageSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_imageSliderStateChanged

        if (imageSlider.getValue() > 0)
        this.currentImageID = imageSlider.getValue() - 1;
        display(this.currentImageID);
    }//GEN-LAST:event_imageSliderStateChanged

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed

        if (this.currentImageID > 1 ) {
            this.imageSlider.setValue(--this.currentImageID);
        }
        //display(currentImageID);

    }//GEN-LAST:event_prevButtonActionPerformed

    /**
     * Affiche la prochaine image
     * @param evt 
     */
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed

        if (this.currentImageID < this.imagesPerBlock ) {
            this.imageSlider.setValue(++this.currentImageID);

        }

    }//GEN-LAST:event_nextButtonActionPerformed
    
    /**
     * Affiche l'image d'id imageID dans la fenetre
     * @param imageID id de l'image à afficher
     * @throws BadParametersException
     *      Levée lorsqu'aucune image ne correspond à imageID
     */
    private static void display(int imageID){
        
        BufferedImage bufferedImage;
        
        bufferedImage = displayedImages[imageID];
          
        displayImage(bufferedImage);
        
        imageIDTextField.setText((imageID + 1) + " / " + (displayedImages.length));
        
        int blockIndex = frameList.getSelectedIndex();
        try {
            if (blockIndex >= 0) {
                String aqTime  = patient.getBlock(blockIndex).getAcquisitionTime();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                PatientSerieViewer.acquisitionTimeTextField.setText(sdf.format(DicomUtils.dicomDateToDate(aqTime)));
            }
        } catch (BadParametersException ex) {
            Logger.getLogger(PatientSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Affiche une image dans la fenêtre
     * @param b BufferedImage à afficher
     */
    private static void displayImage(BufferedImage b) {
       //On redimensionne l'image
        BufferedImage bufferedImage = rescale(b, IMAGE_SIZE, IMAGE_SIZE);
        
       
        
        //On sature l'image
        bufferedImage = saturateImage(bufferedImage);
        
         //On applique la lookUpTable
        //bufferedImage = applyLut(bufferedImage, this.currentLUT);
        
        
        
        //On l'affiche dans la zone prévu a cet effet
       
        currentImagePlus.setProcessor(new ByteProcessor(bufferedImage));
       
        //System.out.println("Panel component " + imagePanel.getComponentCount());
         
    }
    
    /**
     * Redimensionne une image en largeur, en hauteur et aussi ajuste
     * le contraste
     * @param source l'image à redimensionner
     * @param newWidth nouvelle largeur
     * @param newHeight nouvelle hauteur
     * @return BufferedImage redimensionnée
     */
    public static BufferedImage rescale(BufferedImage source, int newWidth, int newHeight) {
        /* On crée une nouvelle image aux bonnes dimensions. */ 
       

        Image scaledInstance = source.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_FAST);
        BufferedImage buf = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_USHORT_GRAY); 
        Graphics2D g = buf.createGraphics(); 
        g.drawImage(scaledInstance, 0, 0, null);
        g.dispose();
       
      
        
        
        /* On retourne l'image bufferisée, qui est une image. */ 
        return buf; 
        
    }
    
    /**
     * Applique une LookUpTable à l'image DICOM
     * @param dcm image à modifier
     * @param lut LUT à appliquer
     * @return BufferedImage 
     */
    private BufferedImage applyLut(BufferedImage srcImg, LUT lut) {
        
        BufferedImage b = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
        Graphics2D g = b.createGraphics();
        g.drawImage(srcImg, null, 0, 0);
        //System.out.println("Type de l'image :" + ));
        
        ImagePlus imp = new ImagePlus("", b);
        imp.setLut(lut);
       
       
        return imp.getBufferedImage();
    }
    
    /**
     * retourne la liste des images à la frameIndex
     * @param sliceIndex index de la coupe temporelle
     * @return 
     */
    private BufferedImage[] getImagesToDisplay (int sliceIndex) throws BadParametersException {
        if (sliceIndex > this.nbTimeSlices)
            throw new BadParametersException("L'indice de la coupe temporelle fournie n'est pas accessible");
        
        Block block = this.patient.getBlock(sliceIndex);
        BufferedImage[] buffs = new BufferedImage[block.size()];
        for (int frame = 0; frame < block.size(); frame++) {
            DicomImage dcm = block.getDicomImage(frame);
            if (dcm == null) {
                BufferedImage b= new BufferedImage(patient.getWidth(), patient.getHeight(), BufferedImage.TYPE_INT_RGB);
                buffs[frame] = b;
            }
            else {
                buffs[frame] = dcm.getBufferedImage();
            }
            
        }
        
        return buffs;
    }
    
  
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextField acquisitionTimeTextField;
    private static javax.swing.JComboBox frameList;
    private static javax.swing.JTextField imageIDTextField;
    private javax.swing.JPanel imageOptionsPanel;
    private javax.swing.JPanel imagePanel;
    private static javax.swing.JSlider imageSlider;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel labelSliceList;
    private javax.swing.JFileChooser maskFileChooser;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JRadioButton radioButtonDyn1;
    private javax.swing.JRadioButton radioButtonDyn2;
    private javax.swing.JRadioButton radioButtonStat;
    private javax.swing.JButton saveSummsButton;
    private javax.swing.JButton selectAorta;
    private javax.swing.JSlider sliderMax;
    private javax.swing.JSlider sliderMin;
    private javax.swing.JComboBox summ1;
    private javax.swing.JComboBox summ2;
    private javax.swing.JToggleButton summButton;
    private javax.swing.JButton tridimObsButton;
    // End of variables declaration//GEN-END:variables
    
    

    private static BufferedImage saturateImage(BufferedImage buff) {
          BufferedImage b = new BufferedImage(buff.getWidth(), buff.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
          Graphics2D g = b.createGraphics();
          g.drawImage(buff, 0, 0, null);
          ShortProcessor sp = new ShortProcessor(b);
          sp.setMinAndMax(minBrightness, maxBrightness);
          return sp.getBufferedImage();
        
    }
    
    /**
     * Fait la somme de deux coupe temporelle
     * @param s1 coupe de départ
     * @param s2 coupe d'arrivée
     * @return Tableau de BufferedImage
     */
    private BufferedImage[] getSummSlices(int s1, int s2) {
        BufferedImage[] buffs = new BufferedImage[this.imagesPerBlock];
        float[][] imagePixels = this.patient.summSlices(s1, s2); 
        for (int frame = 0; frame < this.imagesPerBlock; frame++) {
              buffs[frame] = DicomUtils.pixelsToBufferedImage(this.patient.getWidth(), this.patient.getHeight(), imagePixels[frame]);
        }
        
        return buffs;
    }

    private void updateComponents() {
        
        System.out.println("On mets à jour les composants");
       //this.nbImages = p.getMaxDicomImage();
        this.pixelUnity = this.patient.getPixelUnity();
        currentImageID = 0;
        
        
        
        this.currentLUT = LutLoader.openLut("luts\\Red Hot.lut");
        
        
        
        try {
            this.imagesPerBlock = this.patient.getNbImages(0);
        } catch (BadParametersException ex) {
            Logger.getLogger(PatientSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.nbTimeSlices = this.patient.getNbBlocks();
        
         try {
            displayedImages = getImagesToDisplay(0);
        } catch (BadParametersException ex) {
            Logger.getLogger(PatientSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         //Slider de luminosité
         //On sature le moins possible l'image au début
        this.sliderMax.setValue(this.sliderMax.getMaximum()/2);
        this.sliderMin.setValue(this.sliderMax.getMinimum());
        
        
       
        
        
        //Slider Settings
        imageSlider.setMaximum(imagesPerBlock);
        imageSlider.setMinimum(1);
            //On ajoute la détection de la molette de la souris
        imageSlider.addMouseWheelListener((MouseWheelEvent e) -> {
            int inc = e.getWheelRotation();
            int val = imageSlider.getValue() - inc;
            imageSlider.setValue(val);
        });
        
        
        
       //frameList settings
            //On ajoute toutes les frames
        if (this.patient instanceof TAPSerie) {
            this.labelSliceList.setText("Coupe corporelle");
        }
        frameList.removeAllItems();
        this.summ1.removeAllItems();
        this.summ2.removeAllItems();
        for (int i=0; i<this.patient.getNbBlocks(); i++) {
            frameList.addItem("" + (i+1));
            this.summ1.addItem("" + (i+1));
            this.summ2.addItem("" + (i+1));
        }
            
        
        //imageIDTextField Settings
        imageIDTextField.setEditable(false);
        acquisitionTimeTextField.setEditable(false);
        
        
        this.revalidate();
       
    }
    
    public static void setDisplayedImage(BufferedImage[] buffs, String title) {
        displayedImages = buffs;
        display(currentImageID);
        imageSlider.setMaximum(buffs.length);
        currentImageTitle = title;
        WindowManager.setTempCurrentImage(currentImagePlus);
        
    }
    
    public static Roi getRoi() {
        return currentImagePlus.getRoi();
    }
  

    
}
