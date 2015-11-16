/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuanttIF of the "Centre Henri Becquerel"
 *   * 
 */
package QuantIF_Project.serie;

import QuantIF_Project.patient.DicomImage;
import QuantIF_Project.patient.PatientMultiSeries;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.DicomFilesNotFoundException;
import QuantIF_Project.patient.exceptions.NotDirectoryException;
import QuantIF_Project.utils.DicomUtils;
import com.pixelmed.dicom.TagFromName;
import ij.gui.Roi;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cyriac
 */

public class TAPSerie implements Serie{
    /**
     * Nombre de block contenus dans la TAP
     */
    private int nbBodyBlocks;
    
   /**
    * Nombre d'images contenus dans chaque blocks
    */ 
    private int nbImagesPerBodyBlock;
    
    /**
     * Liste des fichiers dicom
     */
    private final ArrayList<DicomImage> dicomImages;
    
    /**
     * Listes des parties du corps
     */
    private ArrayList<BodyBlock> bodyBlocks;
    private String name;
    private final String id;
    private String sex;
    private String age;
    private String weight;
    private final int width;
    private final int height;
    
    
    /**
     * Crée une instance de TAPSerie
     * @param dirPath Chemin du repertoire contenant les images de la TAP
     * @throws NotDirectoryException
     * @throws DicomFilesNotFoundException
     * @throws BadParametersException /**
 Crée une instance de TAPSerie
     */
    public TAPSerie(String dirPath) throws NotDirectoryException, DicomFilesNotFoundException, BadParametersException {
        
        this.dicomImages = DicomUtils.checkDicomImages(dirPath);
        this.name = dicomImages.get(0).getAttribute(TagFromName.PatientName);

        this.id = dicomImages.get(0).getAttribute(TagFromName.PatientID);

        this.sex = dicomImages.get(0).getAttribute(TagFromName.PatientSex);

        //age sous la forme 045Y, on doit enlever le Y
        this.age = dicomImages.get(0).getAttribute(TagFromName.PatientAge).replace("Y", "");

        this.weight = dicomImages.get(0).getAttribute(TagFromName.PatientWeight);


        

        

        this.width = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.Columns));

        this.height = Integer.parseInt(dicomImages.get(0).getAttribute(TagFromName.Rows));
        this.bodyBlocks = new ArrayList<>();
        checkBodyBlocks();
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
        
        System.out.println(this.bodyBlocks.size() + " parties du corps détectées!!");
        this.nbBodyBlocks = this.bodyBlocks.size();
        
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
        return this.nbBodyBlocks;
    }

    @Override
    public String getPixelUnity() {
        String unit = null;
        DicomImage di = this.bodyBlocks.get(0).getDicomImage(0);
        unit = di.getAttribute(TagFromName.Units);
        return unit;
    }

    @Override
    public Block getBlock(int index) throws BadParametersException {
       if (index < 0) 
                    throw new BadParametersException("L'indice doit être supérieur ou égal à 0");
            if (index > this.bodyBlocks.size())
                    throw new BadParametersException("Il n'y a pas de coupe corporelle à cet indice. L'index est trop grand!");

            return this.bodyBlocks.get(index);
    }

    @Override
    public void selectAorta(Roi roi, int startIndex, int endIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setParent(PatientMultiSeries pms) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float[][] summSlices(int startSlice, int endSlice) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}
