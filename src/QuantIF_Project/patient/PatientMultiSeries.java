/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuantIF_Project.patient;

import QuantIF_Project.serie.TimeFrame;
import QuantIF_Project.serie.TEPSerie;
import QuantIF_Project.patient.exceptions.BadParametersException;
import QuantIF_Project.patient.exceptions.PatientStudyException;
import QuantIF_Project.patient.exceptions.SeriesOrderException;
import com.pixelmed.dicom.TagFromName;
import ij.gui.Roi;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *Contient la série dynamique de départ, la série statique au milieu et la série dynamique de fin
 * @author Cyriac
 */
public class PatientMultiSeries {
    
    /**
     * Série dynamique de début d'acquisition
     */
    private final TEPSerie startDynPatient;
    
    /**
     * Série statique du milieu d'acquisition
     */
    private final TEPSerie staticPatient;
    
    /**
     * Série dynamique de fin d'acquisition
     */
    private final TEPSerie endDynPatient;
    
    /**
     * Résultats de sélection de l'aorte
     */
    private AortaResults aortaResults;
    
    private int startSummIndex, endSummIndex;
    
    /**
     * Champs permettant de déterminer si on affaire au même patient
     */
    private String[] tagsToCheck;
    
    /**
     * Construit une série patient à l'aide de plusieurs acquisitions
     * @param startDynSerie Série dynamique de départ
     * @param staticSerie Série statique
     * @param endDynSerie Série dynamique de fin
     * @throws PatientStudyException
     *      Si tous les images n'appartiennent pas au même patient
     */

    /**
     * Construit une série patient à l'aide de plusieurs acquisitions
     * @param startDynSerie Série dynamique de départ
     * @param staticSerie Série statique
     * @param endDynSerie Série dynamique de fin
     * @throws PatientStudyException Si tous les images n'appartiennent pas au même patient
     * @throws QuantIF_Project.patient.exceptions.SeriesOrderException
     */
    public PatientMultiSeries(TEPSerie startDynSerie, TEPSerie staticSerie, TEPSerie endDynSerie) throws PatientStudyException, SeriesOrderException {
        
        
        this.startDynPatient = startDynSerie;
        
        
        
        this.staticPatient = staticSerie;
        
        
        this.endDynPatient = endDynSerie;
        
        setParents();
        
        this.tagsToCheck = new String [3];
       
            
        //On récupère ce format sur la premiere image de la sous-acquisition de départ
        
        try {
            DicomImage di = this.startDynPatient.getBlock(0).getDicomImage(0);
            this.tagsToCheck[0] = di.getAttribute(TagFromName.PatientName);
            this.tagsToCheck[1] =  di.getAttribute(TagFromName.PatientID);
            this.tagsToCheck[2] = di.getAttribute(TagFromName.StudyInstanceUID);
                  
            this.checkMatchPatientSerieStudy(this.startDynPatient);
            this.checkMatchPatientSerieStudy(this.staticPatient);
            this.checkMatchPatientSerieStudy(this.endDynPatient);
        } catch (BadParametersException ex) {
            Logger.getLogger(PatientMultiSeries.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
        
        this.aortaResults = new AortaResults(startDynPatient.getName());
        
        CheckSerieOrder();
        
    }
    
    public TEPSerie getStartDynSerie() {
        return this.startDynPatient;
    }

    public TEPSerie getStaticSerie() {
        return this.staticPatient;
    }

    public TEPSerie getEndDynSerie() {
        return this.endDynPatient;
    }

    public void selectAorta(int startIndex, int endIndex)  {
       System.out.println("*************************");
       System.out.println("* DEBUT SELECTION AORTE *");
       System.out.println("*************************");
       this.startSummIndex = startIndex;
       this.endSummIndex = endIndex;
       this.startDynPatient.selectAorta(null, startSummIndex, endSummIndex);
    }
    
    /**
     * Ajoute les résultats aux résultats courant
     * @param aortaResults résultats à ajouter
     */
    private void addResults(AortaResults aortaResults) {
        
        this.aortaResults = this.aortaResults.addResults(aortaResults.getResultsTable());
       
    }

    public void roiSelected(Roi roi) {
        //On re-initialise les résultats courant pour éviter l'accumulation de plusieurs mesures
        this.aortaResults = new AortaResults(startDynPatient.getName());
        this.aortaResults.setRoi(roi);
        System.out.println("Roi selectionnée...");
        addResults(this.startDynPatient.getAortaResults());
        System.out.println("Résultats de la série dynamique 1 ajoutées...");
        this.staticPatient.selectAorta(roi, startSummIndex, endSummIndex);
        addResults(this.staticPatient.getAortaResults());
        System.out.println("Résultats de la série statique ajoutées...");
        this.endDynPatient.selectAorta(roi, startSummIndex, endSummIndex);
        addResults(this.endDynPatient.getAortaResults());
        System.out.println("Résultats de la série dynamique 2 ajoutées...");
        this.aortaResults.display(this.startDynPatient.getPixelUnity());
    }

    private void setParents() {
        startDynPatient.setParent(this);
        staticPatient.setParent(this);
        endDynPatient.setParent(this);
    }
       
    /**
     * On vérifie si tous les sous-acquisitions correspondent au même patient
     * et à la même étude.
     * Toutes les dicom images doivent avoir le même triplet <PatientName, PatientID, StudyInstanceUID>
     */
    private void checkMatchPatientSerieStudy(TEPSerie patient) throws PatientStudyException {
        boolean  allOK = true;
        int nbTimeFrames = patient.getNbBlocks();
        ArrayList<String> errorImages = new ArrayList<>();
        
        TimeFrame tf;
        DicomImage di;
        //On parcourt les times frames du patient puis les images
        for (int timeFrameIndex = 0; timeFrameIndex < nbTimeFrames; timeFrameIndex++ ) {
            try {
                tf = patient.getBlock(timeFrameIndex);
                for (int imageIndex = 0; imageIndex < tf.size(); imageIndex++) {
                    di = tf.getDicomImage(imageIndex);
                    if (di != null) {
                        String name = di.getAttribute(TagFromName.PatientName);
                        String id = di.getAttribute(TagFromName.PatientID);
                        String studyUID = di.getAttribute(TagFromName.StudyInstanceUID);
                    
                    
                        if ((!name.equals(this.tagsToCheck[0])) || (!id.equals(this.tagsToCheck[1])) || (!studyUID.equals(this.tagsToCheck[2]))) {
                            allOK = false;
                            errorImages.add("\"" + di.getAbsolutePath() + "\"\n");
                        }
                    }
                }
            } catch (BadParametersException ex) {
                Logger.getLogger(PatientMultiSeries.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!allOK)
            throw new PatientStudyException("Ces images n'appartiennent pas au bon patient : \n" + errorImages.toString());
    }
    
    /**
     * On vérifie si les séries sont dans le bon ordre
     */
    private void CheckSerieOrder() throws SeriesOrderException {
       
        Date startDynDate = this.startDynPatient.getSerieStartDate();
        Date staticDate = this.staticPatient.getSerieStartDate();
        Date endDynDate = this.endDynPatient.getSerieStartDate();
        
        if (startDynDate.after(staticDate))
            throw new SeriesOrderException("La série dynamique de départ doit être réalisée avant la série statique!!");
        
        if (staticDate.after(endDynDate))
            throw new SeriesOrderException("La série dynamique de fin doit être réalisée après la série statique!!");
        
        
            
    }
    
    
    
}
