/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuantIF_Project.gui;

import QuantIF_Project.patient.DicomImage;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.serie.Block;
import QuantIF_Project.serie.TAPSerie;
import QuantIF_Project.utils.DicomUtils;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.LutLoader;
import ij.process.ByteProcessor;
import ij.process.LUT;
import ij.process.ShortProcessor;
import ij.process.StackConverter;
import ij3d.Content;
import ij3d.Image3DUniverse;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.vecmath.Color3f;










/**
 *
 * @author Cyriac
 */
public class TAPSerieViewer extends javax.swing.JInternalFrame {
    
    /**
     * TEPSerie à afficher
     */
    private TAPSerie tapSerie;
    
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
     * Taille de l'image affichée
     */
    private final static int IMAGE_SIZE = 600;
    
    /**
     * Creates new form AfficherImages
     * @param tapSerie Acquisition corps entier
     * 
     */
    public TAPSerieViewer(TAPSerie tapSerie) {
        initComponents();
        this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        
        this.tapSerie = tapSerie;
        this.patientMultiSeries = null;
        
        if (!this.tapSerie.isPartOfMultAcq()) {
            this.chooseBodyBlockButton.setVisible(false);
            this.chooseBodyBlockButton.setEnabled(false);
        }
        updateComponents();
        
        
        display(currentImageID);
        
        //On empeche la fenêtre interne de pouvoir être déplacée
        BasicInternalFrameUI bifui = (BasicInternalFrameUI) this.getUI();
        Component northPane = bifui.getNorthPane();
        MouseMotionListener[] motionListeners = (MouseMotionListener[]) northPane.getListeners(MouseMotionListener.class);

        for (MouseMotionListener listener: motionListeners)
            northPane.removeMouseMotionListener(listener);
   
        
        //On gère les radioButton pr les différentes séries
     
        
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
        jLabel4 = new javax.swing.JLabel();
        acquisitionTimeTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        sliderMax = new javax.swing.JSlider();
        labelSliceList = new javax.swing.JLabel();
        frameList = new javax.swing.JComboBox();
        sliderMin = new javax.swing.JSlider();
        jLabel7 = new javax.swing.JLabel();
        tridimObsButton = new javax.swing.JButton();
        chooseBodyBlockButton = new javax.swing.JButton();

        maskFileChooser.setDialogTitle("Choisir le dossier du masque");
        maskFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setBorder(null);
        setTitle("Choisir une coupe corporelle");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFocusable(false);
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
        getContentPane().add(nextButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 610, 50, 40));

