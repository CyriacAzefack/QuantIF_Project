package QuantIF_Project.patient;

import QuantIF_Project.gui.Curve;
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
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import org.jfree.ui.RefineryUtilities;




/**
 * 
 * @author Cyriac
 *
 */
public final class PatientSerie {
	/**
	 * Nom anonymis� du patient
	 */
	private String name;
	
	/**
	 * ID du patient
	 */
	private final String id;
	
	/**
	 * Sexe du patient : 'M' ou 'F'
	 */
	private String sex;
	
	/**
	 * Age du patient
	 */
	private String age;
	
	/**
	 * Poids du patient (en Kg)
	 */
	private String weight;
	
	/**
	 * Liste des timesFrames
	 */
	private ArrayList<TimeFrame> timeFrames;
        
        /**
         * Temps de début d'acquisition
         */
        private String startTime;
        
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
        private int width;
        
        /**
         * hauteur des images
         */
        private int height;
        
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
        private boolean isPartOfMultAcq;
	
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
	public PatientSerie(String dirPath) throws NotDirectoryException, DicomFilesNotFoundException, BadParametersException {

            if (dirPath == null) {
                    throw new BadParametersException("Le chemin rentré est invalide.");
            }

            this.nbTimeFrames = 0;
            this.nbImagesPerTimeFrame = 0;


            ArrayList<DicomImage> dicomImages = checkDicomImages(dirPath);

            //On récupère les paramètres du patient sur le premier fichier

            this.name = dicomImages.get(0).getAttribute(TagFromName.PatientName);

            this.id = dicomImages.get(0).getAttribute(TagFromName.PatientID);

            this.sex = dicomImages.get(0).getAttribute(TagFromName.PatientSex);

            //age sous la forme 045Y, on doit enlever le Y
            this.age = dicomImages.get(0).getAttribute(TagFromName.PatientAge).replace("Y", "");

            this.weight = dicomImages.get(0).getAttribute(TagFromName.PatientWeight);
            
            this.startTime = dicomImages.get(0).getAttribute(TagFromName.SeriesTime);

            this.nbTimeFrames = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.NumberOfTimeSlices));

            this.nbImagesPerTimeFrame = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.NumberOfSlices));
            
            this.width = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.Columns));
            
            this.height = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.Rows));

            this.timeFrames = new ArrayList<>(this.nbTimeFrames);
            checkTimeFrames(dicomImages);
            this.summALL = this.summSlices(0, this.nbTimeFrames-1);
            this.aortaResults = null;
            this.isPartOfMultAcq = false;
            
           /*
            //Séparation de la série en sous-série
            String outputDirDyn1 = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\Test Multi acquisitions\\TEPDyn1\\";
            String outputDirDyn2 = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\Test Multi acquisitions\\TEPDyn2\\";
            String outputDirStatic = "C:\\Users\\kamelya\\Documents\\QuantIF_Project\\Test Multi acquisitions\\TEPStatic\\";
            //On vide les répertoires
            DicomUtils.emptyDirectory(new File(outputDirDyn1));
            DicomUtils.emptyDirectory(new File(outputDirDyn2));
            DicomUtils.emptyDirectory(new File(outputDirStatic));
            try {
                createSubDynAcquisition(outputDirDyn1, 0, 7);
                createSubDynAcquisition(outputDirDyn2, 27, 33);
                createSubDynAcquisition(outputDirStatic, 20);
            } catch (DicomException | IOException ex) {
                Logger.getLogger(PatientSerie.class.getName()).log(Level.SEVERE, null, ex);
            }
            */
               
	}
        
        /**
         * Construit une série patient
         * @param dirPath chemin du repertoire où se trouve les fichiers DICOM
         * @param multiAcq vaut "true" si la série fait partie d'une multi acquisition, "false" sinon
         * @throws NotDirectoryException 
	 * 		Lev�e quand le chemin fourni ne correspond pas � un repertoire
	 * @throws DicomFilesNotFoundException
	 * 		Lev�e quand aucun fichier DICOM n'a �t� trouv� dans le r�pertoire
	 * @throws BadParametersException
	 * 		Lev�e quand les param�tres d'entr�e sont invalidesoject.patient.exceptions.BadParametersException
         */
        public PatientSerie(String dirPath, boolean multiAcq) throws NotDirectoryException, DicomFilesNotFoundException, BadParametersException {
            this(dirPath);
            this.isPartOfMultAcq = multiAcq;
        }
    
	
	/**
	 * Retourne la coupe temporelle ayant cet index.
	 * 
	 * @param index index de la coupe
         * @return la TimeFrame à cet index
	 * @throws BadParametersException
	 * 		Levee quand l'index demand� est soit inférieur ou égal à 0 soit supérieur au nombre de coupes
	 */
	public TimeFrame getTimeFrame(int index) throws BadParametersException {
            if (index < 0) 
                    throw new BadParametersException("L'indice doit être supérieur ou égal à 0");
            if (index > this.timeFrames.size())
                    throw new BadParametersException("Il n'y a pas de coupe temporelle à cet index. L'index est trop grand!");

            return this.timeFrames.get(index);
	}
	
       
	
        
	
	
	/**
	 * Parcourt le repertoire de fichiers et cree les instances de DicomImage
	 * �à l'aide des fichier DICOM et les range dans l'ordre croissant des index des images
         * 
	 * @param path Chemin du dossier DICOM
	 * @return Une ArrayList de DicomImage : la liste des fichiers 'DICOM' image liés au patient
	 * @throws NotDirectoryException 
	 * 		Levée quand le chemin fourni ne correspond pas � un repertoire
	 * @throws DicomFilesNotFoundException
	 * 		Levée quand aucun fichier DICOM n'a été� trouvé�dans le répertoire
	 */
	
	private ArrayList<DicomImage> checkDicomImages(String path) throws NotDirectoryException, DicomFilesNotFoundException {
		ArrayList<DicomImage> listDI = new ArrayList<>();
		File dir = new File(path);
		if (!dir.isDirectory()) {
			throw new NotDirectoryException("Le chemin '" + path + "' n'est pas un répertoire");
		}
		File[] files = dir.listFiles();
	        
		//On parcourt le dossier de fichiers
		if (files != null) {
                  
                    for (File file : files) {
                        
                        if (file.isFile()) {
                            // Si c'est un fichier on vérifie si c'est un fichier DICOM
                            
                            if (DicomUtils.isADicomFile(file)) {
                                try {
                                    
                                    DicomImage dcm = new DicomImage(file);
                                    listDI.add(dcm);
                                   
                                    
                                       
                                } catch (DicomException | IOException ex) {
                                    Logger.getLogger(PatientSerie.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                   
		}
               
		
		//On vérifie si la liste n'est pas vide
		if (listDI.isEmpty()) {
			throw new DicomFilesNotFoundException("Aucun fichier DICOM n'a été trouvé dans ce repertoire");
		}
		//On range  les dicomImages
		Collections.sort(listDI);
                
                System.out.println(listDI.size() + " images DICOM détectées!!");
		return listDI;
	}    
        
        /**
         * Parcourt la liste des dicomImages et on les range dans les time frames
         * @param dicomImages 
         */
        private void checkTimeFrames(ArrayList<DicomImage> dicomImages) {
            //On récupère la liste des acquisition time
            ArrayList<String> ATList = new ArrayList<>();
            String AT;
            for (DicomImage di : dicomImages) {
                AT = di.getAttribute(TagFromName.AcquisitionTime);
                if (!ATList.contains(AT))
                    ATList.add(AT);
            }
            
            //On crée tous les TimeFrame
            ATList.stream().forEach((at) -> {
                try {
                    this.timeFrames.add(new TimeFrame(this.nbImagesPerTimeFrame, at, this.width, this.height));
                } catch (BadParametersException ex) {
                    Logger.getLogger(PatientSerie.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            //On parcout les dicomImages et on les ajoutent à leur TimeFrame respectif
            dicomImages.stream().forEach((di) -> {
                try {
                    int timeFrameIndex = ATList.indexOf(di.getAttribute(TagFromName.AcquisitionTime));
                    this.timeFrames.get(timeFrameIndex).addDicomImage(di);
                } catch (BadParametersException | ImageSizeException | TimeFrameOverflowException ex) {
                    Logger.getLogger(PatientSerie.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
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
    
    public float[][] summSlices(int startSlice, int endSlice) {
        
        //Tableau contenant les images sommés
        float[][] summImagesArray = new float[this.nbImagesPerTimeFrame][width*height];
        //Image avec les bonnes dimensions : à re remplir
        
        BufferedImage resultSumm;
        
        float max = 0;
        
        
        //On parcourt toutes les images de la coupe temporelle d'index startSlice
        for (int imageIndex = 0; imageIndex < this.nbImagesPerTimeFrame; imageIndex++) {
            //Pour chacune de ces images on la somme avec ceux des coupes allant de startSlice à endSlice
            
            
            resultSumm = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
            
            //tableau contenant les valeurs des pixels de l'image resultSumm en SHORT
            
            
            //On cree un tableau de mm taille mais en float, car la somme des pixels risque de dépasser
            //la taille max du format SHORT
            float[] pixels = new float[width*height];
            
            for (int sliceIndex = startSlice ; sliceIndex <= endSlice; sliceIndex++) {
                try {
                    DicomImage dcmToSumm = this.timeFrames.get(sliceIndex).getDicomImage(imageIndex);
                    if (dcmToSumm != null) {
                        
                        //Comme on ne connait pas le type de l'image à sommer, à la dessine dans un
                        //BufferedImage de type USHORT_GRAY
                        BufferedImage buffToSumm = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
                        Graphics2D gBuffToSumm = buffToSumm.createGraphics();
                        gBuffToSumm.drawImage(dcmToSumm.getBufferedImage(), null, 0, 0);
                        
                        //Tableau contenant les valeurs de pixels de l'image a sommer
                        short[] buffPix = ((DataBufferUShort) buffToSumm.getRaster().getDataBuffer()).getData();
                        
                      
                        for (int row = 0; row<width; ++row) {
                            for (int col = 0; col < height; ++col ) {
                                int pix = buffPix[row * width + col];
                                if (buffPix[row * width + col] < 0) {
                                    //On le convertie en valeur INT UNSIGNED
                                    pix = 65536 + pix;
                                }
                                pixels[row * width + col] += pix;
                                if (max < pixels[row * width + col])
                                    max = pixels[row * width + col];
                                
                            }
                            gBuffToSumm.dispose();
                        }
                    }
                }
                catch (BadParametersException ex) {
                    Logger.getLogger(PatientSerie.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
           
            
            
            summImagesArray[imageIndex] = pixels;
        }   
        System.out.println("Max Value : " + max);
        System.out.println("Somme de " + (startSlice + 1)  + " à " + (endSlice + 1) + " faite!!!");
        
        //Affichage de la somme 
        /*
        try {
            DicomUtils.showImages(summImagesArray);
        } catch (BadParametersException ex) {
            Logger.getLogger(PatientSerie.class.getName()).log(Level.SEVERE, null, ex);
        }
                */
        return summImagesArray;
        
    }
    
   
    public int getNbTimeSlices() {
        return this.nbTimeFrames;
    }
    
    public int getFramesPerTimeSlice() {
        return this.nbImagesPerTimeFrame;
    }
    
    @Override
    public String toString() {
            String str = "Présentation du patient " + this.id + "\n";
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

    public String getPixelUnity() {
        String unit = null;
            try {
                unit = this.timeFrames.get(0).getDicomImage(0).getAttribute(TagFromName.Units);
            } catch (BadParametersException ex) {
                Logger.getLogger(PatientSerie.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public void selectAorta() throws BadParametersException {
        //this.patient.selectAorta();
        IJ.run("Close All");
        //Stack d'images à afficher
        ImageStack imgStack = new ImageStack(this.width, this.height, null);

        FloatProcessor imgProc;
        ImagePlus summAll;

        
        //On parcout la liste des images pour les chargés dans le stack
        for (float[] pixels : this.summALL) {


            imgProc = new FloatProcessor(width, height, pixels);
            imgStack.addSlice(imgProc);
        }

        summAll = new ImagePlus("", imgStack);


        //On cree les stacks des images non sommés
        ImagePlus[] framesStack = new ImagePlus[this.nbImagesPerTimeFrame];
        //à la position i, on aura un stack composé de tous les images d'indice i de chaque coupe temporelle
        for (int frameIndex = 0; frameIndex < this.nbImagesPerTimeFrame; frameIndex++) {
            //On doit recupérer toutes les images  à la frameIndex dans tous les coupes temporelles
            ImageStack stack = new ImageStack(width, height, null);
            ImageProcessor proc;


            //on parcourt toutes les coupes temporelles
            for (int slice = 0; slice < this.nbTimeFrames; slice++) {
                DicomImage dcm  = this.timeFrames.get(slice).getDicomImage(frameIndex);
                BufferedImage buffe =  new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
                if (dcm != null) {
                    Graphics2D g = buffe.createGraphics();
                    g.drawImage(dcm.getBufferedImage(), 0, 0, null);

                   g.dispose();
                }

                ShortProcessor sp = new ShortProcessor(buffe);
                ImagePlus tmp = new ImagePlus("", buffe );
                proc = tmp.getProcessor();
                //System.out.println("Max pixel value images non sommées : " + sp.getMax());
                stack.addSlice(sp);

            }
            ImagePlus anotherImp = new ImagePlus("Frame " + Integer.toString(frameIndex+1), stack);

            framesStack[frameIndex] = anotherImp;

        }
        System.out.println("Frame Stack size : " + framesStack.length + " x " + framesStack[0].getImageStackSize());
        
        //On construit l'axe des abscisses
        double[] times = new double[this.nbTimeFrames]; 
        this.timeFrames.stream().forEach((tf) -> {
            times[this.timeFrames.indexOf(tf)] = DicomUtils.getMinutesBetweenDicomDates(this.startTime, tf.getAcquisitionTime());
           
        });
        System.out.println(Arrays.toString(times));
        roiSelection(summAll, framesStack, times);

       

            
    }
    
    /**
     * Affiche l'interface pour la selection d'une ROI
     * @param imgPlus stack composé de la somme de toutes les images
     * @param framesStack tableau de stack, à la position i on a un stack est composé des images d'index i de chaque coupe temporelle
     */
    private void roiSelection(ImagePlus imgPlus, ImagePlus[] framesStack, double[] timeArray) {
        //On affiche la série d'images 
        imgPlus.show();
        
        //On recupère la fenêtre d'affichage
        Window imgPlusWindow = WindowManager.getWindow(imgPlus.getTitle());
        
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
        
        RoiManager roiManager = new RoiManager();
        //roiManager.runCommand("reset");
        JButton b = new JButton();
        b.setSize(100, 100);
        b.setVisible(true);
        
        
        
        
        
        //On défini l'action du bouton
        b.addActionListener( new java.awt.event.ActionListener() {
            private ImagePlus[] framesStack;
            private double[] timeArray;
            private Roi roi;
            
            @Override
            
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               
               //Lorsqu'on appui sur le boutton
                if (roiManager.getCount() > 0) { try {
                    //On vérifie qu'une ROI a été ajoutée
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
                    
                    
                    subRoiManager.addRoi(roi);
                    subRoiManager.select(0);
                    //on fait de la muti- mesure sur la roi
                    ResultsTable multiMeasure = subRoiManager.multiMeasure(stackFrame);
                   
                   
                  
                    
                    createResults(roi, multiMeasure,  this.timeArray);
                    //if (!isPartOfMultiAcq())
                        plotResults();
                    
                    
                    } catch (BadParametersException ex) {
                        Logger.getLogger(PatientSerie.class.getName()).log(Level.SEVERE, null, ex);
                    }
                            
                }
            }
            
            private ActionListener init(ImagePlus[] fStack, double[] timeArray) {
                this.framesStack = fStack;
               
                this.timeArray = timeArray;
                
                System.out.println("Init button done");
                return this;
            }
        }.init(framesStack, timeArray));
        b.setText("Display the results");
        
        Window roiManagerWindow = WindowManager.getWindow("ROI Manager");                
        roiManagerWindow.add(b, BorderLayout.SOUTH);           
    }
    
    /**
     * Renvoie true si ce patient fait partie d'une multi acquisition
     * 
     */
    private boolean isPartOfMultiAcq() {
        return this.isPartOfMultAcq;
    }
    private void plotResults() {
        
        this.aortaResults.display(getPixelUnity());
    }
    
    /**
     * Construit les résultats de l'aorte
     * @param roi roi dessiné sur summALL
     * @param resultTable résultats liés à cette ROI
     * @param timeAxis l'axe des temps
     */
    private void createResults(Roi roi, ResultsTable resultTable, double[] timeAxis ) throws BadParametersException {
        //on ajoute la liste des acquisition times a resultTable
        int resultTableSize = resultTable.getColumnAsDoubles(0).length;
        System.out.println("Size of ResultsTable : " + resultTableSize );
        if (resultTableSize != timeAxis.length)
            throw new BadParametersException("ResultsTable et le tableau des acquisition time n'ont pas la même taille! ");
        int row = 0;
        for (int i = 0; i < resultTableSize; i++) {
            resultTable.setValue("Time", row, timeAxis[i]);
            row++;
        }
        
        this.aortaResults = new AortaResults(this.name, roi, resultTable);
    }
        
}
