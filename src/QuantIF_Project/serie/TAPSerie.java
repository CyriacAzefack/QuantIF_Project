/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.AortaResults;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.NoTAPSerieFoundException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import QuantIF_Project.utils.DicomUtils;
import com.pixelmed.dicom.TagFromName;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.process.FloatProcessor;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cyriac
 */

public class TAPSerie implements Serie{
    
   
    
   
    
    /**
     * Liste des fichiers dicom
     */
    private final ArrayList<DicomImage> dicomImages;
    
    /**
     * Patient paren
     */
    PatientMultiSeries parent;
    
    
    /**
     * Partie du corps choisie pour l'étude
     * 
     */
    private BodyBlock choosenBodyBlock;
    /**
     * Listes des parties du corps
     */
    private ArrayList<BodyBlock> bodyBlocks;
    
    /**
     * Résultats de l'aorte
     */
    private AortaResults aortaResults;
    
    private String name;
    private final String id;
    private String sex;
    private String age;
    private String weight;
    private final int width;
    private final int height;
    private String directoryPath;
    
    /**
     * La série fait partie d'une acquisition multiple
     */
    private boolean isPartOfMultAcq;
    
    /**
     * Série TEP précédant cette série TAP dans la multi acquisition
     */
    private TEPSerie startTEPSerie;
    
    /**
     * 
     */
    
