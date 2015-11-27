/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.AortaResults;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.ImageSizeException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import QuantIF_Project.patient.exceptions.TimeFrameOverflowException;
import QuantIF_Project.utils.DicomUtils;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.process.FloatProcessor;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;




/**
 * 
 * @author Cyriac
 *
 */
public class TEPSerie implements Serie{
	/**
	 * Nom anonymis� du patient
	 */
	protected String name;
	
	/**
	 * ID du patient
	 */
	protected final String id;
	
	/**
	 * Sexe du patient : 'M' ou 'F'
	 */
	protected String sex;
	
	/**
	 * Age du patient
	 */
	protected String age;
	
	/**
	 * Poids du patient (en Kg)
	 */
	protected String weight;
	
        /**
         * Liste des dicomImages
         */
        protected ArrayList<DicomImage> dicomImages;
       
	/**
	 * Liste des timesFrames
	 */
	ArrayList<TimeFrame> timeFrames;
        
        /**
         * Nombres de coupes temporelles
         */
        private int nbTimeFrames;
        
        /**
         * Nombres d'images par coupes temporelle
         */
        private int nbImagesPerTimeFrame;
        
        /**
         * largeur des images
         */
        protected int width;
        
        /**
         * hauteur des images
         */
        protected int height;
        
        /**
         * Somme de toutes les frames de cette séri
         */
        private float[][] summALL;
	
        /**
         * Resultats liés à la segmentation de l'aorte
         */
        private AortaResults aortaResults;
       
        /**
         * Vaut "true" si cette série patient fait partie d'une multi acquisition.
         */
        private boolean isPartOfMultAcq = false;
        
        /**
         * Vaut "true" si cette série patient est la première série de la multiAcquisition
         */
        private boolean isFirstInMultiAcq;
                
        /**
         * Instance de la muti série lié à ce patient
         */
        protected PatientMultiSeries parent;
        
        /**
         * Heure de début de la série
         */
        private String serieStartDate;
	
