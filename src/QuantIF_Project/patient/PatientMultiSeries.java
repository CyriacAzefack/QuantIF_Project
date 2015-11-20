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
import QuantIF_Project.serie.Block;
import QuantIF_Project.serie.Serie;
import QuantIF_Project.serie.TAPSerie;
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
    private final TEPSerie startTEPSerie;
    
    /**
     * Série statique du milieu d'acquisition
     */
    private final TAPSerie staticTAPSerie;
    
    /**
     * Série dynamique de fin d'acquisition
     */
    private final TEPSerie endTEPSerie;
    
    /**
     * Résultats de sélection de l'aorte
     */
    private AortaResults aortaResults;
    
    private int startSummIndex, endSummIndex;
    
    /**
     * Champs permettant de déterminer si on affaire au même patient
     */
    private final String[] tagsToCheck;
    
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
    public PatientMultiSeries(TEPSerie startDynSerie, TAPSerie staticSerie, TEPSerie endDynSerie) throws PatientStudyException, SeriesOrderException {
        
        
        this.startTEPSerie = startDynSerie;
        
        
        
        this.staticTAPSerie = staticSerie;
        
        
        this.endTEPSerie = endDynSerie;
        
        setParents();
        
        this.tagsToCheck = new String [3];
       
            
        //On récupère ce format sur la premiere image de la sous-acquisition de départ
        /*
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
       */
        
        
        this.aortaResults = new AortaResults(startTEPSerie.getName());
        
        //CheckSerieOrder();
        
    }
    
    public TEPSerie getStartDynSerie() {
        return this.startTEPSerie;
    }

    public TAPSerie getStaticSerie() {
        return this.staticTAPSerie;
    }

    public TEPSerie getEndDynSerie() {
        return this.endTEPSerie;
    }

    public void selectAorta(int startIndex, int endIndex)  {
       System.out.println("*************************");
       System.out.println("* DEBUT SELECTION AORTE *");
       System.out.println("*************************");
       this.startSummIndex = startIndex;
       this.endSummIndex = endIndex;
       this.startTEPSerie.selectAorta(null, startSummIndex, endSummIndex);
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
        this.aortaResults = new AortaResults(startTEPSerie.getName());
        this.aortaResults.setRoi(roi);
        System.out.println("Roi selectionnée...");
        //On ajoute les résultats pour la série TEP de départ
        addResults(this.startTEPSerie.getAortaResults());
        System.out.println("Résultats de la série dynamique 1 ajoutées...");
        
        //On fait les calculs pour la série TAP
        this.staticTAPSerie.selectAorta(roi, 0, 0);
        //On ajoute les résultats pour la série statique TAP
        addResults(this.staticTAPSerie.getAortaResults());
        System.out.println("Résultats de la série statique ajoutées...");
        
        //On fait les calculs pour la série TEP de fin
        this.endTEPSerie.selectAorta(roi, 0, this.endTEPSerie.getNbBlocks() - 1 );
        //On ajoute les résultats pour la série TEP de fin
        addResults(this.endTEPSerie.getAortaResults());
        System.out.println("Résultats de la série dynamique 2 ajoutées...");
        
        //On affiche l'ensemble des résultats
        this.aortaResults.display(this.startTEPSerie.getPixelUnity());
    }

    private void setParents() {
        startTEPSerie.setParent(this);
        staticTAPSerie.setParent(this);
        endTEPSerie.setParent(this);
    }
       
    /**
     * On vérifie si tous les sous-acquisitions correspondent au même patient
     * et à la même étude.
     * Toutes les dicom images doivent avoir le même triplet <PatientName, PatientID, StudyInstanceUID>
     */
    private void checkMatchPatientSerieStudy(Serie patient) throws PatientStudyException {
        boolean  allOK = true;
        int nbTimeFrames = patient.getNbBlocks();
        ArrayList<String> errorImages = new ArrayList<>();
        
        Block block;
        DicomImage di;
        //On parcourt les times frames du patient puis les images
        for (int timeFrameIndex = 0; timeFrameIndex < nbTimeFrames; timeFrameIndex++ ) {
            try {
                block = patient.getBlock(timeFrameIndex);
                for (int imageIndex = 0; imageIndex < block.size(); imageIndex++) {
                    di = block.getDicomImage(imageIndex);
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
       
        Date startDynDate = this.startTEPSerie.getSerieStartDate();
        Date staticDate = this.staticTAPSerie.getSerieStartDate();
        Date endDynDate = this.endTEPSerie.getSerieStartDate();
        
        if (startDynDate.after(staticDate))
            throw new SeriesOrderException("La série dynamique de départ doit être réalisée avant la série statique!!");
        
        if (staticDate.after(endDynDate))
            throw new SeriesOrderException("La série dynamique de fin doit être réalisée après la série statique!!");
        
        
            
    }
    
    
    
}
