package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.jfree.ui.RefineryUtilities;

import javafx.application.Platform;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class controller1 implements Initializable{

	ObservableList<String> atts;
	@FXML
	Button ouvrir;
	@FXML
	Button boiteAMous;
	@FXML
	TableView<instances> tableInstance; 
	ObservableList<instances> dataInstance ;
	@FXML
	ChoiceBox<String> AttChooser;
	@FXML
	TableView<attribut> tableAttributes;
	ObservableList<attribut> dataAttribute ;

	@FXML
	TextArea textArea;
	@FXML
	TextField Bq1,Bq3,Bmax,Bmin,Bmean;
	@FXML
	private BarChart<?, ?> barChart;
	@FXML
	Label nbrAttr, nbrInst, relation;
	public Instances inst;

	
	@FXML
	TextField textF;
	@FXML
	ListView<String> listView;
	FileChooser fileC;
	public static LinkedList<LinkedList<Double>> linkedList;
	

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}
	
	
	public void choice(ActionEvent event)throws Exception {
		String s=AttChooser.getValue();
		int k=atts.indexOf(s);
			if(inst.attribute(k).isNumeric()){
				double [] vecteur;
				vecteur = new double [inst.size()];
				for(int j=0;j<inst.size();j++){
					vecteur[j]=inst.instance(j).value(k);
				}
				
				
				vecteur = sort(vecteur,inst.size()); 
				Bq1.setText(String.valueOf(calQ1(vecteur)));
				Bq3.setText(String.valueOf(calQ3(vecteur)));
				Bmean.setText(Double.toString(inst.meanOrMode(inst.attribute(k))));
				//Bmean.setText(String.valueOf(calMean(vecteur)));
				Bmin.setText(Double.toString(inst.attributeStats(k).numericStats.min));
				Bmax.setText(Double.toString(inst.attributeStats(k).numericStats.max));
				new Thread(()->{
					Platform.runLater(() -> barChartAttribute(k));

				}).start();
			}
			Platform.runLater(() -> barChartAttribute(k));

		
		
	}
	
	public double[] sort (double [] vecteur, int taille){
		//double great = vecteur[0];
		for(int tailleMax=taille;tailleMax>0;tailleMax--){
			for(int i=1;i<tailleMax;i++){
				if(vecteur[i-1]>vecteur[i]){
					double stock=vecteur[i-1];
					vecteur[i-1]= vecteur[i];
					vecteur[i]=stock;
				}
			}
		}

		return vecteur;
	}
	public double calQ1(double[] values) {
		return values[Math.round(values.length/4)];
	}
	public double calQ3(double[] values) {
		return values[Math.round(values.length*3/4)];
	}
	public double calMean(double[] values) {
		return values[Math.round(values.length/2)];
	}
	public float Calmax(float[] values) {
		float max=values[0];
		for(int i=0;i<values.length;i++) {
			if(max<values[i]) max=values[i];
		}
		return max;
	}
	public float Calmin(float[] values) {
		float min=values[0];
		for(int i=0;i<values.length;i++) {
			if(min>values[i]) min=values[i];
		}
		return min;
	}
	
	public void Boutton1Action(ActionEvent event) throws Exception {
		fileC = new FileChooser();
		Bmax.clear();
		Bmean.clear();
		Bmin.clear();
		Bq1.clear();
		Bq3.clear();

		fileC.setInitialDirectory(new File("C:\\Program Files\\Weka-3-8\\data"));
		fileC.getExtensionFilters().addAll(
				new ExtensionFilter("ARFF files", "*.arff"));
		File selectedFile = fileC.showOpenDialog(null);
		if(selectedFile != null) {
			textF.setText(selectedFile.getAbsolutePath());
		}
		

	
		tableData(textF.getText());

		tableInstance.refresh();
		 tableAttributes.refresh();
		  

	}
	
	public void BouttonActionBoiteAMous(ActionEvent event) throws Exception {
		 final BoxPlot demo = new BoxPlot("Tiiiitree", textF.getText());
	     demo.pack();
	     RefineryUtilities.centerFrameOnScreen(demo);
	     demo.setVisible(true);

	}
	
	public void tableData(String path) throws Exception {
		
		
		 atts = FXCollections.observableArrayList();
		 dataInstance= FXCollections.observableArrayList();
		 dataAttribute= FXCollections.observableArrayList();
		
		 //AttChooser= new ChoiceBox<>();
		 DataSource p= new DataSource(path);
		 inst = p.getDataSet();
		 System.out.println("kakaaaaaaaaaaaaa");
		 System.out.println(inst.instance(1).value(1));
		 linkedList = new LinkedList<LinkedList<Double>>();
		 for(int i=0; i<inst.numAttributes(); i++) {
			 linkedList.add(new LinkedList<Double>());
			 for(int j=0; j<inst.numInstances(); j++) {
				if(inst.attribute(i).isNumeric())
					linkedList.get(i).add((inst.instance(j).value(i)));
			 }
			 //linkedList.add(hashData);
			System.out.println(Arrays.asList(linkedList.get(i)));
		 }
		 
		 
		// BoxPlot boxPlot = new BoxPlot("Box Plot",inst);
	       // boxPlot.pack();
	        //RefineryUtilities.centerFrameOnScreen(boxPlot);
	        //boxPlot.setVisible(true);
		 int cpt = 0;
		 
		 nbrInst.setText(""+inst.numInstances());
		 int c = inst.numAttributes();
		 relation.setText(""+inst.relationName());
		 nbrAttr.setText(""+c);
		 
		 //System.out.println(inst.numInstances());
		 
		 TableColumn<instances, String> colonne1 = new TableColumn<instances,String>("Num");
		 colonne1.setCellValueFactory(new PropertyValueFactory<instances, String>("numAttr"));
		 TableColumn<instances, String> colonne2 = new TableColumn<instances, String>("Instance");
		 colonne2.setCellValueFactory(new PropertyValueFactory<instances, String>("instance"));
		 colonne1.setMinWidth(5d);
		 colonne2.setMinWidth(300d);
		 tableInstance.getColumns().addAll(colonne1, colonne2);
		 tableInstance.setItems(dataInstance);
		 

		 while(cpt<inst.size()) {
			 
			dataInstance.add(new instances(Integer.toString(cpt),inst.get(cpt).toString()));
			//System.out.println( inst.get(cpt).toString());
			cpt++;
		}
		
		tableInstance.setItems(dataInstance);
		

		/**********************************************************
		 * table attribut
		 */
		 TableColumn<attribut, String> colonne3 = new TableColumn<attribut, String>("Num");
		 colonne3.setCellValueFactory(new PropertyValueFactory<attribut, String>("numAttr"));
		 TableColumn<attribut, String> colonne4 = new TableColumn<attribut, String>("Nom");
		 colonne4.setCellValueFactory(new PropertyValueFactory<attribut, String>("nomAttr"));
		 TableColumn<attribut, String> colonne5 = new TableColumn<attribut, String>("Type");
		 colonne5.setCellValueFactory(new PropertyValueFactory<attribut, String>("typeAttr"));
		 colonne3.setMinWidth(5d);
		 colonne4.setMinWidth(100d);
		 colonne5.setMinWidth(100d);
		 tableAttributes.getColumns().addAll(colonne3, colonne4, colonne5);
		 tableAttributes.setItems(dataAttribute);
		
		int cpt1 = 0;
		while(cpt1<inst.numAttributes()) {
			String[] splitAttr = inst.attribute(cpt1).toString().split(" ", 3);
			dataAttribute.add(new attribut(Integer.toString(cpt1+1), splitAttr[1], splitAttr[2]));
			cpt1++;
			//System.out.println("Display Atts");
			int k=0;
			
			for (int i=1;i<splitAttr.length;i=i+2)
			{
				 atts.add(new String(splitAttr[i].toString()));
				k=k+1;
			}
			
			
			
		}
		
	//atts.remove(atts.size()-1);
		AttChooser.getItems().setAll(atts);
		 //  AttChooser.getItems().addAll(atts);
			tableAttributes.setItems(dataAttribute);
			/*System.out.println("Les types des attributs:");
			System.out.println(inst.attribute(1));
			System.out.println(inst.attribute(2));
			System.out.println(inst.attribute(3));
			System.out.println(inst.attribute(4));*/
			
		 
	}
	
	public HashMap<String, Integer> calculateFreqAttributes(int numAttr){
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();

		//A revoir demain nchallah 
		for(int i=0; i<linkedList.get(numAttr).size(); i++) {
			String key = Double.toString(linkedList.get(numAttr).get(i));
			if(hashMap.containsKey(key)) {
				System.out.println("rymaaa");
				int tmp = hashMap.get(key);
				//System.out.println(numAttr);
				hashMap.put(key, tmp+1);
			}
			else {
				System.out.println("izaan");
				String strAttr =Double.toString(linkedList.get(numAttr).get(i));
				hashMap.put(strAttr, 1);
			}
		}
		
		 		
		return hashMap;
	}
	
	public int compteNbreAttr(LinkedList<String> link, String value) {
		int cpt=0;
		for(int i=0; i<link.size(); i++) {
			if(link.get(i).equals(value))
				cpt++;
		}
		return cpt;
	}
	
	
	public void barChartAttribute(int numAttr) {
		barChart.getData().clear();
		XYChart.Series series = new XYChart.Series();
		
		HashMap<String, Integer> newHash = new HashMap<String, Integer>();
		newHash = calculateFreqAttributes(numAttr);
		int x=0;
		
		 for (String key:newHash.keySet()){
            System.out.println("Key:" + key +" Value:" + newHash.get(key));// Get Key and value 
             
 			if(x<8) {series.getData().add(new XYChart.Data(key, newHash.get(key))); x++;}

        }

		barChart.getData().add(series);

	}

}
