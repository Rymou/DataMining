package application;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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

    public BoxPlot(final String title, String path, int filtre) throws Exception {//ajout des attribut data et attribut

        super(title);
        
        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset(path, filtre);

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

    private BoxAndWhiskerCategoryDataset createSampleDataset(String path, int filtre) throws Exception {//inclure le dataset et les attributs
        
    
        
		DataSource p= new DataSource(path);
		Instances inst = p.getDataSet();
		if(filtre == 2) {
			 inst = missingValue(inst);
			 inst = normalization(inst);
		}
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
                dataset.add(list, "Dataset " +inst.relationName(), "" + inst.attribute(i).name());
            }
            
       // }

        return dataset;
    }
    
    
    public Instances missingValue(Instances inst) throws Exception {
		Vector<Integer> missingInstances=new Vector<Integer>();
		Vector<String> classValue=new Vector<String>();
		if(inst.attribute(inst.numAttributes()-1).isNumeric()) {
			for(int i=0;i<inst.size();i++) {
				if(!classValue.contains(Double.toString(inst.instance(i).value(inst.numAttributes()-1))))
				{
					classValue.add(Double.toString(inst.instance(i).value(inst.numAttributes()-1)));
				}
			}
		}
		if(inst.attribute(inst.numAttributes()-1).isNominal()) {
			for(int i=0;i<inst.size();i++) {
				if(!classValue.contains(inst.instance(i).stringValue(inst.numAttributes()-1)))
				{
					classValue.add((inst.instance(i).stringValue(inst.numAttributes()-1)));
				}
			}
		}
	
		int j;
		for(int at=0;at<inst.numAttributes();at++) {
			for(int c=0;c<classValue.size();c++) {
				boolean missing=false;
				if(inst.attribute(inst.numAttributes()-1).isNominal())
				{j=0;
				while(!missing && j<inst.size() ) {
					
					if(inst.attribute(at).isNumeric() && Double.toString(inst.instance(j).value(at)).contentEquals("NaN") && inst.instance(j).stringValue(inst.numAttributes()-1).contentEquals(classValue.get(c)) )
					{missing=true;}
					if(inst.attribute(at).isNominal() && inst.instance(j).stringValue(at).contentEquals("?") && inst.instance(j).stringValue(inst.numAttributes()-1).contentEquals(classValue.get(c)) )
					{missing=true;}
					
					
						j++;
				}
				}
				if(inst.attribute(inst.numAttributes()-1).isNumeric())
				{j=0;
				while(!missing && j<inst.size() ) {
					
					if(inst.attribute(at).isNumeric() && Double.toString(inst.instance(j).value(at)).contentEquals("NaN") && Double.toString(inst.instance(j).value(inst.numAttributes()-1)).contentEquals(classValue.get(c)) )
					{missing=true;}
					if(inst.attribute(at).isNominal() && inst.instance(j).stringValue(at).contentEquals("?") && Double.toString(inst.instance(j).value(inst.numAttributes()-1)).contentEquals(classValue.get(c)) )
					{missing=true;}
					
					
						j++;
				}
				}
				if(missing) {
					if(inst.attribute(at).isNumeric()) {
						double moyenne=0;
						int totale=0;
						for(int i =0;i<inst.size();i++) {
							if(!Double.toString(inst.instance(i).value(at)).contentEquals("NaN") && inst.instance(i).stringValue(inst.numAttributes()-1).contentEquals(classValue.get(c)))
							{   moyenne+=inst.instance(i).value(at);
								totale = totale+1;
							}
						}
						moyenne=moyenne/totale;
						for(int i =0;i<inst.size();i++) {
							if(Double.toString(inst.instance(i).value(at)).contentEquals("NaN") && inst.instance(i).stringValue(inst.numAttributes()-1).contentEquals(classValue.get(c)))
							{   
								inst.instance(i).setValue(at,moyenne);
							}
						}
						
					}
					if(inst.attribute(at).isNominal()) {
						HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
						int maxcount=0;
						String maxAttr=null;
						
						for(int i=0; i<inst.size(); i++) {
							String key = inst.instance(i).stringValue(at);
							if(hashMap.containsKey(key) && inst.instance(i).stringValue(inst.numAttributes()-1).contentEquals(classValue.get(c))) {
								int tmp = hashMap.get(key);
								hashMap.put(key, tmp+1);
								
							}
							else {
								if(!inst.instance(i).stringValue(at).equals("?"))
								{String strAttr =inst.instance(i).stringValue(at);
								hashMap.put(strAttr, 1);}
							}
						}
						 for (String key:hashMap.keySet()){
				             //System.out.println("Key:" + key +" Value:" + hashMap.get(key));// Get Key and value 
				             if(maxcount<hashMap.get(key)) {
				            	 maxcount=hashMap.get(key);
				            	 maxAttr=key;
				             }
				 			

				        }
						for(int k=0;k<inst.size();k++){ //remplacer les valeurs manquantes
							if(inst.instance(k).stringValue(at).contentEquals("?") && inst.instance(k).stringValue(inst.numAttributes()-1).contentEquals(classValue.get(c)))
								
								inst.instance(k).setValue(at, maxAttr);
						}
					
					}
					}
					
					
				}
				
			}
		return inst;
		}
    public Instances normalization(Instances inst) {
		double min,max;
			for(int i=0;i<inst.numAttributes();i++){
				if(inst.attribute(i).isNumeric()){
					 min = inst.attributeStats(i).numericStats.min;
					 max = inst.attributeStats(i).numericStats.max;

					for(int j=0;j<inst.numInstances();j++){
						
						inst.instance(j).setValue(i, (inst.instance(j).value(i)-min) / (max-min));
					}
				}
			}
			return inst;
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