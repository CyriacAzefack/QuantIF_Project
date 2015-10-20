/*
 * This Code belongs to his creator Cyriac Azefack and the lab QuanttIF of the "Centre Henri Becquerel"
 *   * 
 */
package QuantIF_Project.patient;

import QuantIF_Project.gui.Curve;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Cyriac
 */
public class AortaResults {
    private final Roi roi;
    
    private final ResultsTable resultTable;
    
    private final String title;
    
    
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

    public void display(String yMeasureUnity) {
        int timeColumnIndex = this.resultTable.getColumnIndex("Time");
        double[] dataToDisplay = this.resultTable.getColumnAsDoubles(1); //On affiche la VALEUR MOYENNE de la ROI
        double[] timeArray = this.resultTable.getColumnAsDoubles(timeColumnIndex);
        // On affiche la courbe
        Curve chart = new Curve(this.title + " Graphe", "Moyenne de la ROI", "Temps (min)",  yMeasureUnity, timeArray, dataToDisplay);
        chart.setVisible( true );
        //On place la courbe au centre de l'écran
        RefineryUtilities.centerFrameOnScreen(chart);
        
        //On affiche le tablea 
        this.resultTable.show(this.title + " Tableau de résultats");
    }
    
    public Roi getRoi() {
        return this.roi;
    }
    
    public ResultsTable getResultsTable() {
        return this.resultTable;
    }
}
