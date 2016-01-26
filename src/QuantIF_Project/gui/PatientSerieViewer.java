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
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
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
import java.awt.Polygon;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
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
    private static PatientMultiSeries patientMultiSeries;
    
    
    /**
     * Nombre total d'images à afficher
     */
    private int nbImages;
    
    /**
     * ID de l'image en cours d'affichage
     */
    private static int currentImageID;
    
    
    
    
    
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
    public static int IMAGE_WIDTH; 
    
    public static int IMAGE_HEIGHT; 
    
    public static int width;
    
    public static int height;
    
    
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
        
        width = p.getWidth();
        height = p.getHeight();
        
        IMAGE_WIDTH = 800;
        IMAGE_HEIGHT = 800;
        
        
        patientMultiSeries = null;
        
        this.radioButtonDyn1.setVisible(false);
        this.radioButtonStat.setVisible(false);
        this.radioButtonDyn2.setVisible(false);
        
       
        
        //Image Panel settings
        imagePanel.removeAll();
        imagePanel.repaint();
        
       
        currentImagePlus.show();
        
        System.out.println("###################");
        System.out.println("IMAGE SIZE = " + IMAGE_WIDTH + " x " + IMAGE_HEIGHT);
        
        WindowManager.setTempCurrentImage(currentImagePlus);
        Frame frame = WindowManager.getCurrentWindow(); //getFrame(currentImageTitle);
        
        frame.dispose();
        ImageCanvas canvas = (ImageCanvas) frame.getComponent(0);
       
        
        //On affiche l'image dans le panel
        //System.out.println(comp.toString());
        
        
        imagePanel.add(canvas);
        
        
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
        
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                display(currentImageID);
            }
        });
        
        
     
        
    }
    
    public PatientSerieViewer(PatientMultiSeries pms) {
        this(pms.getStartDynSerie());
        patientMultiSeries = pms;
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
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        maskFileChooser = new javax.swing.JFileChooser();
        popUpMenu = new javax.swing.JPopupMenu();
        saveImages = new javax.swing.JMenuItem();
        applyLUT = new javax.swing.JMenuItem();
        LeftButtonsPanel = new javax.swing.JPanel();
        tridimObsButton = new javax.swing.JButton();
        selectAorta = new javax.swing.JButton();
        allImagePanel = new javax.swing.JPanel();
        imagePanel = new javax.swing.JPanel();
        imageTitle = new javax.swing.JTextField();
        imageOptionsPanel = new javax.swing.JPanel();
        imageIDTextField = new javax.swing.JTextField();
        changeImage = new javax.swing.JPanel();
        prevButton = new javax.swing.JButton();
        imageSlider = new javax.swing.JSlider();
        nextButton = new javax.swing.JButton();
        tempsAcq = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        acquisitionTimeTextField = new javax.swing.JTextField();
        rightButtonsPanel = new javax.swing.JPanel();
        sliders = new javax.swing.JPanel();
        sliderMax = new javax.swing.JSlider();
        sliderMin = new javax.swing.JSlider();
        framesOptions = new javax.swing.JPanel();
        labelSliceList = new javax.swing.JLabel();
        summ1 = new javax.swing.JComboBox();
        summ2 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        summButton = new javax.swing.JToggleButton();
        frameList = new javax.swing.JComboBox();
        seriesChoice = new javax.swing.JPanel();
        saveSummsButton = new javax.swing.JButton();
        radioButtonDyn1 = new javax.swing.JRadioButton();
        radioButtonStat = new javax.swing.JRadioButton();
        radioButtonDyn2 = new javax.swing.JRadioButton();

        maskFileChooser.setDialogTitle("Choisir le dossier du masque");
        maskFileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        saveImages.setText("Sauvegarder les images");
        saveImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImagesActionPerformed(evt);
            }
        });
        popUpMenu.add(saveImages);

        applyLUT.setText("Appliquer une LUT");
        popUpMenu.add(applyLUT);

        setBorder(null);
        setTitle("Patient Viewer");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFocusable(false);
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(800, 800));
        try {
            setSelected(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        setVisible(true);
        getContentPane().setLayout(new java.awt.BorderLayout(50, 100));

        LeftButtonsPanel.setLayout(new java.awt.GridLayout(5, 1, 10, 50));

        tridimObsButton.setBackground(new java.awt.Color(204, 255, 255));
        tridimObsButton.setFont(new java.awt.Font("Lucida Console", 1, 14)); // NOI18N
        tridimObsButton.setText("3D Viewer");
        tridimObsButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tridimObsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tridimObsButtonActionPerformed(evt);
            }
        });
        LeftButtonsPanel.add(tridimObsButton);

        selectAorta.setBackground(new java.awt.Color(51, 102, 255));
        selectAorta.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        selectAorta.setForeground(new java.awt.Color(255, 255, 255));
        selectAorta.setText("<html>Sélectionner Aorte</html>");
        selectAorta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectAorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAortaActionPerformed(evt);
            }
        });
        LeftButtonsPanel.add(selectAorta);

        getContentPane().add(LeftButtonsPanel, java.awt.BorderLayout.WEST);

        allImagePanel.setLayout(new java.awt.BorderLayout());

        imagePanel.setBorder(null);
        imagePanel.setMaximumSize(new java.awt.Dimension(800, 800));
        imagePanel.setRequestFocusEnabled(false);
        imagePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                imagePanelMouseReleased(evt);
            }
        });
        imagePanel.setLayout(new java.awt.GridBagLayout());
        allImagePanel.add(imagePanel, java.awt.BorderLayout.CENTER);

        imageTitle.setEditable(false);
        imageTitle.setBackground(new java.awt.Color(229, 90, 120));
        imageTitle.setFont(new java.awt.Font("Lucida Console", 1, 14)); // NOI18N
        allImagePanel.add(imageTitle, java.awt.BorderLayout.NORTH);

        imageOptionsPanel.setLayout(new java.awt.BorderLayout());

        imageIDTextField.setEditable(false);
        imageIDTextField.setBackground(new java.awt.Color(153, 255, 255));
        imageIDTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        imageIDTextField.setText("               ");
        imageIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imageIDTextFieldActionPerformed(evt);
            }
        });
        imageOptionsPanel.add(imageIDTextField, java.awt.BorderLayout.NORTH);

        changeImage.setLayout(new java.awt.BorderLayout());

        prevButton.setBackground(java.awt.Color.cyan);
        prevButton.setFont(new java.awt.Font("Wide Latin", 0, 12)); // NOI18N
        prevButton.setText("<<");
        prevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevButtonActionPerformed(evt);
            }
        });
        changeImage.add(prevButton, java.awt.BorderLayout.WEST);

        imageSlider.setPaintLabels(true);
        imageSlider.setPaintTicks(true);
        imageSlider.setSnapToTicks(true);
        imageSlider.setValue(0);
        imageSlider.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        imageSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        imageSlider.setOpaque(true);
        imageSlider.setPreferredSize(new java.awt.Dimension(512, 23));
        imageSlider.setValueIsAdjusting(true);
        imageSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                imageSliderStateChanged(evt);
            }
        });
        changeImage.add(imageSlider, java.awt.BorderLayout.CENTER);

        nextButton.setBackground(java.awt.Color.cyan);
        nextButton.setFont(new java.awt.Font("Wide Latin", 1, 14)); // NOI18N
        nextButton.setText(">>");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        changeImage.add(nextButton, java.awt.BorderLayout.EAST);

        imageOptionsPanel.add(changeImage, java.awt.BorderLayout.CENTER);

        tempsAcq.setLayout(new javax.swing.BoxLayout(tempsAcq, javax.swing.BoxLayout.X_AXIS));

        jLabel4.setText("Temps d'acquisition (secondes)");
        tempsAcq.add(jLabel4);

        acquisitionTimeTextField.setEditable(false);
        acquisitionTimeTextField.setBackground(new java.awt.Color(204, 255, 255));
        acquisitionTimeTextField.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        acquisitionTimeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acquisitionTimeTextFieldActionPerformed(evt);
            }
        });
        tempsAcq.add(acquisitionTimeTextField);

        imageOptionsPanel.add(tempsAcq, java.awt.BorderLayout.SOUTH);

        allImagePanel.add(imageOptionsPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(allImagePanel, java.awt.BorderLayout.CENTER);

        rightButtonsPanel.setLayout(new java.awt.GridLayout(3, 1));

        sliders.setLayout(new java.awt.GridLayout(2, 1));

        sliderMax.setMaximum(100000);
        sliderMax.setSnapToTicks(true);
        sliderMax.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Max", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("sansserif", 1, 12), java.awt.Color.blue)); // NOI18N
        sliderMax.setValueIsAdjusting(true);
        sliderMax.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMaxStateChanged(evt);
            }
        });
        sliders.add(sliderMax);

        sliderMin.setMaximum(100000);
        sliderMin.setSnapToTicks(true);
        sliderMin.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "Min", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("sansserif", 1, 12), java.awt.Color.blue)); // NOI18N
        sliderMin.setValueIsAdjusting(true);
        sliderMin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMinStateChanged(evt);
            }
        });
        sliders.add(sliderMin);

        rightButtonsPanel.add(sliders);

        labelSliceList.setFont(new java.awt.Font("Lucida Console", 1, 14)); // NOI18N
        labelSliceList.setText("Coupe temporelle");

        summ1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summ1ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel8.setText("à");

        summButton.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        summButton.setText("Sommer");
        summButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summButtonActionPerformed(evt);
            }
        });

        frameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameListActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout framesOptionsLayout = new javax.swing.GroupLayout(framesOptions);
        framesOptions.setLayout(framesOptionsLayout);
        framesOptionsLayout.setHorizontalGroup(
            framesOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 310, Short.MAX_VALUE)
            .addGroup(framesOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(framesOptionsLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(framesOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(framesOptionsLayout.createSequentialGroup()
                            .addComponent(labelSliceList, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(frameList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(framesOptionsLayout.createSequentialGroup()
                            .addComponent(summButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(40, 40, 40)
                            .addComponent(summ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(64, 64, 64)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(30, 30, 30)
                            .addComponent(summ2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        framesOptionsLayout.setVerticalGroup(
            framesOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 406, Short.MAX_VALUE)
            .addGroup(framesOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(framesOptionsLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(framesOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(framesOptionsLayout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(labelSliceList, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(frameList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(20, 20, 20)
                    .addGroup(framesOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(summButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(framesOptionsLayout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addGroup(framesOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(summ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(summ2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        rightButtonsPanel.add(framesOptions);

        seriesChoice.setLayout(new java.awt.GridLayout(4, 1));

        saveSummsButton.setText("Sauvegarder toutes les images sommées");
        saveSummsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSummsButtonActionPerformed(evt);
            }
        });
        seriesChoice.add(saveSummsButton);

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
        seriesChoice.add(radioButtonDyn1);

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
        seriesChoice.add(radioButtonStat);

        radioButtonDyn2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        radioButtonDyn2.setText("Série Dynamique de fin");
        radioButtonDyn2.setEnabled(false);
        radioButtonDyn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonDyn2ActionPerformed(evt);
            }
        });
        seriesChoice.add(radioButtonDyn2);

        rightButtonsPanel.add(seriesChoice);

        getContentPane().add(rightButtonsPanel, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    /**
     * Lance la sélection de l'aorte
     * @param evt 
     */
    private void selectAortaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAortaActionPerformed
        
        
        if (getRoi() == null) {
            JOptionPane.showMessageDialog(null, "Aucune Roi détectée!!");
            return;
        }
        int startFrame, endFrame;
       
        // On récupère la liste des frames
        ComboBoxModel model = frameList.getModel();
         String[] frames = new String[model.getSize()];
        for (int i = 0; i < model.getSize(); i++) {
            frames[i] = "" + (i + 1);
        }
        
        //On demande à l'utilisateur de choisir la frame de début et celle de fin pour la somme
        String result = (String) JOptionPane.showInputDialog(null, "Choisir la coupe de début", "Coupes temporelles à sommer", JOptionPane.INFORMATION_MESSAGE, null, frames, frames[0]);
        if(result != null) {
            startFrame = -1 + Integer.valueOf(result);
            
            String[] frames2 = Arrays.copyOfRange(frames, startFrame + 1, frames.length);
            result = (String)JOptionPane.showInputDialog(null, "Choisir la coupe de fin", "Coupes temporelles à sommer", JOptionPane.INFORMATION_MESSAGE, null, frames2, frames2[0]);
            
            if (result != null) {
                endFrame = -1 + Integer.valueOf(result);
                JOptionPane.showMessageDialog(null, "Somme de la coupe " + (startFrame+1) + " - " + (endFrame+1));
                if (patient != null) {     
                    Roi roi = getRoi();
                    if (roi == null)
                        JOptionPane.showMessageDialog(this, "Aucune ROI dessinée.\nVeuillez tracer une ROI autour de l'aorte.");
                    else
                        patient.selectAorta(getRoi(), startFrame, endFrame);
                }
                else {

                    patientMultiSeries.selectAorta(startFrame, endFrame);
                }
            }
        }
       
        
        
    }//GEN-LAST:event_selectAortaActionPerformed

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
            float[][] imagePixels = patient.summSlices(s1, s2); 
            currentImageTitle = "Somme de la frame " + (s1 + 1) + " à " + (s2 + 1);
            
            
            for (int image = 0; image < this.imagesPerBlock; image++) {
                FloatProcessor fp = new FloatProcessor(width, height, imagePixels[image]);
                displayedImages[image] = fp.getBufferedImage();
            }
            
            Main_Window.println(currentImageTitle);
            
            
            
        }
        else {
            try {
                //On affiche les frames
                PatientSerieViewer.displayedImages = getImagesToDisplay(frameList.getSelectedIndex());
            } catch (BadParametersException ex) {
                Logger.getLogger(PatientSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        display(PatientSerieViewer.currentImageID);
        
    }//GEN-LAST:event_summButtonActionPerformed

    private void sliderMinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMinStateChanged
        PatientSerieViewer.minBrightness = this.sliderMin.getValue();
        
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
        patient = patientMultiSeries.getStartDynSerie();
        updateComponents();
        display(currentImageID);
        Main_Window.println("Affichage de la série dynamique de depart");
    }//GEN-LAST:event_radioButtonDyn1ActionPerformed

    private void radioButtonStatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonStatActionPerformed
        patient = patientMultiSeries.getStaticSerie();
        updateComponents();
        display(currentImageID);
        Main_Window.println("Affichage de la série statique TAP");
    }//GEN-LAST:event_radioButtonStatActionPerformed

    private void radioButtonDyn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonDyn2ActionPerformed
        patient = patientMultiSeries.getEndDynSerie();
        updateComponents();
        display(currentImageID);
        Main_Window.println("Affichage de la série dynamique de fin");
    }//GEN-LAST:event_radioButtonDyn2ActionPerformed

    private void radioButtonDyn1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_radioButtonDyn1StateChanged
       
    }//GEN-LAST:event_radioButtonDyn1StateChanged

    private void radioButtonStatStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_radioButtonStatStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_radioButtonStatStateChanged

    private void tridimObsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tridimObsButtonActionPerformed
        ImageStack imgStack = new ImageStack(patient.getWidth(), patient.getHeight(), null);
        ImageProcessor imProc;
       
        
        for (BufferedImage buff : displayedImages) {
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
            @Override
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
        
        Main_Window.println("Affichage 3D de \""+ currentImageTitle +"\"");
         
    }//GEN-LAST:event_tridimObsButtonActionPerformed

    private void acquisitionTimeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acquisitionTimeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_acquisitionTimeTextFieldActionPerformed

    private void imageIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imageIDTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_imageIDTextFieldActionPerformed

    private void imageSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_imageSliderStateChanged

        if (imageSlider.getValue() > 0)
            currentImageID = imageSlider.getValue() - 1;
        display(currentImageID);
    }//GEN-LAST:event_imageSliderStateChanged

    private void prevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevButtonActionPerformed

        if (currentImageID > 1 ) {
            imageSlider.setValue(--currentImageID);
        }
        //display(currentImageID);

    }//GEN-LAST:event_prevButtonActionPerformed

    
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed

        if (currentImageID < this.imagesPerBlock ) {
            imageSlider.setValue(++currentImageID);

        }

    }//GEN-LAST:event_nextButtonActionPerformed

    private void sliderMaxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderMaxStateChanged
        maxBrightness = this.sliderMax.getValue();
       
        display(currentImageID);
    }//GEN-LAST:event_sliderMaxStateChanged

    private void imagePanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imagePanelMouseReleased
        if (evt.isPopupTrigger()) {
            popUpMenu.show(this, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_imagePanelMouseReleased
    
    /**
     * Sauvegarde des images affichées
     * @param evt 
     */
    private void saveImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImagesActionPerformed
        FloatProcessor[] fps = new FloatProcessor[displayedImages.length];
        BufferedImage b;
        for (int i = 0; i < displayedImages.length; i++) {
            b = displayedImages[i];
            float[] pixels = new float[b.getWidth()*b.getHeight()];
            pixels = b.getData().getPixel(0, 0, pixels);
            
            fps[i] = new FloatProcessor(b.getWidth(), b.getHeight(), pixels);
        }
        
        String dirPath = IJ.getFilePath("Choisir le dossier de sauvegarde");
        
        DicomUtils.saveImages(fps, dirPath);
    }//GEN-LAST:event_saveImagesActionPerformed
    
    /**
     * Affiche l'image d'id imageID dans la fenetre
     * @param imageID id de l'image à afficher
     * @throws BadParametersException
     *      Levée lorsqu'aucune image ne correspond à imageID
     */
    private static void display(int imageID){
        
        BufferedImage bufferedImage;
        
        bufferedImage = displayedImages[imageID];
        
        imageTitle.setText(currentImageTitle);
        
        if (currentImageTitle.equals("Images des Ki ParaPET")) {
            displayColorImage(bufferedImage);
        }
        else {
            displayImage(bufferedImage);
        }
        
       
        
        
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
        //On mets à jour la taille
        if (imagePanel.getWidth() != 0 && imagePanel.getHeight() != 0) {
            IMAGE_WIDTH = imagePanel.getWidth()*3/4;
            IMAGE_HEIGHT = imagePanel.getHeight()*3/4;
            
            if (IMAGE_WIDTH > IMAGE_HEIGHT)
                IMAGE_WIDTH = IMAGE_HEIGHT;
            else 
                IMAGE_HEIGHT = IMAGE_WIDTH;
        }
        
       //On redimensionne l'image
        BufferedImage bufferedImage = rescale(b, IMAGE_WIDTH, IMAGE_HEIGHT);
        
       
        
        //On sature l'image
        bufferedImage = saturateImage(bufferedImage);
        
         //On applique la lookUpTable
        //bufferedImage = applyLut(bufferedImage, this.currentLUT);
        
        
        
        //On l'affiche dans la zone prévu a cet effet
       
        currentImagePlus.setProcessor(new ByteProcessor(bufferedImage));
       
        //System.out.println("Panel component " + imagePanel.getComponentCount());
         
    }
    
     /**
     * Affiche une image dans la fenêtre
     * @param b BufferedImage à afficher
     */
    private static void displayColorImage(BufferedImage b) {
        
       
       //On redimensionne l'image
        BufferedImage bufferedImage = rescale(b, IMAGE_WIDTH, IMAGE_HEIGHT);
        
       
        
        //On sature l'image
        //bufferedImage = saturateImage(bufferedImage);
        
         //On applique la lookUpTable
        //bufferedImage = applyLut(bufferedImage, this.currentLUT);
        
        
        
        //On l'affiche dans la zone prévu a cet effet
       
        currentImagePlus.setProcessor(new ColorProcessor(bufferedImage));
       
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
       

        ColorProcessor cp = new ColorProcessor(source);
        
        ImageProcessor ip = cp.resize(newWidth, newHeight, true);
        
       
      
        
        
        /* On retourne l'image bufferisée, qui est une image. */ 
        return ip.getBufferedImage(); 
        
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
        
        Block block = patient.getBlock(sliceIndex);
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
        float[][] imagePixels = patient.summSlices(s1, s2); 
        for (int frame = 0; frame < this.imagesPerBlock; frame++) {
              buffs[frame] = DicomUtils.pixelsToBufferedImage(patient.getWidth(), patient.getHeight(), imagePixels[frame]);
        }
        
        return buffs;
    }
    
     @SuppressWarnings("unchecked")
    private void updateComponents() {
        
        System.out.println("On mets à jour les composants");
       //this.nbImages = p.getMaxDicomImage();
        this.pixelUnity = PatientSerieViewer.patient.getPixelUnity();
        currentImageID = imageSlider.getValue();
        
        this.selectAorta.setToolTipText("<html>Calcul de la courbe<br/> d'entrée artérielle<html>");
        this.tridimObsButton.setToolTipText("Aperçu 3D");
        
        
        
        
        try {
            this.imagesPerBlock = patient.getNbImages(0);
        } catch (BadParametersException ex) {
            Logger.getLogger(PatientSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.nbTimeSlices = patient.getNbBlocks();
        
         try {
            displayedImages = getImagesToDisplay(0);
        } catch (BadParametersException ex) {
            Logger.getLogger(PatientSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         //Slider de luminosité
         //On sature le moins possible l'image au début
        this.sliderMax.setValue(this.sliderMin.getMaximum());
        this.sliderMin.setValue(this.sliderMin.getMinimum());
        
        
       
        
        
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
        if (patient instanceof TAPSerie) {
            this.labelSliceList.setText("Coupe corporelle");
        }
        frameList.removeAllItems();
        this.summ1.removeAllItems();
        this.summ2.removeAllItems();
        String[] list = new String[patient.getNbBlocks()];
        for (int i=0; i<patient.getNbBlocks(); i++) {
            String s = "" + (i+1);
            frameList.addItem(s);
            summ1.addItem(s);
            summ2.addItem(s);
        }
        
        
        
            
        
        //imageIDTextField Settings
        imageIDTextField.setEditable(false);
        acquisitionTimeTextField.setEditable(false);
        
        
        this.revalidate();
       
        display(currentImageID);
    }
    
    public static void setDisplayedImage(BufferedImage[] buffs, String title) {
        displayedImages = buffs;
        currentImageID = 0;
        display(currentImageID);
        
        imageSlider.setMaximum(buffs.length);
        imageSlider.setValue(1);
        currentImageTitle = title;
        WindowManager.setTempCurrentImage(currentImagePlus);
       
        
    }
    
    /**
     * Récupère la ROI déssinée dans l'image
     * @return 
     */
    public static Roi getRoi() {
        
        Roi roi = currentImagePlus.getRoi();
        
        if (roi != null) {
            roi = adjustRoi(roi);
            roi.setPosition(currentImageID);
        }
       return roi;
    }
    
    private static Roi adjustRoi(Roi roi) {
        //On ajuste la ROI à l'image initiale
        Polygon p = roi.getPolygon();
        int startX = p.xpoints[0]*width/PatientSerieViewer.IMAGE_WIDTH;
        int endX = p.xpoints[1]*width/PatientSerieViewer.IMAGE_WIDTH;
        int startY = p.ypoints[0]*height/PatientSerieViewer.IMAGE_HEIGHT;
        int endY = p.ypoints[2]*height/PatientSerieViewer.IMAGE_HEIGHT;

        Roi roiResult = new Roi(startX, startY, endX-startX, endY-startY);
        return roiResult;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel LeftButtonsPanel;
    private static javax.swing.JTextField acquisitionTimeTextField;
    private javax.swing.JPanel allImagePanel;
    private javax.swing.JMenuItem applyLUT;
    private javax.swing.JPanel changeImage;
    private static javax.swing.JComboBox frameList;
    private javax.swing.JPanel framesOptions;
    private static javax.swing.JTextField imageIDTextField;
    private javax.swing.JPanel imageOptionsPanel;
    private static javax.swing.JPanel imagePanel;
    private static javax.swing.JSlider imageSlider;
    private static javax.swing.JTextField imageTitle;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel labelSliceList;
    private javax.swing.JFileChooser maskFileChooser;
    private javax.swing.JButton nextButton;
    private javax.swing.JPopupMenu popUpMenu;
    private javax.swing.JButton prevButton;
    private javax.swing.JRadioButton radioButtonDyn1;
    private javax.swing.JRadioButton radioButtonDyn2;
    private javax.swing.JRadioButton radioButtonStat;
    private javax.swing.JPanel rightButtonsPanel;
    private javax.swing.JMenuItem saveImages;
    private javax.swing.JButton saveSummsButton;
    private javax.swing.JButton selectAorta;
    private javax.swing.JPanel seriesChoice;
    private javax.swing.JSlider sliderMax;
    private javax.swing.JSlider sliderMin;
    private javax.swing.JPanel sliders;
    private javax.swing.JComboBox summ1;
    private javax.swing.JComboBox summ2;
    private javax.swing.JToggleButton summButton;
    private javax.swing.JPanel tempsAcq;
    private javax.swing.JButton tridimObsButton;
    // End of variables declaration//GEN-END:variables

    
    
    
  

    
}
