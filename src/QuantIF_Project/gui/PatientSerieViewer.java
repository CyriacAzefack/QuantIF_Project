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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionListener;
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
     * Tableau d'images a afficher
     */
    private static BufferedImage[] displayedImages;
    
    /**
     * Tableau d'images à afficher en plus (cas de ParaPET)
     */
    private static BufferedImage[] displayedImages2;
    
    private static boolean duoImage;
    
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
        
        patientMultiSeries = null;
        
        this.radioButtonDyn1.setVisible(false);
        this.radioButtonStat.setVisible(false);
        this.radioButtonDyn2.setVisible(false);
        
        
        
        //Image Panel settings
        imagePanel.removeAll();
        imagePanel.repaint();
        
        
        //Mise en place de la fenêtre d'affichage d'images
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

        maskFileChooser = new javax.swing.JFileChooser();
        popUpMenu = new javax.swing.JPopupMenu();
        saveImages = new javax.swing.JMenuItem();
        applyLUT = new javax.swing.JMenuItem();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        labelSliceList = new javax.swing.JLabel();
        summ1 = new javax.swing.JComboBox();
        summ2 = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        summButton = new javax.swing.JToggleButton();
        sliderMin = new javax.swing.JSlider();
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
        selectAorta = new javax.swing.JButton();
        tridimObsButton = new javax.swing.JButton();
        frameList = new javax.swing.JComboBox();
        sliderMax = new javax.swing.JSlider();
        imageTitle = new javax.swing.JTextField();
        maxLabel = new javax.swing.JLabel();
        minLabel = new javax.swing.JLabel();
        mainImageSlider = new javax.swing.JSlider();
        secImageSlider = new javax.swing.JSlider();

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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelSliceList.setFont(new java.awt.Font("Lucida Console", 1, 14)); // NOI18N
        labelSliceList.setText("Coupe temporelle");
        getContentPane().add(labelSliceList, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 180, 170, 20));

        summ1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summ1ActionPerformed(evt);
            }
        });
        getContentPane().add(summ1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 230, -1, -1));

        getContentPane().add(summ2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1360, 230, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jLabel8.setText("à");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1320, 230, 10, 20));

        summButton.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        summButton.setText("Sommer");
        summButton.setToolTipText("Somme des frames");
        summButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                summButtonActionPerformed(evt);
            }
        });
        getContentPane().add(summButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 220, 110, 40));

        sliderMin.setMaximum(100000);
        sliderMin.setSnapToTicks(true);
        sliderMin.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("sansserif", 1, 12), java.awt.Color.blue)); // NOI18N
        sliderMin.setValueIsAdjusting(true);
        sliderMin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMinStateChanged(evt);
            }
        });
        getContentPane().add(sliderMin, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 79, 570, 60));

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
        getContentPane().add(radioButtonDyn2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 350, 250, 40));

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
        getContentPane().add(radioButtonDyn1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 290, 270, 40));

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
        getContentPane().add(radioButtonStat, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 320, 210, 40));

        imagePanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 153), 3, true));
        imagePanel.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        imagePanel.setRequestFocusEnabled(false);
        imagePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                imagePanelMouseReleased(evt);
            }
        });
        imagePanel.setLayout(new javax.swing.BoxLayout(imagePanel, javax.swing.BoxLayout.LINE_AXIS));
        getContentPane().add(imagePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 50, 600, 600));

        nextButton.setBackground(java.awt.Color.cyan);
        nextButton.setFont(new java.awt.Font("Wide Latin", 1, 14)); // NOI18N
        nextButton.setText(">>");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        prevButton.setBackground(java.awt.Color.cyan);
        prevButton.setFont(new java.awt.Font("Wide Latin", 0, 12)); // NOI18N
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

        imageIDTextField.setEditable(false);
        imageIDTextField.setBackground(new java.awt.Color(153, 255, 255));
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

        getContentPane().add(imageOptionsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 650, 640, 120));

        selectAorta.setBackground(new java.awt.Color(51, 102, 255));
        selectAorta.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        selectAorta.setForeground(new java.awt.Color(255, 255, 255));
        selectAorta.setText("<html>Sélectionner Aorte</html>");
        selectAorta.setToolTipText("");
        selectAorta.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectAorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAortaActionPerformed(evt);
            }
        });
        getContentPane().add(selectAorta, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 330, 230, 50));

        tridimObsButton.setBackground(new java.awt.Color(204, 255, 255));
        tridimObsButton.setFont(new java.awt.Font("Lucida Console", 1, 14)); // NOI18N
        tridimObsButton.setText("3D Viewer");
        tridimObsButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tridimObsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tridimObsButtonActionPerformed(evt);
            }
        });
        getContentPane().add(tridimObsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 230, 60));

        frameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameListActionPerformed(evt);
            }
        });
        getContentPane().add(frameList, new org.netbeans.lib.awtextra.AbsoluteConstraints(1260, 170, -1, -1));

        sliderMax.setMaximum(100000);
        sliderMax.setSnapToTicks(true);
        sliderMax.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("sansserif", 1, 12), java.awt.Color.blue)); // NOI18N
        sliderMax.setValueIsAdjusting(true);
        sliderMax.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sliderMaxStateChanged(evt);
            }
        });
        getContentPane().add(sliderMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 9, 570, 60));
        sliderMax.getAccessibleContext().setAccessibleName("");

        imageTitle.setEditable(false);
        imageTitle.setBackground(new java.awt.Color(229, 90, 120));
        imageTitle.setFont(new java.awt.Font("Lucida Console", 1, 14)); // NOI18N
        getContentPane().add(imageTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 10, 310, 30));

        maxLabel.setBackground(new java.awt.Color(255, 255, 255));
        maxLabel.setFont(new java.awt.Font("Lucida Console", 1, 16)); // NOI18N
        maxLabel.setForeground(new java.awt.Color(0, 0, 204));
        maxLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        maxLabel.setText("Maximun");
        getContentPane().add(maxLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 20, 140, 50));

        minLabel.setBackground(new java.awt.Color(102, 102, 102));
        minLabel.setFont(new java.awt.Font("Lucida Console", 1, 16)); // NOI18N
        minLabel.setForeground(new java.awt.Color(0, 0, 204));
        minLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minLabel.setText("Minimun");
        getContentPane().add(minLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 80, 150, 50));

        mainImageSlider.setMinorTickSpacing(10);
        mainImageSlider.setPaintLabels(true);
        mainImageSlider.setPaintTicks(true);
        mainImageSlider.setSnapToTicks(true);
        mainImageSlider.setBorder(javax.swing.BorderFactory.createTitledBorder("Image Principale"));
        mainImageSlider.setName(""); // NOI18N
        mainImageSlider.setOpaque(true);
        mainImageSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mainImageSliderStateChanged(evt);
            }
        });
        getContentPane().add(mainImageSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 230, 70));

        secImageSlider.setMinorTickSpacing(10);
        secImageSlider.setPaintLabels(true);
        secImageSlider.setPaintTicks(true);
        secImageSlider.setSnapToTicks(true);
        secImageSlider.setToolTipText("");
        secImageSlider.setBorder(javax.swing.BorderFactory.createTitledBorder("Image secondaire"));
        secImageSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                secImageSliderStateChanged(evt);
            }
        });
        getContentPane().add(secImageSlider, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, -1, -1));

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
                    Main_Window.println("********** Segmentation Aorte Multi-Acquisition **********");
                    patientMultiSeries.selectAorta(startFrame, endFrame);
                    Main_Window.println("********** Fin Segmentation Aorte Multi-Acquisition **********");
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
       
        display(currentImageID);
        
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

    private void mainImageSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mainImageSliderStateChanged
        displayDuoImage(currentImageID);
    }//GEN-LAST:event_mainImageSliderStateChanged

    private void secImageSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_secImageSliderStateChanged
       displayDuoImage(currentImageID);
    }//GEN-LAST:event_secImageSliderStateChanged
    
    /**
     * Affiche l'image d'id imageID dans la fenetre
     * @param imageID id de l'image à afficher
     * @throws BadParametersException
     *      Levée lorsqu'aucune image ne correspond à imageID
     */
    private static void display(int imageID){
        
        if (duoImage) 
            displayDuoImage(imageID);
        else {
            BufferedImage bufferedImage;

            bufferedImage = displayedImages[imageID];

            imageTitle.setText(currentImageTitle);



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
        
    }
    
    /**
     * Affiche une image dans la fenêtre
     * @param b BufferedImage à afficher
     */
    private static void displayImage(BufferedImage b) {
       //On redimensionne l'image
        BufferedImage bufferedImage = rescale(b, IMAGE_SIZE, IMAGE_SIZE);
        
       
        
        //On sature l'image avec les barres min & max
        
        bufferedImage = saturateImage(bufferedImage);
        
         //On applique la lookUpTable
        //bufferedImage = applyLut(bufferedImage, this.currentLUT);
        
        BufferedImage buff = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        
        Graphics graphics = buff.getGraphics();
        
        graphics.drawImage(bufferedImage, 0, 0, null);
        
        graphics.dispose();
        
        //On l'affiche dans la zone prévu a cet effet
        ByteProcessor bp = new ByteProcessor( buff);
        currentImagePlus.setProcessor(bp);
       
        //System.out.println("Panel component " + imagePanel.getComponentCount());
         
    }
    
    /**
     * Affiche les deux images avec le poids de l'affichage
     * @param imageID Indice de l'image à afficher
     */
    public static void displayDuoImage(int imageID) {
        BufferedImage b1, b2;
        
        //System.out.println("AFFICHAGE DUO!!!!!!!!!");
        
        
        b1 = displayedImages[imageID];
        b2 = displayedImages2[imageID];
        
        
        
        imageTitle.setText(currentImageTitle);
        
        //On récupère les poids des différents images
        float alpha = (float)mainImageSlider.getValue()/(float)(mainImageSlider.getMaximum() - mainImageSlider.getMinimum());
        float beta = (float)secImageSlider.getValue()/(float)(secImageSlider.getMaximum() - secImageSlider.getMinimum());
        
        
        
        
        
        ColorProcessor cp1 = new ColorProcessor(b1);
        ColorProcessor cp2 = new ColorProcessor(b2);
        
        ImageProcessor resultIP = DicomUtils.drawImageOnImage(cp1.convertToFloatProcessor(), cp2.convertToFloatProcessor(), alpha, beta);
        
        ImagePlus imp = new ImagePlus("", resultIP);
        
        
        
       
        
        displayImage(resultIP.getBufferedImage());
        
        
       
        
        
        imageIDTextField.setText((imageID + 1) + " / " + (displayedImages.length));
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
            displayedImages2 = new BufferedImage[displayedImages.length];
            duoImage = false;
        } catch (BadParametersException ex) {
            Logger.getLogger(PatientSerieViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         //Slider de luminosité
         //On sature le moins possible l'image au début
        this.sliderMax.setValue(this.sliderMin.getMaximum());
        this.sliderMin.setValue(this.sliderMin.getMinimum());
        
        //Gestion slider duo affichage
        mainImageSlider.setEnabled(false);
        mainImageSlider.setVisible(false);
        secImageSlider.setEnabled(false);
        secImageSlider.setVisible(false);
       
        
        
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
       
    }
    
    /**
     * Mets à jour les images à afficher
     * @param buffs
     * @param title 
     */
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
     * Mets à jour les images à afficher
     * @param images1
     * @param images2
     * @param title 
     */
    public static void setDisplayedImage(BufferedImage[] images1, BufferedImage[] images2, String title) {
        
                
        duoImage = true;
        displayedImages = images1;
        displayedImages2 = images2;
        currentImageTitle = title;
        
        System.out.println("Duo Displayed Images setted up!!");

        
       //Sliders Images
        mainImageSlider.setEnabled(true);
        mainImageSlider.setVisible(true);
        secImageSlider.setEnabled(true);
        secImageSlider.setVisible(true);
        
        
        displayDuoImage(currentImageID);
        



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
        int startX = p.xpoints[0]*width/PatientSerieViewer.IMAGE_SIZE;
        int endX = p.xpoints[1]*width/PatientSerieViewer.IMAGE_SIZE;
        int startY = p.ypoints[0]*height/PatientSerieViewer.IMAGE_SIZE;
        int endY = p.ypoints[2]*height/PatientSerieViewer.IMAGE_SIZE;

        Roi roiResult = new Roi(startX, startY, endX-startX, endY-startY);
        return roiResult;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextField acquisitionTimeTextField;
    private javax.swing.JMenuItem applyLUT;
    private static javax.swing.JComboBox frameList;
    private static javax.swing.JTextField imageIDTextField;
    private javax.swing.JPanel imageOptionsPanel;
    private javax.swing.JPanel imagePanel;
    private static javax.swing.JSlider imageSlider;
    private static javax.swing.JTextField imageTitle;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JLabel labelSliceList;
    private static javax.swing.JSlider mainImageSlider;
    private javax.swing.JFileChooser maskFileChooser;
    private static javax.swing.JLabel maxLabel;
    private static javax.swing.JLabel minLabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JPopupMenu popUpMenu;
    private javax.swing.JButton prevButton;
    private javax.swing.JRadioButton radioButtonDyn1;
    private javax.swing.JRadioButton radioButtonDyn2;
    private javax.swing.JRadioButton radioButtonStat;
    private javax.swing.JMenuItem saveImages;
    private javax.swing.JButton saveSummsButton;
    private static javax.swing.JSlider secImageSlider;
    private javax.swing.JButton selectAorta;
    private static javax.swing.JSlider sliderMax;
    private static javax.swing.JSlider sliderMin;
    private javax.swing.JComboBox summ1;
    private javax.swing.JComboBox summ2;
    private javax.swing.JToggleButton summButton;
    private javax.swing.JButton tridimObsButton;
    // End of variables declaration//GEN-END:variables

    
    
    
  

    
}
