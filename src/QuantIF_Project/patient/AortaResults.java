/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.patient;

import QuantIF_Project.gui.Main_Window;
import QuantIF_Project.utils.Curve;
import ij.gui.Roi;
import ij.io.RoiEncoder;
import ij.measure.ResultsTable;
import java.io.File;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Cyriac
 */
public class AortaResults {
    private  Roi roi;
    
    private ResultsTable resultTable;
    
    private  String title;
    
    
    /**
     * Construit des résultats de l'aorte
     * @param serieName Titre des résultats
     * @param roi Roi sélectionnée
     * @param resultTable tableau de résultats
     */
    public AortaResults(String serieName, Roi roi, ResultsTable resultTable) {
        this.roi = roi;
        this.resultTable = resultTable;
        this.title = serieName + "---" + this.roi.getName();
    }

    AortaResults(String serieName) {
       this.roi = null;
       this.resultTable = new ResultsTable();
       this.title = serieName;
    }
    
    /**
     * 
     * @param yMeasureUnity 
     */
    public void display(String yMeasureUnity) {
        this.resultTable.show(this.title + " Tableau de résultats");
        int timeColumnIndex = this.resultTable.getColumnIndex("Mid time (sec)");
        
        double[] dataToDisplay = this.resultTable.getColumnAsDoubles(1); //On affiche la VALEUR MOYENNE de la ROI
        double[] timeArray = this.resultTable.getColumnAsDoubles(timeColumnIndex); // temps en min
        
        for (int i = 0; i < timeArray.length; i++) {
           timeArray[i] /= 60;
        }
        // On affiche la courbe
        Curve chart = new Curve(this.title + " Graphe", "Concentration moyenne dans l'aorte", "Temps (min)",  yMeasureUnity, timeArray, dataToDisplay);
        chart.setVisible( true );
        //On place la courbe au centre de l'écran
        RefineryUtilities.centerFrameOnScreen(chart);
        
        //L'aire sous la courbe
        /*
        double AUC;
        try {
            AUC = MathUtils.AreaUnderTheCurve(timeArray, dataToDisplay);
            System.out.println("### AIRE SOUS LA COURBE ###");
            System.out.println("### AUC = " + AUC);
            System.out.println("###########################");
        } catch (BadParametersException ex) {
            Logger.getLogger(AortaResults.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
       
              
        
    }
    
    public Roi getRoi() {
        return this.roi;
    }
    
    public void setRoi(Roi roi) {
        this.roi = roi;
        this.title += "---" + roi.getName();
    }
    
    public ResultsTable getResultsTable() {
        return this.resultTable;
    }
    
    public void setResultsTable(ResultsTable rt) {
        this.resultTable = rt;
    }
    
    /**
     * Ajoute les résultats passés en paramètres aux résultats
     * courants
     * @param rtToAdd résultats à ajouter
     * @return AortaResults
     */
    public AortaResults addResults(ResultsTable rtToAdd) {
        System.out.println("Size de results avant l'ajout de résultats: " + this.resultTable.size());
        if(this.resultTable.size() == 0) {
            resultTable = rtToAdd;
            return this;
        }
        
       
       
        //On parcourt les lignes
        for (int rowIndex = 0; rowIndex < rtToAdd.size(); rowIndex++) {
            this.resultTable.incrementCounter();
             int columnIndex = 0;
       
            //System.out.println("Counter Value : " + this.resultTable.getCounter());
            while(rtToAdd.columnExists(columnIndex)) {
                
                 //On parcourt les colonnes
                this.resultTable.addValue(columnIndex, rtToAdd.getValueAsDouble(columnIndex, rowIndex));
                
                columnIndex++;
            }
            
        }
           
        /*
        this.resultTable.addResults();
        this.resultTable.updateResults();
        */
        
        System.out.println("Size de results après l'ajout de résultats : " + this.resultTable.size());
        return this;
    }
    
    /**
     * Charge et affiche des résultats d'aorte
     * @param rt ResultTable
     * @param roi roi
     */
    public void loadResultsTable(ResultsTable rt, Roi roi) {
        this.setResultsTable(rt);
        this.setRoi(roi);
        //Affichage des résultats chargés
        this.display(title);
    }
    
    /**
     * Sauvegarde les données dans un dossier
     * @param path chemin du dossier
     */
    public void save(String path) {
        
        path = Main_Window.outputDir() + path;
        File file  = new File(path);
        file.mkdirs();
        
        //On sauvegarde les ResultsTable
        this.resultTable.save(path+"\\resultsTable");
        
        //On sauvegarde la ROI
        
        RoiEncoder.save(roi, path+"\\roi");
        
        
    }
    
    
}
