/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.gui.Main_Window;
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
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 * 
 * @author Cyriac
 *
 */
public class TEPSerie implements Serie{
	/**
	 * Nom anonymisé du patient
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
         * Somme de toutes les frames de cette série
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
         * Chemin du dossier où sont les images
         */
        private String directoryPath;
	
        /**
	 * On cree une instance de patient  l'aide du chemin vers le dossier où
	 * sont contenus tous les fichiers DICOM concernant le patient
	 * @param dirPath chemin du repertoire où se trouve les fichiers DICOM
	 * @throws NotDirectoryException 
	 * 		Quand le chemin fourni ne correspond pas à un repertoire
	 * @throws DicomFilesNotFoundException
	 * 		Quand aucun fichier DICOM n'a été trouv� dans le r�pertoire
	 * @throws BadParametersException
	 * 		Quand les paramètres d'entrée sont invalides
	 */
	public TEPSerie(String dirPath) throws NotDirectoryException, DicomFilesNotFoundException, BadParametersException {

            if (dirPath == null) {
                    throw new BadParametersException("Le chemin rentré est invalide.");
            }

            this.nbTimeFrames = 0;
            this.nbImagesPerTimeFrame = 0;

            this.directoryPath = dirPath;
            
            //On récupère tous les fichiers DICOM dans le repertoire
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
            
            this.width = dicomImages.get(0).getWidth();
            
            this.height = dicomImages.get(0).getHeight();
            
            this.serieStartDate = dicomImages.get(0).getAttribute(TagFromName.StudyDate) + " " + dicomImages.get(0).getAttribute(TagFromName.StudyTime);

            this.timeFrames = new ArrayList<>(this.nbTimeFrames);
            
            //On range les images DICOM dans les frame
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
                createSubDynAcquisition(outputDirDyn1, 0, 7);
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
                    throw new BadParametersException("["+ index + "] L'indice doit être supérieur ou égal à 0");
            if (index > this.timeFrames.size())
                    throw new BadParametersException("[" + index + "] Il n'y a pas de coupe temporelle à cet index. L'index est trop grand!");

            return this.timeFrames.get(index);
	}
	
       
	
        
	
	
	
        
        /**
         * Parcourt la liste des dicomImages et on les range dans les time frames
         * 
         */
        private void checkTimeFrames() {
            //On récupère la liste des acquisition time (si 7 acqTime différetn, tableau de 7 cases)
            ArrayList<String> acqTimeList = new ArrayList<>();
            String acqTime;
            for (DicomImage di : dicomImages) {
                acqTime = di.getAttribute(TagFromName.AcquisitionTime);
                if (!acqTimeList.contains(acqTime))
                    acqTimeList.add(acqTime);
            }
            
            acqTimeList.sort(null); //Tri acqTime dans l'ordre chonologique
            
            //On crée tous les TimeFrame vides
            acqTimeList.stream().forEach((acqTim) -> {
                try {
                    this.timeFrames.add(new TimeFrame(this.nbImagesPerTimeFrame, acqTim, this.width, this.height));
                } catch (BadParametersException ex) {
                    Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            //On range les dicomImages dans les timeFrame
            for (DicomImage di : this.dicomImages) {
                try {
                    acqTime = di.getAttribute(TagFromName.AcquisitionTime);
                    int index = acqTimeList.indexOf(acqTime);
                    this.timeFrames.get(index).addDicomImage(di);
                } catch (BadParametersException | ImageSizeException | TimeFrameOverflowException ex) {
                    Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
            //On met à jour les valeurs de temps des différentes frames
            this.timeFrames.stream().forEach((tf) -> {
                tf.setTime();
            });
            
            System.out.println(this.timeFrames.size() + " Coupes temporelles détectées!!");
            this.nbTimeFrames = this.timeFrames.size();
            
        }
    
    
    
        @Override
    public int getHeight(){
        return height;
    }
    
        @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public String getSeriePath() {
        return this.directoryPath;
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
    private void createSubStaticAcquisition(String outputDir, int frameIndex) throws BadParametersException, IOException, DicomException {
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
     * Calcule la moyenne dans la ROI pour toutes les frames
     * @param roi Zone sélectionnée
     * @param startFrameIndex indice de la frame de début pour la somme 
     * @param endFrameIndex indice de la frame de fin pour la somme 
     */
        @Override
    public void selectAorta(Roi roi, int startFrameIndex, int endFrameIndex)  {
        
        //On récupère la position de l'image sur laquelle a été tracée la ROI
        int imageIndex = roi.getPosition();
        
        //Tableau contenant tous les images étant à l'index "imageIndex" pour toutes
        //  les frames allant de "startFrameIndex" à "endFrameIndex"
        ArrayList<ImageProcessor> imagesAtImageIndex = new ArrayList<>();
        
        if (endFrameIndex == 0) { // On prend tous
            endFrameIndex = this.nbTimeFrames - 1;
        }
        
        //On rempli le tableau en parcourant les frames
        
        for (int frameIndex = startFrameIndex; frameIndex <= endFrameIndex; frameIndex++) {
            try {
                DicomImage di = this.timeFrames.get(frameIndex).getDicomImage(imageIndex);
                imagesAtImageIndex.add(di.getImageProcessor());
            } catch (BadParametersException ex) {
                Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        RoiManager roiManager = new RoiManager(true); //S'occupe de la gestion des ROI et des images (Outil ImageJ)
        
        roiManager.addRoi(roi);
        
        System.out.println("Selected ROI position : " + roi.getPosition());
        
       
        ImagePlus imagesList; //Liste des images sur lesquelles on vas faire le calcul. Type de données acceptées par le roiManager //(Outil ImageJ)
        
        ImageStack stackImages = new ImageStack(width, height);
        imagesAtImageIndex.stream().forEach((ip) -> {
            stackImages.addSlice(ip);
        });
        
        imagesList = new ImagePlus("", stackImages);
            
        
        
        
        
        roiManager.select(0); //On selectionne la roi qu'on vient d'ajouter
        
        //on fait de la muti- mesure sur la roi
        ResultsTable multiMeasure = roiManager.multiMeasure(imagesList);
        
        //On crée les resultats
        createResults(roi, multiMeasure, startFrameIndex, endFrameIndex);
        
        
        //Si on est le premier dans une multi-acquisition
        if (this.isPartOfMultAcq && this.isFirstInMultiAcq)
            //On averti le parent de lancer la sélection d'aorte pour les autres séries
            this.parent.roiSelected(roi);
            
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
     * 
     */
    private void createResults(Roi roi, ResultsTable resultTable, int startFrameIndex, int endFrameIndex) {
        //on ajoute la liste des acquisition times a resultTable
        int resultTableSize = resultTable.getColumnAsDoubles(0).length;
        System.out.println("Size of ResultsTable : " + resultTableSize );
       
        //On parcourt les lignes du tableaux pour ajouter les valeur de temps
        for (int row = 0; row < resultTableSize; row++) {
            resultTable.setValue("Start Time(sec)", row, this.timeFrames.get(row+startFrameIndex).getStartTime());
            resultTable.setValue("Mid time (sec)", row, this.timeFrames.get(row+startFrameIndex).getMidTime());
            resultTable.setValue("End Time(sec)", row, this.timeFrames.get(row+startFrameIndex).getEndTime());
            
            
        }
        
        this.aortaResults = new AortaResults(this.name, roi, resultTable);
        
        //On affiche les résultats si la série est unique (ne fait pas partie d'une acquisition multiple)
        if (!isPartOfMultAcq)
            this.aortaResults.display("PROPCPS");
        
        
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
    
        @Override
    public Date getSerieStartDate() {
        return DicomUtils.dicomDateToDate(this.serieStartDate);
    }
    
    public BufferedImage[] getBuffSummALL() {
        BufferedImage[] buffs = new BufferedImage[this.summALL.length];
        FloatProcessor fp;
        for (int i = 0; i < buffs.length; i++) {
            float[] pixels = summALL[i];
            fp = new FloatProcessor(width, height, pixels);
            buffs[i] = fp.getBufferedImage();
        }
        
        return buffs;
    }
    
    public FloatProcessor[] getSummALL() {
        FloatProcessor[] fps = new FloatProcessor[this.summALL.length];
        FloatProcessor fp;
        for (int i = 0; i < fps.length; i++) {
            float[] pixels = summALL[i];
            fp = new FloatProcessor(width, height, pixels);
            fps[i] = fp;
        }
        
        return fps;
    }
    
    /**
     * Retourne la dose de FDG injectée dans le patient en <b>MBq</b>
     * 
     */
    public double getPatientInjectedDose() {
        double dose = 0;
        try {
            DicomImage dcm = this.timeFrames.get(0).getDicomImage(2);
            dose = Double.parseDouble(dcm.getAttribute(TagFromName.RadionuclideTotalDose)); //En Bq
        } catch (BadParametersException ex) {
            Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
        }
        dose /= 1E6;
        return dose;
                
    }
    
    /**
     * Retourne le poids du patient en <b>Kg</b>
     *  
     */
        @Override
    public int getPatientWeight() {
        return Integer.parseInt(weight); 
    }
    
    /**
     * Retourne la taille du patient en <b>cm</b> 
     */
        @Override
    public int getPatientHeight() {
        
        DicomImage dcm = this.dicomImages.get(0);
        return Integer.parseInt(dcm.getAttribute(TagFromName.PatientSize))/100;
        
    }
    
    
    
    
    /**
     * Retourne <b>true</b> si c'est un homme et <b>false</b> sinon
     * 
     */
    public boolean isAMale() {
        return "M".equals(sex.trim());
    }
    
    /**
     * <p>Retourne le temps de début de l'injection </p>
     * Valeur du tag SeriesTime (<b>hhmmss.frac</b>)
     * @return 
     */
    public String getSeriesTime() {
        DicomImage dcm = this.dicomImages.get(0);
        return dcm.getAttribute(TagFromName.SeriesTime);
        
         
    }
    
    /**
     * 
     * @return Le nombre d'images dans la première coupe temporelle
     */
    public int getNbImages() {
        try {
            return getNbImages(0);
        } catch (BadParametersException ex) {
            Logger.getLogger(TEPSerie.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

   

   
   
    
    
        
}