    /**
     * Crée une instance de TAPSerie
     * @param dirPath Chemin du repertoire contenant les images de la TAP
     * @throws NotDirectoryException
     * @throws DicomFilesNotFoundException
     * @throws BadParametersException 
     * @throws NoTAPSerieFoundException
     */
    public TAPSerie(String dirPath) throws NotDirectoryException, DicomFilesNotFoundException, BadParametersException, NoTAPSerieFoundException {
        
         if (dirPath == null) 
            throw new BadParametersException("Le chemin rentré est invalide.");
        
        this.directoryPath = dirPath;
        this.dicomImages = DicomUtils.checkDicomImages(dirPath);
        this.name = dicomImages.get(0).getAttribute(TagFromName.PatientName);

        this.id = dicomImages.get(0).getAttribute(TagFromName.PatientID);
        
        this.sex = dicomImages.get(0).getAttribute(TagFromName.PatientSex);

        //age sous la forme 045Y, on doit enlever le Y
        this.age = dicomImages.get(0).getAttribute(TagFromName.PatientAge).replace("Y", "");

        this.weight = dicomImages.get(0).getAttribute(TagFromName.PatientWeight);
        
        this.choosenBodyBlock = null;
        
        this.aortaResults = null;
        
        this.isPartOfMultAcq = false;
        
        int nbTimeSlice =Integer.valueOf(dicomImages.get(0).getAttribute(TagFromName.NumberOfTimeSlices));
        if (nbTimeSlice > 0) 
            throw new NoTAPSerieFoundException("La série ouverte n'est pas une série TAP corps entier");
        

        

        this.width = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.Columns));

        this.height = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.Rows));
        this.bodyBlocks = new ArrayList<>();
        checkBodyBlocks();
    }
    
    /**
     * Cree une série TAP appartenant à une multi-acquisition
     * @param dirPath chemin du dossier contenant les images
     * @param multiAcq appartient à une multiAcquisition
     * @param tepSerie Série TEP précédant cette série dans la multi acquisition
     * @throws QuantIF_Project.patient.exceptions.NotDirectoryException
     * @throws QuantIF_Project.patient.exceptions.BadParametersException
     * @throws QuantIF_Project.patient.exceptions.NoTAPSerieFoundException
     * @throws QuantIF_Project.patient.exceptions.DicomFilesNotFoundException
     */
    public TAPSerie (String dirPath, boolean multiAcq, TEPSerie tepSerie) throws NotDirectoryException, BadParametersException, NoTAPSerieFoundException, DicomFilesNotFoundException {
        this(dirPath);
        this.isPartOfMultAcq = multiAcq;
        this.startTEPSerie = tepSerie;
    }
    
    /**
     * On parcout la liste des dicomImages et on repere le nombre de blocks qu'on a, et on range les images par block.
     * Chaque block a un AcquisitionNumber différent.
     */
    private void checkBodyBlocks() {
        
        //On récupére les différents Acquisition Number
        ArrayList<Integer> AcqNumberList = new ArrayList<>();
        int AcqNumber;
        for (DicomImage di : this.dicomImages) {
            AcqNumber = 0 - Integer.valueOf(di.getAttribute(TagFromName.AcquisitionNumber));
            //AcqNumber = di.getAttribute(TagFromName.NumberOfSlices);
            if (!AcqNumberList.contains(AcqNumber))
                AcqNumberList.add(AcqNumber);
        }
        
        AcqNumberList.sort(null);
        //On crée les différents timeFrame
        AcqNumberList.stream().forEach((an) -> {
            try {
                //On suppose que tous les blocks ont la même taille
                this.bodyBlocks.add(new BodyBlock(an, width, height));
            } catch (BadParametersException ex) {
                Logger.getLogger(TAPSerie.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
       
        //On parcout les dicomImages et on les ajoutent à leur TimeFrame respectif
        this.dicomImages.stream().forEach((di) -> {
            int bodyBlockIndex = AcqNumberList.indexOf(0 - Integer.valueOf(di.getAttribute(TagFromName.AcquisitionNumber)));
            //int bodyBlockIndex = AcqNumberList.indexOf(di.getAttribute(TagFromName.NumberOfSlices));
            this.bodyBlocks.get(bodyBlockIndex).addDicomImage(di);
        });
        
        //On met à jour les valeurs de temps des différentes coupes corporelles
        for (BodyBlock bb : this.bodyBlocks) {
            bb.setTime();
        }
        System.out.println(this.bodyBlocks.size() + " parties du corps détectées!!");
        
        
    }
    
    /**
     * On selectionne la partie du corps qu'on utilise pour l'étude
     * @param imageIndex index de l'image appartenant au block qu'on étudie
     */
    public void setChoosenBodyBlock(int imageIndex) {
        
        DicomImage dcm = dicomImages.get(imageIndex);
        
        //On a parcourt les coupes pour trouver celle qui correspond à l'image
        // sélectionnée
        for (BodyBlock bb : bodyBlocks) {
            if (bb.contains(dcm))
                this.choosenBodyBlock = bb;
        }
        //On vide la liste et on garde uniquement celle sélectionnée
        
        this.bodyBlocks.clear();
        this.bodyBlocks.add(this.choosenBodyBlock);
        
    }
        
    
    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getNbImages(int blockIndex) {
        return this.bodyBlocks.get(blockIndex).size();
    }
    
    @Override
    public int getNbBlocks() {
        return this.bodyBlocks.size();
    }

    @Override
    public String getPixelUnity() {
        String unit = null;
        DicomImage di = this.bodyBlocks.get(0).getDicomImage(0);
        unit = di.getAttribute(TagFromName.Units);
        return unit;
    }

    @Override
    public BodyBlock getBlock(int index) throws BadParametersException {
       if (index < 0) 
                    throw new BadParametersException("L'indice doit être supérieur ou égal à 0");
            if (index > this.bodyBlocks.size())
                    throw new BadParametersException("Il n'y a pas de coupe corporelle à cet indice. L'index est trop grand!");

            return this.bodyBlocks.get(index);
    }
    
    /**
     * On fait les calculs sur la ROI
     * @param roi roi selectionnée
     * @param startIndex inutile pour une série TAP
     * @param endIndex inutile pour une série TAP
     */
    @Override
    public void selectAorta(Roi roi, int startIndex, int endIndex) {
        ImageStack imgStack = new ImageStack(this.width, this.height, null);

        //On fait la somme des Coupes allant de "startIndex" à "endIndex"
        FloatProcessor imgProc;
        ImagePlus summAll;
        float[][] summSlices;
        summSlices = this.summSlices(startIndex, endIndex);
        
        

        
        //On parcout la liste des images pour les chargés dans le stack
        for (float[] pixels : summSlices) {


            imgProc = new FloatProcessor(width, height, pixels);
            imgStack.addSlice(imgProc);
        }
        
        //Ensemble des images résultantes de la somme
        summAll = new ImagePlus("", imgStack);


        //On cree les stacks des images non sommés
        ImagePlus[] framesStack = new ImagePlus[this.choosenBodyBlock.size()];
        //à la position i, on aura un stack composé de tous les images d'indice i de chaque coupe temporelle
        for (int imageIndex = 0; imageIndex < this.choosenBodyBlock.size(); imageIndex++) {
            //On doit recupérer toutes les images  à la frameIndex dans tous les coupes temporelles
            ImageStack stack = new ImageStack(width, height);
           
            
            DicomImage dcm  = this.choosenBodyBlock.getDicomImage(imageIndex);
            if (dcm != null) {
                stack.addSlice(dcm.getImageProcessor());
            }
            else {
                FloatProcessor sp = new FloatProcessor(width, height);
                stack.addSlice(sp);
                
            }
            
            framesStack[imageIndex] = new ImagePlus("", stack);
        }
        
        getRoiResults(roi, summAll, framesStack);
        

    }

    @Override
    public void setParent(PatientMultiSeries pms) {
        this.parent = pms;
    }
    
    /**
     * Dans une série TAP on a qu'un seul Block.
     * 
     * @param startSlice inutile pour une série TAP
     * @param endSlice inutile pour une série TAP
     * @return 
     */
    @Override
    public float[][] summSlices(int startSlice, int endSlice) {
        //Tableau contenant les images sommés
        
        int nbImagesInBodyBlock = this.choosenBodyBlock.size();
        float[][] summImagesArray = new float[nbImagesInBodyBlock][width*height];
        
        
        //On parcourt toutes les images de la coupe temporelle d'index startSlice
        for (int imageIndex = 0; imageIndex < nbImagesInBodyBlock; imageIndex++) {
            //Pour chacune de ces images on la somme avec ceux des coupes allant de startSlice à endSlice
            
            
            //On cree un tableau de mm taille mais en float, car la somme des pixels risque de dépasser
            //la taille max du format SHORT
            float[] pixels = new float[width*height];
            
            
            DicomImage dcmToSumm = this.choosenBodyBlock.getDicomImage(imageIndex);
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
    public String toString() {
         String str = "Présentation de la Serie TAP " + this.id + "\n";
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
    public AortaResults getAortaResults() {
        return this.aortaResults;
    }

    @Override
    public Date getSerieStartDate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void getRoiResults(Roi roi, ImagePlus summAll, ImagePlus[] framesStack) {
        //On récupère la série d'images statique affichée
        ImagePlus impROI = WindowManager.getImage("Série Statique TAP -- " + 1 + "x" + this.choosenBodyBlock.size());
        
        //Si elle n'est pas affichée, on l'affiche
        if (impROI == null) {
            summAll.setTitle("Série Statique TAP -- " + 1 + "x" + this.choosenBodyBlock.size());
            
            impROI = summAll;
            System.out.println("***RESET AFFICHAGE "+ 1 + "x" + this.choosenBodyBlock.size()+"****");
            
            impROI.show();
        }
        
        Roi selectedRoi = roi;
        
       //On recupére la ROI dessinée sur la série si il y en a une
        if (impROI.getRoi() != null) {
            selectedRoi = impROI.getRoi();
            //selectedRoi.setPosition(roi.getPosition());
            
            
        }
        
        
        
        //summAll.setRoi(selectedRoi, true);
        
        
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
       
        
        //On ajoute les résultats (comme on est en statique, on a qu'une seule ligne dans le tableau de résultats)
        resultTable.setValue("Start Time(sec)", 0, this.choosenBodyBlock.getStartTime());
        resultTable.setValue("Mid time (sec)", 0, this.choosenBodyBlock.getMidTime());
        resultTable.setValue("End Time(sec)", 0, this.choosenBodyBlock.getEndTime());
          
        //resultTable.show("TAP RESULTS");
        
        
        this.aortaResults = new AortaResults(this.name, roi, resultTable);
        
        
        
        
        //On dessine sur l'image la ROI utilisée pour le calul
        ImagePlus impROI = WindowManager.getImage(imageTitle);
        impROI.setRoi(roi);
        
    }
    
    public boolean isPartOfMultAcq () {
        return this.isPartOfMultAcq;
    }
    
    public BufferedImage[] getStartTEPSerieSummAll() {
        return this.startTEPSerie.getBuffSummALL();
    }
    
    public BufferedImage[] getAllImages() {
        BufferedImage[] buffs = new BufferedImage[dicomImages.size()];
        
        for (int i = 0; i < buffs.length; i++) {
            buffs[i] = dicomImages.get(i).getBufferedImage();  
        }
        
        return buffs;
    }

    @Override
    public String getSeriePath() {
        return this.directoryPath;
    }

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
    
}