        /**
	 * On cree une instance de patient  l'aide du chemin vers le dossier où�
	 * sont contenus tous les fichiers DICOM concernant le patient
	 * @param dirPath chemin du repertoire où se trouve les fichiers DICOM
	 * @throws NotDirectoryException 
	 * 		Lev�e quand le chemin fourni ne correspond pas � un repertoire
	 * @throws DicomFilesNotFoundException
	 * 		Lev�e quand aucun fichier DICOM n'a �t� trouv� dans le r�pertoire
	 * @throws BadParametersException
	 * 		Lev�e quand les param�tres d'entr�e sont invalides
	 */
	public TEPSerie(String dirPath) throws NotDirectoryException, DicomFilesNotFoundException, BadParametersException {

            if (dirPath == null) {
                    throw new BadParametersException("Le chemin rentré est invalide.");
            }

            this.nbTimeFrames = 0;
            this.nbImagesPerTimeFrame = 0;


            this.dicomImages = DicomUtils.checkDicomImages(dirPath);

            //On récupère les paramètres du patient sur le premier fichier

            this.name = dicomImages.get(0).getAttribute(TagFromName.PatientName);

            this.id = dicomImages.get(0).getAttribute(TagFromName.PatientID);

            this.sex = dicomImages.get(0).getAttribute(TagFromName.PatientSex);

            //age sous la forme 045Y, on doit enlever le Y
            this.age = dicomImages.get(0).getAttribute(TagFromName.PatientAge).replace("Y", "");

            this.weight = dicomImages.get(0).getAttribute(TagFromName.PatientWeight);
            
         
            this.nbTimeFrames = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.NumberOfTimeSlices));

            this.nbImagesPerTimeFrame = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.NumberOfSlices));
            
            this.width = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.Columns));
            
            this.height = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.Rows));
            
            this.serieStartDate = dicomImages.get(0).getAttribute(TagFromName.StudyDate) + " " + dicomImages.get(0).getAttribute(TagFromName.StudyTime);

            this.timeFrames = new ArrayList<>(this.nbTimeFrames);
            checkTimeFrames();
            this.summALL = this.summSlices(0, this.nbTimeFrames-1);
            this.aortaResults = null;
            
            this.parent = null;  
            this.isFirstInMultiAcq = false;
            

           
            
            //Séparation de la série en sous-série
            /*
            String outputDirDyn1 = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\Test Multi acquisitions\\TEPDyn1\\";
            String outputDirDyn2 = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\Test Multi acquisitions\\TEPDyn2\\";
            String outputDirStatic = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\Test Multi acquisitions\\TEPStatic\\";
            //On vide les répertoires
            DicomUtils.emptyDirectory(new File(outputDirDyn1));
            DicomUtils.emptyDirectory(new File(outputDirDyn2));
            DicomUtils.emptyDirectory(new File(outputDirStatic));
            try {
                createSubDynAcquisition(outputDirDyn1, 2, 9);
                createSubDynAcquisition(outputDirDyn2, 27, 33);
                createSubDynAcquisition(outputDirStatic, 20);
            } catch (DicomException | IOException ex) {
                Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
            }
            */
               
	}
        
        /**
         * Construit une série patient
         * @param dirPath chemin du repertoire où se trouve les fichiers DICOM
         * @param multiAcq vaut "true" si la série fait partie d'une multi acquisition, "false" sinon
         * @param isFirstInMultiAcq
         * @throws NotDirectoryException 
	 * 		Lev�e quand le chemin fourni ne correspond pas � un repertoire
	 * @throws DicomFilesNotFoundException
	 * 		Lev�e quand aucun fichier DICOM n'a �t� trouv� dans le r�pertoire
	 * @throws BadParametersException
	 * 		Lev�e quand les param�tres d'entr�e sont invalidesoject.patient.exceptions.BadParametersException
         */
        public TEPSerie(String dirPath, boolean multiAcq, boolean isFirstInMultiAcq) throws NotDirectoryException, DicomFilesNotFoundException, BadParametersException {
            this(dirPath);
            this.isPartOfMultAcq = multiAcq;
            this.isFirstInMultiAcq = isFirstInMultiAcq;
        }
    
	
	/**
	 * Retourne la coupe temporelle ayant cet index.
	 * 
	 * @param index index de la coupe
         * @return la TimeFrame à cet index
	 * @throws BadParametersException
	 * 		Levee quand l'index demand� est soit inférieur ou égal à 0 soit supérieur au nombre de coupes
	 */
        @Override
	public TimeFrame getBlock(int index) throws BadParametersException {
            if (index < 0) 
                    throw new BadParametersException("L'indice doit être supérieur ou égal à 0");
            if (index > this.timeFrames.size())
                    throw new BadParametersException("Il n'y a pas de coupe temporelle à cet index. L'index est trop grand!");

            return this.timeFrames.get(index);
	}
	
       
	
        
	
	
	
        
        /**
         * Parcourt la liste des dicomImages et on les range dans les time frames
         * @param dicomImages 
         */
        private void checkTimeFrames() {
            //On récupère la liste des acquisition time
            ArrayList<String> ATList = new ArrayList<>();
            String AT;
            for (DicomImage di : dicomImages) {
                AT = di.getAttribute(TagFromName.AcquisitionTime);
                if (!ATList.contains(AT))
                    ATList.add(AT);
            }
            
            ATList.sort(null);
            
            //On crée tous les TimeFrame
            ATList.stream().forEach((at) -> {
                try {
                    this.timeFrames.add(new TimeFrame(this.nbImagesPerTimeFrame, at, this.width, this.height));
                } catch (BadParametersException ex) {
                    Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            for (DicomImage di : this.dicomImages) {
                try {
                    AT = di.getAttribute(TagFromName.AcquisitionTime);
                    int index = ATList.indexOf(AT);
                    this.timeFrames.get(index).addDicomImage(di);
                } catch (BadParametersException | ImageSizeException | TimeFrameOverflowException ex) {
                    Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
            //On met à jour les valeurs de temps des différentes frames
            for (TimeFrame tf : this.timeFrames) {
                tf.setTime();
            }
            
            System.out.println(this.timeFrames.size() + " Coupes détectées!!");
            this.nbTimeFrames = this.timeFrames.size();
            
        }
    
    
    
    public int getHeight(){
        return height;
    }
    
    public int getWidth() {
        return width;
    }
    
    /**
     * Somme les images d'une coupe temporelle à l'autre
     * @param startSlice indice de la coupe temporellle de déaprt
     * @param endSlice indice de la coupe temporelle de fin
     * @return Un tableau contenant la somme des images
     */
    
        @Override
    public float[][] summSlices(int startSlice, int endSlice) {
        
        //Tableau contenant les images sommés
        float[][] summImagesArray = new float[this.nbImagesPerTimeFrame][width*height];
        //Image avec les bonnes dimensions : à re remplir
        
       
        
      
        
        
        //On parcourt toutes les images de la coupe temporelle d'index startSlice
        for (int imageIndex = 0; imageIndex < this.nbImagesPerTimeFrame; imageIndex++) {
            //Pour chacune de ces images on la somme avec ceux des coupes allant de startSlice à endSlice
            
            
            //On cree un tableau de mm taille mais en float, car la somme des pixels risque de dépasser
            //la taille max du format SHORT
            float[] pixels = new float[width*height];
            
            for (int sliceIndex = startSlice ; sliceIndex <= endSlice; sliceIndex++) {
                try {
                    DicomImage dcmToSumm = this.timeFrames.get(sliceIndex).getDicomImage(imageIndex);
                    if (dcmToSumm != null) {
                        
                        
                        //Tableau contenant les valeurs de pixels de l'image a sommer
                        float[] buffPix = (float[]) dcmToSumm.getImageProcessor().getPixels();
                        
                      
                        for (int row = 0; row<width; ++row) {
                            for (int col = 0; col < height; ++col ) {
                                int pix = (int) buffPix[row * width + col];
                                if (buffPix[row * width + col] < 0) {
                                    //On le convertie en valeur INT UNSIGNED
                                    pix = 65536 + pix;
                                }
                               
                                pixels[row * width + col] += pix;
                                
                                
                                
                                
                            }
                            
                        }
                    }
                }
                catch (BadParametersException ex) {
                    Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
           
            
            
            summImagesArray[imageIndex] = pixels;
        }   
        
        System.out.println("Somme de " + (startSlice + 1)  + " à " + (endSlice + 1) + " faite!!!");
        
        //Affichage de la somme 
        /*
        try {
            DicomUtils.showImages(summImagesArray);
        } catch (BadParametersException ex) {
            Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
        }
                */
        return summImagesArray;
        
    }
    
   
        @Override
    public int getNbBlocks() {
        return this.nbTimeFrames;
    }
    
        @Override
    public int getNbImages(int blockIndex) throws BadParametersException {
         if (blockIndex < 0 )
            throw new BadParametersException("L'indice de la frame doit être supérieur ou égal à 0.");
        if (blockIndex >= timeFrames.size() )
            throw new BadParametersException("L'indice de la frame est trop grande.");
        return this.timeFrames.get(blockIndex).size();
    }
    
    @Override
    public String toString() {
            String str = "Présentation de la Serie TEP " + this.id + "\n";
            if(name == null)
                name = "N/A";
            if(sex == null)
                sex = "N/A";
            if(age == null)
                age = "N/A";
            if(weight == null)
                weight = "N/A";
            str += "-Nom : " + this.name + "\n";
            str += "-Sexe : " + this.sex + "\n";
            str += "-Age : " + this.age + " ans\n";
            str += "-Poids : " + this.weight + " Kg\n";

            return str;
    }

        @Override
    public String getPixelUnity() {
        String unit = null;
            try {
                //On récupère l'unité marqué dans le premier fichier dicom
                DicomImage di = this.timeFrames.get(0).getDicomImage(0);
                
                unit = di.getAttribute(TagFromName.Units);
            } catch (BadParametersException ex) {
                Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
            }
        return unit;
    }
    
    /**
     * Crée une acquisition TEP Dynamique DICOM à partir du patient courant
     * @param outputDir repertoire où seront crées les images DICOM
     * @param startIndex index de TimeFrame de début
     * @param endIndex index de TimeFrame de fin
     * @throws BadParametersException 
     * @throws DicomException
     * @throws IOException 
     */
    private void createSubDynAcquisition(String outputDir, int startIndex, int endIndex) throws BadParametersException, DicomException, IOException  {
        
        AttributeList list;
        TimeFrame tf;
        DicomImage dcm ;
        File file;
        String filePath;
        String transferSyntaxUID = "1.2.840.10008.1.2"; //Implicit VR Endian: Default Transfer Syntax for DICOM
        for (int frameIndex = startIndex; frameIndex < endIndex; frameIndex++) {
            tf = this.timeFrames.get(frameIndex);
            
            for (int imageIndex = 0; imageIndex<tf.size(); imageIndex++) {
                //Pour chaque image on recupére l'entête
               
                dcm = tf.getDicomImage(imageIndex);
                if (dcm != null) {
                    list = dcm.getAttributeList();
                    //On remplace le nombre de time frame dans l'entete des nouveaus images dicom
                    list.replaceWithValueIfPresent(TagFromName.NumberOfTimeSlices, Integer.toString(endIndex-startIndex));
                    //On crée le fichier ou sera sauvé la nouvelle image DICOM
                    filePath = outputDir + "IM"+ (frameIndex+1) +"."+(imageIndex+1);
                    file = new File(filePath);

                    list.write(file, transferSyntaxUID, true, true);
                }
                
            }
        }
        
        System.out.println("Sous Série dynamique de la coupe " + startIndex + " à " + endIndex + " crée avec succès dans \""+outputDir+"\"");
       
    }
    
    /**
     * Crée une acquisition statique à partir du patient courant
     * @param outputDir repertoire où seront crées les images DICOM
     * @param frameIndex index de la time frame 
     * @throws BadParametersException
     * @throws IOException
     * @throws DicomException 
     */
    private void createSubDynAcquisition(String outputDir, int frameIndex) throws BadParametersException, IOException, DicomException {
        AttributeList list;
        TimeFrame tf = this.timeFrames.get(frameIndex);
        DicomImage dcm ;
        File file;
        String filePath;
        String transferSyntaxUID = "1.2.840.10008.1.2"; //Implicit VR Endian: Default Transfer Syntax for DICOM
        for (int imageIndex = 0; imageIndex<tf.size(); imageIndex++) {
            //Pour chaque image on recupére l'entête

            dcm = tf.getDicomImage(imageIndex);
            if (dcm != null) {
                list = dcm.getAttributeList();
                //On remplace le nombre de time frame dans l'entete des nouveaus images dicom
                list.replaceWithValueIfPresent(TagFromName.NumberOfTimeSlices, Integer.toString(0));
                //On crée le fichier ou sera sauvé la nouvelle image DICOM
                filePath = outputDir + "IM"+ (frameIndex+1) +"."+(imageIndex+1);
                file = new File(filePath);

                list.write(file, transferSyntaxUID, true, true);
            }
                
         }
        System.out.println("Sous Série statique de la coupe " + frameIndex + " crée avec succès dans \""+outputDir+"\"");
    }
    
    /**
     * Trace les contours de la ROI et calcule la moyenne pour toutes les frames
     * @param roi
     * @param startIndex indice de la frame de début pour la somme 
     * @param endIndex indice de la frame de fin pour la somme 
     */
        @Override
    public void selectAorta(Roi roi, int startIndex, int endIndex)  {
        
        ImageStack imgStack = new ImageStack(this.width, this.height, null);

        //On fait la somme des Coupes allant de "startIndex" à "endIndex"
        FloatProcessor imgProc;
        ImagePlus summAll;
        float[][] summSlices;
        if (roi == null) {
            summSlices = this.summSlices(startIndex, endIndex);
        }
        else {
            summSlices = this.summALL;
        }
        

        
        //On parcout la liste des images pour les chargés dans le stack
        for (float[] pixels : summSlices) {


            imgProc = new FloatProcessor(width, height, pixels);
            imgStack.addSlice(imgProc);
        }
        
        //Ensemble des images résultantes de la somme
        summAll = new ImagePlus("", imgStack);


        //On cree les stacks des images non sommés
        ImagePlus[] framesStack = new ImagePlus[this.nbImagesPerTimeFrame];
        //à la position i, on aura un stack composé de tous les images d'indice i de chaque coupe temporelle
        for (int imageIndex = 0; imageIndex < this.nbImagesPerTimeFrame; imageIndex++) {
            //On doit recupérer toutes les images  à la frameIndex dans tous les coupes temporelles
            ImageStack stack = new ImageStack(width, height);
           
            //on parcourt toutes les coupes temporelles
            for (int sliceIndex = 0; sliceIndex < this.nbTimeFrames; sliceIndex++) {
                try {
                    DicomImage dcm  = this.timeFrames.get(sliceIndex).getDicomImage(imageIndex);
                    
                    if (dcm != null) {
                        stack.addSlice(dcm.getImageProcessor());
                    }
                    else {
                        FloatProcessor sp = new FloatProcessor(width, height);
                        stack.addSlice(sp);
                        
                    }
                } catch (BadParametersException ex) {
                    Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            framesStack[imageIndex] = new ImagePlus("", stack);
        }
        
        
        
        if (roi == null) {
            //on trace la roi et on calcule les résultats
            roiSelection(summAll, framesStack);
            
            
        }
        else {
            try {
                //On calcule les résultats à l'aide de cette Roi

                getRoiResults(roi, summAll, framesStack);
            } catch (BadParametersException ex) {
                Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

       

            
    }
    
    /**
     * Affiche l'interface pour la selection d'une ROI et fait le calcul de la moyenne dans la roi
     * @param summAll stack composé de la somme de toutes les images
     * @param framesStack tableau de stack, à la position i on a un stack est composé des images d'index i de chaque coupe temporelle
     * @param timeArray tableau des valeurs des temps d'acquisition des coupes temporelles
     */
    private void roiSelection(ImagePlus summAll, ImagePlus[] framesStack) {
        //On affiche la série d'images 
        summAll.setTitle("Série Dynamique TEP de départ -- " + this.nbTimeFrames + "x" + this.nbImagesPerTimeFrame);
        summAll.show();
        
        
        //On recupère la fenêtre d'affichage
        Window imgPlusWindow = WindowManager.getWindow(summAll.getTitle());
        
        //On y ajoute la gestion du ZOOM
        imgPlusWindow.addMouseWheelListener((MouseWheelEvent e) -> {
            int zoom = e.getWheelRotation();
            
            if (zoom < 0 && e.isControlDown()) {
                IJ.run("To Selection");
            } 
            else if (zoom > 0 && e.isControlDown()){
                IJ.run("Out");
            }
        });
        
        //GESTION DU DESSIN DE LA ROI
        RoiManager roiManager = new RoiManager();
        
        //Bouton de calcul
        JButton compileAndDisplayButton = new JButton();
        compileAndDisplayButton.setSize(100, 100);
        compileAndDisplayButton.setVisible(true);
        
        //On défini l'action du bouton
        compileAndDisplayButton.addActionListener( new java.awt.event.ActionListener() {
            private ImagePlus[] framesStack;
           
            private Roi roi;
            private int pressed;
            
            @Override
            
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               
               //Lorsqu'on appui sur le boutton
                if (roiManager.getCount() > 0) { 
                    System.out.println("#########################");
                    System.out.println("*  AFFICHER RESULTATS   *");
                    System.out.println("#########################");
                    
                        //On vérifie qu'une ROI a été ajoutée
                        this.pressed ++;
                        System.out.println("Selected Rois Array : " + Arrays.toString(roiManager.getSelectedRoisAsArray()));
                        roi = roiManager.getSelectedRoisAsArray()[0];

                        //On doit faire la multi mesure sur la bonne stack d'image
                        //
                        RoiManager subRoiManager = new RoiManager(true);
                        //subRoiManager.runCommand("reset");
                        System.out.println("Roi roi : " + roi);
                        System.out.println("Position roi : " + roi.getPosition());
                        
                       
                        ImagePlus stack = this.framesStack[roi.getPosition()-1];
                           
                      


                        subRoiManager.addRoi(roi);
                        subRoiManager.select(0);
                        //on fait de la muti- mesure sur la roi
                        ResultsTable multiMeasure = subRoiManager.multiMeasure(stack);
                       //System.out.println("Summ ALL Processor position 1 : " + summAll.getStack().getProcessor(1).toString());
                       //roi.getImage().show();
                       System.out.println("ID Image liée à la ROI : " + roi.getImage());
                       /*
                        if (summAll.getStack().getProcessor(roi.getPosition()) == roi.getImage().getProcessor()) {
                            System.out.println("La roi a été sélectionné sur la série d'image courante");
                        }
                        else {
                            System.out.println("La roi a été sélectionné sur une série autre que la série courante");
                        }
                       */
                        createResults(roi, multiMeasure,  summAll.getTitle());

                        if (!isPartOfMultiAcq())
                            plotResults();
                        
                    
                    
                            
                }
            }
            
            private ActionListener init(ImagePlus[] fStack) {
                this.framesStack = fStack;
               
               
                this.pressed = 0;
                System.out.println("Init button done");
                return this;
            }
        }.init(framesStack));
        compileAndDisplayButton.setText("Afficher les résultats");
      
        
        Window roiManagerWindow = WindowManager.getWindow("ROI Manager"); 
        
        roiManagerWindow.add(compileAndDisplayButton, BorderLayout.SOUTH);           
    }
    
    /**
     * Renvoie true si ce patient fait partie d'une multi acquisition
     */
    private boolean isPartOfMultiAcq() {
        return this.isPartOfMultAcq;
    }
    
    /**
     * Affiche les résultats
     */
    private void plotResults() {
        this.aortaResults.display(getPixelUnity());
    }
    
    /**
     * Construit les résultats de l'aorte
     * @param roi roi dessiné sur summALL
     * @param resultTable résultats liés à cette ROI
     * @param timeAxis l'axe des temps
     */
    private void createResults(Roi roi, ResultsTable resultTable, String imageTitle) {
        //on ajoute la liste des acquisition times a resultTable
        int resultTableSize = resultTable.getColumnAsDoubles(0).length;
        System.out.println("Size of ResultsTable : " + resultTableSize );
       
        //On parcourt les lignes du tableaux pour ajouter les valeur de temps
        for (int row = 0; row < resultTableSize; row++) {
            resultTable.setValue("Start Time(sec)", row, this.timeFrames.get(row).getStartTime());
            resultTable.setValue("Mid time (sec)", row, this.timeFrames.get(row).getMidTime());
            resultTable.setValue("End Time(sec)", row, this.timeFrames.get(row).getEndTime());
            
            
        }
        
        this.aortaResults = new AortaResults(this.name, roi, resultTable);
        
        
        //On avertit la multi série que la ROI a été sélectionner donc on peut commencer les calculs pour les autres séries
        if (this.parent != null && this.isFirstInMultiAcq)
            this.parent.roiSelected(roi);
        
        //On dessine sur l'image la ROI utilisée pour le calul
        ImagePlus impROI = WindowManager.getImage(imageTitle);
        impROI.setRoi(roi);
        
        
        
    }
    
    /**
     * calcul la courbe pour une ROI fournie
     * @param selectedRoi roi sur lequel s'effectuera le calcul
     * @param imgPlus stack composé de la somme de toutes les images
     * @param framesStack tableau de stack, à la position i on a un stack est composé des images d'index i de chaque coupe temporelle
     *
     */
    private void getRoiResults(Roi roi, ImagePlus summAll, ImagePlus[] framesStack) throws BadParametersException {
         //On récupère la série d'images dynamique affichée
        ImagePlus impROI = WindowManager.getImage("Série Dynamique TEP de fin -- " + this.nbTimeFrames + "x" + this.nbImagesPerTimeFrame);
       
        //Si elle n'est pas affichée, on l'affiche
        
        if (impROI == null) {
           
          
            summAll.setTitle("Série Dynamique TEP de fin -- " + this.nbTimeFrames + "x" + this.nbImagesPerTimeFrame);
           
           
            impROI = summAll;
            System.out.println("***RESET AFFICHAGE "+this.nbTimeFrames + "x" + this.nbImagesPerTimeFrame+"****");
            
            impROI.show();
        }
        Roi selectedRoi = roi;
       
        //On recupére la ROI dessinée sur la série si il y en a une
        if (impROI.getRoi() != null) {
            selectedRoi = impROI.getRoi();
            //selectedRoi.setPosition(roi.getPosition());
            
            
        }
        
        
        
        //summAll.setRoi(selectedRoi, true);
        
        
        //On recupère la fenêtre d'affichage
        Window imgPlusWindow = WindowManager.getWindow(summAll.getTitle());
        
        
        
        RoiManager roiManager = new RoiManager(true); //true -> Ce roimanager ne s'affiche pas
        
        roiManager.addRoi(selectedRoi);
        System.out.println("Selected ROI position : " + selectedRoi.getPosition());
        
       
        ImagePlus stack = framesStack[selectedRoi.getPosition()-1];
            
        
        //ImagePlus stackFrame = framesStack[selectedRoi.getPosition()-1];
        
        
        roiManager.select(0); //On selectionne la roi qu'on vient d'ajouter
        //on fait de la muti- mesure sur la roi
        ResultsTable multiMeasure = roiManager.multiMeasure(stack);
        
        //On crée les resultats
        createResults(selectedRoi, multiMeasure, impROI.getTitle());
        
         
    }
    
     

    @Override
    public AortaResults getAortaResults() {
       
        return this.aortaResults;
    }
    
        @Override
    public void setParent(PatientMultiSeries pms) {
        this.parent = pms;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Date getSerieStartDate() {
        return DicomUtils.dicomDateToDate(this.serieStartDate);
    }
    
    public BufferedImage[] getSummALL() {
        BufferedImage[] buffs = new BufferedImage[this.summALL.length];
        FloatProcessor fp;
        for (int i = 0; i < buffs.length; i++) {
            float[] pixels = summALL[i];
            fp = new FloatProcessor(width, height, pixels);
            buffs[i] = fp.getBufferedImage();
        }
        
        return buffs;
    }
   

   

   
   
    
    
        
}
