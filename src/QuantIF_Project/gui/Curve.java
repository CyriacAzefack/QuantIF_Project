package QuantIF_Project.gui;

import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Curve extends ApplicationFrame {
    
    private String xlegend;
    private String ylegend;
  
    
    /**
     * Crée fenêtre affichant une courbe.
     * L'objet est de type JFrame
     * @param applicationTitle Nome de la fenêtre d'affichage
     * @param chartTitle Titre de la courbe
     * @param xlegend Legende axe des abscisses
     * @param ylegend Legende axe des ordonnées
     * @param x tableau des abscisses
     * @param y tableau des ordonnées
     */
    public Curve( String applicationTitle , String chartTitle, String xlegend, String ylegend, double[] x, double[] y)    {
        super(applicationTitle);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.xlegend = xlegend;
        this.ylegend = ylegend;
        JFreeChart lineChart = null;
        try {
            lineChart = ChartFactory.createLineChart(
                    chartTitle,
                    this.xlegend, this.ylegend,
                    createDataset(x, y),
                    PlotOrientation.VERTICAL,
                    true,true,false);
            
            
        } catch (Exception ex) {
            Logger.getLogger(Curve.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        ChartPanel chartPanel = new ChartPanel( lineChart );
        //
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        setContentPane( chartPanel );
        
        //La fenêtre s'adapte à la taille des données
        this.pack( );
        
       
   }
    
    private DefaultCategoryDataset createDataset(double[] x, double[] y) throws Exception  {
        
        if(x.length != y.length)
            throw new Exception("Le tableau d'abscisses et d'ordonnées n'ont pas la même taille");
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
        for(int i = 0; i < x.length; i++) {
            dataset.addValue(y[i], this.ylegend, String.valueOf(x[i]).substring(0, 3)); //On prends juste 3 chiffres signiicatif
        }
        
        return dataset;
   }
    
    @Override
   public void windowClosing(WindowEvent event) {
       this.dispose();
   }
   /**
   public static void main( String[ ] args ) 
   {
      Curve chart = new Curve(
      "School Vs Years" ,
      "Numer of Schools vs years");

      chart.pack( );
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );
   }
   **/
}