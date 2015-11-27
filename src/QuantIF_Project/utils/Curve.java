/* 
 * This Code belongs to his creator Cyriac Azefack and the lab QuantIF of the "Centre Henri Becquerel of Rouen"
 *   * 
 */
package QuantIF_Project.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Curve extends ApplicationFrame {
    
    private final String xlegend;
    private final String ylegend;
    private final XYSeriesCollection  dataset;
    private final XYLineAndShapeRenderer renderer;
    private JFreeChart xylineChart;
    private String chartTitle;
    
    
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
        this.dataset = new XYSeriesCollection ();
        this.renderer = new XYLineAndShapeRenderer( );
        this.xylineChart = null;
        this.chartTitle = chartTitle;
        addData(x, y, chartTitle);
        //draw(chartTitle);
        
        
        
       
   }
    
    /**
     * Crée les données d'affichage
     * @param x valeurs en abscisses
     * @param y valeurs en ordonnées
     * @return
     * @throws Exception 
     */
    private XYSeries createXYSeries(double[] x, double[] y, String title) {
        
        if(x.length != y.length)
            throw new IllegalArgumentException("Le tableau d'abscisses et d'ordonnées n'ont pas la même taille");
       
        XYSeries data = new XYSeries(title);
        for(int i = 0; i < x.length; i++) {
            data.add(x[i], y[i]); //On prends juste 3 chiffres signiicatif
        }
        
        
        return data;
   }
    
   public final void addData(double[] x, double[] y, String title) {
        if(x.length != y.length)
            throw new IllegalArgumentException("Le tableau d'abscisses et d'ordonnées n'ont pas la même taille");
        this.dataset.addSeries(createXYSeries(x, y, title));
        draw();
        
   }
   
   public final void addData(double[] x, double[] y, String title, Color color, float thickness) {
       addData(x, y, title);
       System.out.println("Add Data sophistiqué");
       int n = this.dataset.getSeriesCount();
       this.renderer.setSeriesPaint(n-1, color);
       this.renderer.setSeriesStroke(n-1, new BasicStroke(thickness));
            
       XYPlot plot = this.xylineChart.getXYPlot();
       //plot.setRenderer(n, renderer);
       plot.setRenderer(renderer);
       
       
   }
   
    @Override
   public void windowClosing(WindowEvent event) {
       this.dispose();
   }

    private void draw() {
       
       
        this.xylineChart = ChartFactory.createXYLineChart(
            chartTitle,
            this.xlegend, this.ylegend,
            this.dataset,
            PlotOrientation.VERTICAL,
            true,true,false);

            
       
         
        ChartPanel chartPanel = new ChartPanel( xylineChart );
        
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        setContentPane( chartPanel );
        
        //La fenêtre s'adapte à la taille des données
        this.pack( );
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