        prevButton.setText("<<");
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });
        getContentPane().add(prevButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 610, 50, 40));

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
        getContentPane().add(imageSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 670, 550, 50));

        imageIDTextField.setEditable(false);
        imageIDTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        imageIDTextField.setText("               ");
        imageIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageIDTextFieldActionPerformed(evt);
            }
        });
        getContentPane().add(imageIDTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 610, 60, 28));

        imageLabel.setMaximumSize(new java.awt.Dimension(700, 700));
        imageLabel.setMinimumSize(new java.awt.Dimension(512, 512));
        imageLabel.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                imageLabelMouseWheelMoved(evt);
            }
        });
        getContentPane().add(imageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, 600, 600));

        jLabel4.setText("Temps d'acquisition (secondes)");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 730, -1, -1));

        acquisitionTimeTextField.setEditable(false);
        acquisitionTimeTextField.setBackground(new java.awt.Color(204, 255, 255));
        acquisitionTimeTextField.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        acquisitionTimeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acquisitionTimeTextFieldActionPerformed(evt);
            }
        });
        getContentPane().add(acquisitionTimeTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 720, 60, 30));

        jLabel5.setText("Max");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 20, -1, -1));

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
        getContentPane().add(sliderMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 10, 400, -1));

        labelSliceList.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        labelSliceList.setText("Coupe corporelle");
        getContentPane().add(labelSliceList, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 110, 140, 20));

        frameList.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        frameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameListActionPerformed(evt);
            }
        });
        getContentPane().add(frameList, new org.netbeans.lib.awtextra.AbsoluteConstraints(1280, 110, 110, 30));

        sliderMin.setMaximum(100000);
        sliderMin.setSnapToTicks(true);
        sliderMin.setValueIsAdjusting(true);
        sliderMin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMinStateChanged(evt);
            }
        });
        getContentPane().add(sliderMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 40, 400, -1));

        jLabel7.setText("Min");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 40, -1, -1));

        tridimObsButton.setText("Observation 3D");
        tridimObsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tridimObsButtonActionPerformed(evt);
            }
        });
        getContentPane().add(tridimObsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 170, 60));

        chooseBodyBlockButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        chooseBodyBlockButton.setText("CHOISIR CETTE COUPE CORPORELLE");
        chooseBodyBlockButton.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        chooseBodyBlockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseBodyBlockButtonActionPerformed(evt);
            }
        });
        getContentPane().add(chooseBodyBlockButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 240, 280, 80));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Affiche la prochaine image
     * @param evt 
     */
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        
        if (this.currentImageID < this.imagesPerBlock ) {
            this.imageSlider.setValue(++this.currentImageID);
            
       }
        
    }//GEN-LAST:event_nextButtonActionPerformed

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed
        
        if (this.currentImageID > 1 ) {
           this.imageSlider.setValue(--this.currentImageID);
       }
        //display(currentImageID);
                           
    }//GEN-LAST:event_prevButtonActionPerformed

    private void imageSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_imageSliderStateChanged
        
        if (imageSlider.getValue() > 0)
            this.currentImageID = imageSlider.getValue() - 1;
        display(this.currentImageID);
    }//GEN-LAST:event_imageSliderStateChanged

    private void imageIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageIDTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_imageIDTextFieldActionPerformed
    
    private void acquisitionTimeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acquisitionTimeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_acquisitionTimeTextFieldActionPerformed

    private void sliderMaxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMaxStateChanged
        this.maxBrightness = this.sliderMax.getValue();
        this.currentLUT.max = this.sliderMax.getValue();
        display(this.currentImageID);
    }//GEN-LAST:event_sliderMaxStateChanged

    private void frameListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameListActionPerformed
        try {
            if (this.frameList.getSelectedIndex() >= 0) {
                int frameIndex = this.frameList.getSelectedIndex();
                this.displayedImages = getImagesToDisplay(frameIndex);
                this.imagesPerBlock = this.displayedImages.length;
                imageSlider.setMaximum(imagesPerBlock);
                display(this.currentImageID);

                //On affiche l'acquisition time
                //this.acquisitionTimeTextField.setText("" + patient.getBlock(frameIndex).getMidTime());
                //this.tridimObsButtonActionPerformed(evt);
            }
            
        } catch (BadParametersException ex) {
            Logger.getLogger(TAPSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }//GEN-LAST:event_frameListActionPerformed

    private void imageLabelMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_imageLabelMouseWheelMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_imageLabelMouseWheelMoved

    private void sliderMinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMinStateChanged
        this.minBrightness = this.sliderMin.getValue();
         this.currentLUT.min = this.sliderMin.getValue();
        display(this.currentImageID);
    }//GEN-LAST:event_sliderMinStateChanged

    private void tridimObsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tridimObsButtonActionPerformed
        
        ImageStack imgStack = new ImageStack(this.tapSerie.getWidth(), this.tapSerie.getHeight(), null);
        ByteProcessor byteProc;
       
        
        for (BufferedImage buff : this.displayedImages) {
            BufferedImage b = new BufferedImage(this.tapSerie.getWidth(), this.tapSerie.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = b.createGraphics();
            g.drawImage(buff, null, 0, 0);
            
            byteProc = new ByteProcessor(b);
            imgStack.addSlice(byteProc);
        }
        ImagePlus imgPlus = new ImagePlus("", imgStack);
       
       
        //imgPlus.show();
        //On stoppe le thread si déjà ouvert
        Image3DUniverse univ = new Image3DUniverse();
        
        Thread thread;
        thread = new Thread("Affichage 3D") {
            @Override
            public void run() {
               
                new StackConverter(imgPlus).convertToGray8();

                // Create a universe and show it
                
                univ.show();
                
                // Add the image as a volume rendering
                Content c = univ.addVoltex(imgPlus);
                //c.setColor(new Color3f(255,255,255));
                
                
            }
        };
        thread.start();
        
         
    }//GEN-LAST:event_tridimObsButtonActionPerformed

    private void chooseBodyBlockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseBodyBlockButtonActionPerformed
        this.tapSerie.setChoosenBodyBlock(this.frameList.getSelectedIndex());
        
        this.dispose();
        
        
        
       
    }//GEN-LAST:event_chooseBodyBlockButtonActionPerformed
    
    /**
     * Affiche l'image d'id imageID dans la fenetre
     * @param imageID id de l'image à afficher
     * @throws BadParametersException
     *      Levée lorsqu'aucune image ne correspond à imageID
     */
    private void display(int imageID){
        
        BufferedImage bufferedImage;
        
        bufferedImage = this.displayedImages[imageID];
          
        //On redimensionne l'image
        bufferedImage = rescale(bufferedImage, IMAGE_SIZE, IMAGE_SIZE);
        
       
        
        //On sature l'image
        bufferedImage = saturateImage(bufferedImage);
        
         //On applique la lookUpTable
        //bufferedImage = applyLut(bufferedImage, this.currentLUT);
        
        
        
        //On l'affiche dans la zone prévu a cet effet
        ImageIcon ii = new ImageIcon(bufferedImage);         
        imageLabel.setIcon(ii);
        imageIDTextField.setText((imageID + 1) + " / " + (this.displayedImages.length));
        /*
        this.champ1.setText(dcm.getAttribute(TagFromName.StudyInstanceUID));
        this.champ2.setText(""+dcm.getTimeSlice());
        this.champ3.setText(""+dcm.getSlice());
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
        
        Block block = this.tapSerie.getBlock(sliceIndex);
        BufferedImage[] buffs = new BufferedImage[block.size()];
        for (int frame = 0; frame < block.size(); frame++) {
            DicomImage dcm = block.getDicomImage(frame);
            if (dcm == null) {
                BufferedImage b= new BufferedImage(tapSerie.getWidth(), tapSerie.getHeight(), BufferedImage.TYPE_INT_RGB);
                buffs[frame] = b;
            }
            else {
                buffs[frame] = dcm.getBufferedImage();
            }
            
        }
        
        return buffs;
    }
    
  
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField acquisitionTimeTextField;
    private javax.swing.JButton chooseBodyBlockButton;
    private javax.swing.JComboBox frameList;
    private javax.swing.JTextField imageIDTextField;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JSlider imageSlider;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel labelSliceList;
    private javax.swing.JFileChooser maskFileChooser;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton prevButton;
    private javax.swing.JSlider sliderMax;
    private javax.swing.JSlider sliderMin;
    private javax.swing.JButton tridimObsButton;
    // End of variables declaration//GEN-END:variables
    
    

    private BufferedImage saturateImage(BufferedImage buff) {
          BufferedImage b = new BufferedImage(buff.getWidth(), buff.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
          Graphics2D g = b.createGraphics();
          g.drawImage(buff, 0, 0, null);
          ShortProcessor sp = new ShortProcessor(b);
          sp.setMinAndMax(this.minBrightness, this.maxBrightness);
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
        float[][] imagePixels = this.tapSerie.summSlices(s1, s2); 
        for (int frame = 0; frame < this.imagesPerBlock; frame++) {
              buffs[frame] = DicomUtils.pixelsToBufferedImage(this.tapSerie.getWidth(), this.tapSerie.getHeight(), imagePixels[frame]);
        }
        
        return buffs;
    }

    private void updateComponents() {
        
        System.out.println("On mets à jour les composants");
       //this.nbImages = p.getMaxDicomImage();
        this.pixelUnity = this.tapSerie.getPixelUnity();
        this.currentImageID = 0;
        
        
        
        this.currentLUT = LutLoader.openLut("luts\\Red Hot.lut");
        
        
        
        this.imagesPerBlock = this.tapSerie.getNbImages(0);
        this.nbTimeSlices = this.tapSerie.getNbBlocks();
        
         try {
            this.displayedImages = getImagesToDisplay(0);
        } catch (BadParametersException ex) {
            Logger.getLogger(TAPSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         //Slider de luminosité
         //On sature le moins possible l'image au début
        this.sliderMax.setValue(this.sliderMax.getMaximum());
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
        if (this.tapSerie instanceof TAPSerie) {
            this.labelSliceList.setText("Coupe corporelle");
        }
        this.frameList.removeAllItems();
       
        for (int i=0; i<this.tapSerie.getNbBlocks(); i++) 
            this.frameList.addItem("" + (i+1));
            
            
        
        //imageIDTextField Settings
        imageIDTextField.setEditable(false);
        acquisitionTimeTextField.setEditable(false);
        
        this.repaint();
       
    }
  

    
}
