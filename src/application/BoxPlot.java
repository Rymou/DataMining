package application;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.Log;
import org.jfree.util.LogContext;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class BoxPlot extends ApplicationFrame {

    /** Access to logging facilities. */
    private static final LogContext LOGGER = Log.createContext(BoxPlot.class);

    public BoxPlot(final String title, String path) throws Exception {//ajout des attribut data et attribut

        super(title);
        
        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset(path);

        final CategoryAxis xAxis = new CategoryAxis("Type");
        final NumberAxis yAxis = new NumberAxis("Value");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        plot.setBackgroundPaint(new Color(224,224,224));


        final JFreeChart chart = new JFreeChart(
            "Box-and-Whisker Demo",
            new Font("SansSerif", Font.BOLD, 14),
            plot,
            true
        );
        chart.setBackgroundPaint(new Color(255,255,255));
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 400));
        setContentPane(chartPanel);

    }

    private BoxAndWhiskerCategoryDataset createSampleDataset(String path) throws Exception {//inclure le dataset et les attributs
        
    
        
		DataSource p= new DataSource(path);
		Instances inst = p.getDataSet();
	    int seriesCount = 3;
        int categoryCount = inst.numAttributes(); // les attributs
        int entityCount = 22;
        
        //Récupération du dataset dans une linkedlist
        LinkedList<LinkedList<Double>> linkedList = new LinkedList<LinkedList<Double>>();
		 for(int i=0; i<inst.numAttributes(); i++) {
			 linkedList.add(new LinkedList<Double>());
			 for(int j=0; j<inst.numInstances(); j++) {
				if(inst.attribute(i).isNumeric()) {
					linkedList.get(i).add((inst.instance(j).value(i)));
					//System.out.println(inst.instance(j).value(i));
				}
					
			 }
			 //linkedList.add(hashData);
			System.out.println(Arrays.asList(linkedList.get(i)));
		 }
        
        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        
       // for (int i = 0; i < linkedList.get(i).size(); i++) {
           
            for (int i = 0; i < categoryCount; i++) {
            	final List list = linkedList.get(i);
                list.sort(Comparator.naturalOrder());
                LOGGER.debug("Adding series " + i);
                LOGGER.debug(list.toString());
                dataset.add(list, "Series " + i, " Type " + (i+1));
            }
            
       // }

        return dataset;
    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * For testing from the command line.
     *
     * @param args  ignored.
     * @throws Exception 
     */
   /* public static void main(final String[] args) throws Exception {
    	//on fait appel dans le bouton approprié

        //Log.getInstance().addTarget(new PrintStreamLogTarget(System.out));
        final BoxPlot demo = new BoxPlot("Tiiiitree", "C:\\Program Files\\Weka-3-8\\data\\iris.arff");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }*/

}