/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuantIF_Project.gui;



import QuantIF_Project.patient.DicomImage;
import QuantIF_Project.patient.Patient;
import QuantIF_Project.patient.TimeFrame;
import QuantIF_Project.patient.exceptions.BadParametersException;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.LutLoader;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.process.LUT;
import ij.process.ShortProcessor;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;






/**
 *
 * @author Cyriac
 */
public class AfficherImages extends javax.swing.JInternalFrame {
    
    /**
     * Patient à afficher
     */
    private Patient patient;
    
    /**
     * Nombre total d'images à afficher
     */
    private int nbImages;
    
    /**
     * ID de l'image en cours d'affichage
     */
    private int currentImageID;
    
    
    
    /**
     * LUT en cours
     */
    private LUT currentLUT;
    
    /**
     * Luminosité des images
     */
    private int maxBrightness;
    private int minBrightness;
   
    
      
    /**
     * Somme d'images a afficher
     */
    private BufferedImage[] displayedImages;
    
    /**
     * nombre d'images par coupe temporelle
     */
    private int framesPerTimeSlice;
    
    /**
     * Nombre de coupes temporelle
     */
    private int nbTimeSlices;
    
    /**
     * Creates new form AfficherImages
     * @param p patient en cours
     * 
     */
    public AfficherImages(Patient p) {
        initComponents();
        this.patient = p;
        //this.nbImages = p.getMaxDicomImage();
        
        this.currentImageID = 0;
        
        
        
        this.currentLUT = LutLoader.openLut("luts\\Red Hot.lut");
        
       
        
        this.framesPerTimeSlice = this.patient.getFramesPerTimeSlice();
        this.nbTimeSlices = this.patient.getNbTimeSlices();
        
         try {
            this.displayedImages = getImagesToDisplay(0);
        } catch (BadParametersException ex) {
            Logger.getLogger(AfficherImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         //Slider de luminosité
         //On sature le moins possible l'image au début
        this.sliderMax.setValue(this.sliderMax.getMaximum());
        this.sliderMin.setValue(this.sliderMax.getMinimum());
        
        
       
        
        
        //Slider Settings
        imageSlider.setMaximum(framesPerTimeSlice);
        imageSlider.setMinimum(1);
        
        
        
       //frameList settings
            //On ajoute toutes les frames
        for (int i=0; i<this.patient.getNbTimeSlices(); i++) {
            this.frameList.addItem("Frame " + (i+1));
            this.summ1.addItem("Frame " + (i+1));
            this.summ2.addItem("Frame " + (i+1));
        }
            
        
        //imageIDTextField Settings
        imageIDTextField.setEditable(false);
        
        display(currentImageID);
        
        
     
        
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
        nextButton = new javax.swing.JButton();
        prevButton = new javax.swing.JButton();
        imageSlider = new javax.swing.JSlider();
        imageIDTextField = new javax.swing.JTextField();
        imageLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        champ1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        champ2 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        champ3 = new javax.swing.JTextField();
        selectAorta = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        champ4 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        sliderMax = new javax.swing.JSlider();
        jLabel6 = new javax.swing.JLabel();
        frameList = new javax.swing.JComboBox();
        summ1 = new javax.swing.JComboBox();
        summ2 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        summButton = new javax.swing.JToggleButton();
        sliderMin = new javax.swing.JSlider();
        jLabel7 = new javax.swing.JLabel();

        maskFileChooser.setDialogTitle("Choisir le dossier du masque");
        maskFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Viewer");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nextButton.setText(">>");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        getContentPane().add(nextButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 530, 50, 40));

        prevButton.setText("<<");
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });
        getContentPane().add(prevButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 530, 50, 40));

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
        getContentPane().add(imageSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 580, 550, 50));

        imageIDTextField.setEditable(false);
        imageIDTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        imageIDTextField.setText("               ");
        imageIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageIDTextFieldActionPerformed(evt);
            }
        });
        getContentPane().add(imageIDTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 530, 60, 28));

        imageLabel.setMaximumSize(new java.awt.Dimension(512, 512));
        imageLabel.setMinimumSize(new java.awt.Dimension(512, 512));
        imageLabel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                imageLabelMouseWheelMoved(evt);
            }
        });
        getContentPane().add(imageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 512, 512));

        jLabel1.setText("UID Study ");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 40, -1, 20));

        champ1.setEditable(false);
        getContentPane().add(champ1, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 40, 560, -1));

        jLabel2.setText("timeSlice");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 70, -1, 20));

        champ2.setEditable(false);
        getContentPane().add(champ2, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 70, 560, -1));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 30, 240, 10));

        jLabel3.setText("slice");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 100, -1, 20));

        champ3.setEditable(false);
        champ3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                champ3ActionPerformed(evt);
            }
        });
        getContentPane().add(champ3, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 100, 560, -1));

        selectAorta.setText("Selectionner Aorte");
        selectAorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAortaActionPerformed(evt);
            }
        });
        getContentPane().add(selectAorta, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 233, 150, 40));

        jLabel4.setText("Acquisition Time");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 140, -1, -1));

        champ4.setEditable(false);
        champ4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                champ4ActionPerformed(evt);
            }
        });
        getContentPane().add(champ4, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 140, 560, -1));

        jLabel5.setText("Max");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 200, -1, -1));

        sliderMax.setMaximum(200000);
        sliderMax.setMinorTickSpacing(1);
        sliderMax.setPaintLabels(true);
        sliderMax.setPaintTicks(true);
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
        getContentPane().add(sliderMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 190, 570, -1));

        jLabel6.setText("Coupe temporelle");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 310, 100, 20));

        frameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameListActionPerformed(evt);
            }
        });
        getContentPane().add(frameList, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 310, 110, 30));

        summ1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summ1ActionPerformed(evt);
            }
        });
        getContentPane().add(summ1, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 380, -1, -1));

        getContentPane().add(summ2, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 380, -1, -1));

        jLabel8.setText("à");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 380, 10, 20));

        summButton.setText("Sommer");
        summButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summButtonActionPerformed(evt);
            }
        });
        getContentPane().add(summButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 380, -1, -1));

        sliderMin.setMaximum(200000);
        sliderMin.setPaintLabels(true);
        sliderMin.setPaintTicks(true);
        sliderMin.setSnapToTicks(true);
        sliderMin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMinStateChanged(evt);
            }
        });
        getContentPane().add(sliderMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 250, 570, -1));

        jLabel7.setText("Min");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 250, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Affiche la prochaine image
     * @param evt 
     */
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
       if (this.currentImageID < this.nbImages ) {
           this.currentImageID += 1;
       }
       
       display(currentImageID);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        if (this.currentImageID > 1 ) {
           this.currentImageID -= 1;
       }
        display(currentImageID);
                                 
    }//GEN-LAST:event_prevButtonActionPerformed

    private void imageSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_imageSliderStateChanged
        
        if (imageSlider.getValue() > 0)
            this.currentImageID = imageSlider.getValue() - 1;
        display(this.currentImageID);
    }//GEN-LAST:event_imageSliderStateChanged

    private void imageIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageIDTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_imageIDTextFieldActionPerformed

    private void champ3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_champ3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_champ3ActionPerformed
    
    /**
     * Affiche les 3 plans de l'image
     * @param evt 
     */
    private void selectAortaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAortaActionPerformed
       
        try {
            
            IJ.run("Close All");
            //Stack d'images à afficher
            ImageStack imgStack = new ImageStack(this.patient.getWidth(), this.patient.getHeight(), null);
            
            ImageProcessor imgProc;
            ImagePlus summAll;
           
            displayedImages = this.patient.summSlices(0, this.nbTimeSlices-1);
            //On parcout la liste des images pour les chargés dans le stack
            for (BufferedImage buff : displayedImages) {
             
                ImagePlus impTemp = new ImagePlus("", buff);
                
                imgProc = impTemp.getProcessor();
                imgStack.addSlice(imgProc);
            }
            
            summAll = new ImagePlus("", imgStack);
           //imgPlus.setLut(currentLUT);
            
            //On cree les stacks des images non sommés
            ImagePlus[] framesStack = new ImagePlus[this.framesPerTimeSlice];
            //à la position i, on aura un stack composé de tous les images d'indice i de chaque coupe temporelle
            for (int frameIndex = 0; frameIndex < this.framesPerTimeSlice; frameIndex++) {
                //On doit recupérer toutes les images  à la frameIndex dans tous les coupes temporelles
                ImageStack stack = new ImageStack(this.patient.getWidth(), this.patient.getHeight(), null);
                ImageProcessor proc;
               
                
                //on parcourt toutes les coupes temporelles
                for (int slice = 0; slice < this.nbTimeSlices; slice++) {
                    DicomImage dcm  = this.patient.getTimeFrame(slice).getDicomImage(frameIndex);
                    BufferedImage buffe;
                    if (dcm != null) {
                       buffe = dcm.getBufferedImage();
                    }
                    else {
                        buffe = new BufferedImage(this.patient.getWidth(), this.patient.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
                    }
                    ImagePlus tmp = new ImagePlus("", buffe );
                    proc = tmp.getProcessor();
                    stack.addSlice(proc);
                }
                framesStack[frameIndex] = new ImagePlus("Frame " + Integer.toString(frameIndex+1), stack);
                
            }
            System.out.println("Frame Stack size : " + framesStack.length + " x " + framesStack[0].getImageStackSize());
            roiSelection(summAll, framesStack);
            
            //On enregistre le stack d'images ainsi crée dans un dossier temporaire
            String tmpFile = "tmp/summAll.tif";
            IJ.saveAs(summAll, "tif", tmpFile);
           
            
         
        } catch (BadParametersException ex) {
            Logger.getLogger(AfficherImages.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }//GEN-LAST:event_selectAortaActionPerformed

    private void champ4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_champ4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_champ4ActionPerformed

    private void sliderMaxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMaxStateChanged
        this.maxBrightness = this.sliderMax.getValue();
        display(this.currentImageID);
    }//GEN-LAST:event_sliderMaxStateChanged

    private void frameListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameListActionPerformed
        try {
            this.displayedImages = getImagesToDisplay(this.frameList.getSelectedIndex());
            display(this.currentImageID);
            
        } catch (BadParametersException ex) {
            Logger.getLogger(AfficherImages.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }//GEN-LAST:event_frameListActionPerformed

    private void summ1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summ1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_summ1ActionPerformed

    private void summButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_summButtonActionPerformed
        if (this.summButton.isSelected()) {
            int s1 = this.summ1.getSelectedIndex();
            int s2 = this.summ2.getSelectedIndex();
            this.displayedImages = this.patient.summSlices(s1, s2); 
            
        }
        else {
            try {
                //On affiche les frames
                this.displayedImages = getImagesToDisplay(this.frameList.getSelectedIndex());
            } catch (BadParametersException ex) {
                Logger.getLogger(AfficherImages.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        display(this.currentImageID);
        
    }//GEN-LAST:event_summButtonActionPerformed

    private void imageLabelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_imageLabelMouseWheelMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_imageLabelMouseWheelMoved

    private void sliderMinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMinStateChanged
        this.minBrightness = this.sliderMin.getValue();
        display(this.currentImageID);
    }//GEN-LAST:event_sliderMinStateChanged
    
    /**
     * Affiche l'image d'id imageID dans la fenetre
     * @param imageID id de l'image à afficher
     * @throws BadParametersException
     *      Levée lorsqu'aucune image ne correspond à imageID
     */
    private void display(int imageID){
        
        BufferedImage bufferedImage;
        
        //Si aucune ROI n'a été chargée, on affiche l'image de base
        
        bufferedImage = this.displayedImages[imageID];
          
       
        
        //On applique la lookUpTable
        
        //On redimensionne l'image
        bufferedImage = rescale(bufferedImage, 512, 512);
        
        //On ajuste le contraste
        //bufferedImage = ajustContrast(bufferedImage);
        //On gere la luminosité
        
        
       
        bufferedImage = ajustContrast(bufferedImage);
        
        //bufferedImage = applyLut(bufferedImage, this.currentLUT);
        
        
        ImageIcon ii = new ImageIcon(bufferedImage);        
        
   
        imageLabel.setIcon(ii);
        
        imageIDTextField.setText((imageID + 1) + " / " + (this.displayedImages.length));
        /*
        this.champ1.setText(dcm.getAttribute(TagFromName.StudyInstanceUID));
        this.champ2.setText(""+dcm.getTimeSlice());
        this.champ3.setText(""+dcm.getSlice());
        this.champ4.setText(dcm.getAttribute(TagFromName.AcquisitionTime));
        */
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
        BufferedImage buf = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB); 

        
        Graphics2D g = buf.createGraphics(); 
        
        int fWidth = newWidth/source.getWidth() ;
        int fHeight = newHeight/source.getHeight();
        
        //On applique une transformation affine aux pixels
        AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
        g.drawRenderedImage(source, at);
        
      
        
        
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
        
        TimeFrame tf = this.patient.getTimeFrame(sliceIndex);
        BufferedImage[] buffs = new BufferedImage[tf.size()];
        for (int frame = 0; frame < tf.size(); frame++) {
            DicomImage dcm = tf.getDicomImage(frame);
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
    private javax.swing.JTextField champ1;
    private javax.swing.JTextField champ2;
    private javax.swing.JTextField champ3;
    private javax.swing.JTextField champ4;
    private javax.swing.JComboBox frameList;
    private javax.swing.JTextField imageIDTextField;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JSlider imageSlider;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JFileChooser maskFileChooser;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JButton selectAorta;
    private javax.swing.JSlider sliderMax;
    private javax.swing.JSlider sliderMin;
    private javax.swing.JComboBox summ1;
    private javax.swing.JComboBox summ2;
    private javax.swing.JToggleButton summButton;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Affiche l'interface pour la selection d'une ROI
     * @param imgPlus stack composé de la somme de toutes les images
     * @param framesStack tableau de stack, à la position i on a un stack est composé des images d'index i de chaque coupe temporelle
     */
    private void roiSelection(ImagePlus imgPlus, ImagePlus[] framesStack) {
        imgPlus.show();
        
        
        RoiManager roiManager = new RoiManager();
        roiManager.runCommand("reset");
        JButton b = new JButton();
        b.setSize(100, 100);
        b.setVisible(true);
        
        //On défini l'action du bouton
        b.addActionListener( new java.awt.event.ActionListener() {
            private ImagePlus[] framesStack;
            private Roi roi;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               //Lorsqu'on appui sur le boutton
                if (roiManager.getRoisAsArray().length > 0) { //On vérifie qu'une ROI a été ajoutée
                    int selectedIndex = roiManager.getSelectedIndex();
                    System.out.println("Selected Rois Array : " + Arrays.toString(roiManager.getSelectedRoisAsArray()));
                    roi = roiManager.getSelectedRoisAsArray()[0];
                    
                     //On doit faire la multi mesure sur la bonne stack d'image
                    //
                    RoiManager subRoiManager = new RoiManager(true);
                    //subRoiManager.runCommand("reset");
                    System.out.println("Roi roi : " + roi);
                    System.out.println("Position roi : " + roi.getPosition());
                    ImagePlus stackFrame = this.framesStack[roi.getPosition()-1];
                    stackFrame.show();
                    
                    subRoiManager.addRoi(roi);
                    subRoiManager.select(0);
                    //on fait de la muti- mesure sur la roi
                    ResultsTable multiMeasure = subRoiManager.multiMeasure(stackFrame);
                    multiMeasure.show("Resultats");
                    
                    double[] means = multiMeasure.getColumnAsDoubles(1); // On recupère la valeur moyenne à l'intérieur de la ROI
                    double[] x = new double[means.length];
                    for(int i = 0; i < x.length; i++) {
                        x[i] = i+1;
                    }
                    System.out.println(Arrays.toString(x));
                    System.out.println(Arrays.toString(means));

                    Curve chart = new Curve("Résultats graphique", "Moyenne de la ROI", x, means);
                    chart.setVisible( true );
                            
                }
            }
            
            private ActionListener init(ImagePlus[] fStack) {
                this.framesStack = fStack;
                
                System.out.println("Init button done");
                return this;
            }
        }.init(framesStack));
        b.setText("Display the results");
        WindowManager.getWindow("ROI Manager").add(b, BorderLayout.SOUTH);           
    }

    private BufferedImage ajustContrast(BufferedImage buff) {
          BufferedImage b = new BufferedImage(buff.getWidth(), buff.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
          Graphics2D g = b.createGraphics();
          g.drawImage(buff, 0, 0, null);
          ShortProcessor sp = new ShortProcessor(b);
          sp.setMinAndMax(this.minBrightness, this.maxBrightness);
          return sp.getBufferedImage();
        
    }
  

    
}
