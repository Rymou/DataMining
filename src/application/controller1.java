package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.plaf.synth.SynthSeparatorUI;

import Traitement.Classify;
import Traitement.DBScan;

import org.jfree.ui.RefineryUtilities;

import Traitement.Apriori;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class controller1 implements Initializable{

	static int typeAttribute; // 0=numerique 1=nominal
	ObservableList<String> atts;
	@FXML
	Button ouvrir;
	@FXML
	Button boiteAMous;
	@FXML
	TableView<instances> tableInstance; 
	ObservableList<instances> dataInstance ;
	@FXML
	TableView<FreqItemSet> tableKnn; 
	public ObservableList<FreqItemSet> dataKnn ;
	@FXML
	TableView<FreqItemSet> tableDBScan; 
	public ObservableList<FreqItemSet> dataDBScan ;
	@FXML
	ChoiceBox<String> AttChooser;
	@FXML
	ComboBox<String> filtres;
	@FXML
	TableView<attribut> tableAttributes;
	ObservableList<attribut> dataAttribute ;

	@FXML
	TextArea textArea;
	@FXML
	TextField Bq1,Bq3,Bmax,Bmin,Bmean, mode, mediane, midRange, symetrie;
	@FXML
	private BarChart<?, ?> barChart;
	@FXML
	Label nbrAttr, nbrInst, relation;
	public Instances inst;
	float rate=0;

	@FXML
	TextField textFKNN;
	@FXML
	TextField textFVoisins;
	@FXML
	TextField textF;
	@FXML
	ListView<String> listView;
	FileChooser fileC;
	DecimalFormat df = new DecimalFormat("#.##");

	public static LinkedList<LinkedList<String>> linkedList;
	static int filtreChoix;
	
	
	
	@FXML
	TableView<FreqItemSet> tableFreqItems;
	public ObservableList<FreqItemSet> dataItemFreq;
	@FXML
	TableView<rules> tableRulesAssociation;
	public ObservableList<rules> dataRules;
	@FXML
	Button start;
	@FXML
	Button start2;
	@FXML
	Button start3;
	@FXML
	TextField minSup;
	double minS = 0.0;
	int minP = 0;
	double epsilon = 0.0;
	@FXML
	TextField confMin;
	@FXML
	TextField textFEps;
	@FXML
	TextField textFMinPts;
	
	double minConf = 0.0;
	@FXML
	public Label accuracy;
	@FXML
	public TextArea acc;
	@FXML
	public TextArea nbClust;
	@FXML
	public TextArea nbBruit;
	@FXML
	public TextArea inertieIntra;
	@FXML
	public TextArea inertieInter;


	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.filtres.getItems().addAll(
				"Aucun",
				"Normalisation et missing values"
				);
		filtres.setPromptText("Choisir un filtre");
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
	
	
	
	public void choice(ActionEvent event)throws Exception {

		String s=AttChooser.getValue();
		int k=atts.indexOf(s);
			if(inst.attribute(k).isNumeric()&&(!inst.attribute(k).isDate())){
				ArrayList<Double>  vecteur;
				vecteur = new ArrayList<Double>();
				for(int j=0;j<inst.size();j++){
					vecteur.add(inst.instance(j).value(k));
				}	
				Bq1.clear();
				Bq3.clear();
				Bmean.clear();
				Bmin.clear();
				Bmax.clear();
				mode.clear();
				midRange.clear();
				mediane.clear();
				symetrie.clear();
				vecteur.sort(Comparator.naturalOrder()); 
				Bq1.setText(String.valueOf(calQ1(vecteur)));
				Bq3.setText(String.valueOf(calQ3(vecteur)));
				Bmean.setText(Double.toString(inst.meanOrMode(inst.attribute(k))));
				Bmin.setText(Double.toString(inst.attributeStats(k).numericStats.min));
				Bmax.setText(Double.toString(inst.attributeStats(k).numericStats.max));
				mode.setText(Double.toString(inst.meanOrMode(inst.attribute(k))));
				midRange.setText(String.valueOf(calMidR(vecteur)));
				mediane.setText(String.valueOf(calMedian(vecteur)));
				if((Math.abs(calMedian(vecteur)-(Math.abs(inst.meanOrMode(inst.attribute(k))))))<=0.1) {
					if((Math.abs(inst.meanOrMode(inst.attribute(k)))-Math.abs(inst.meanOrMode(inst.attribute(k))))<=0.1) 
					 {
						symetrie.setText("Vrai");
						
					}
					else
						symetrie.setText("Faux");
				}
				else
					symetrie.setText("Faux");
						
				

			}
			else {
				if((inst.attribute(k).isNominal())&& (!inst.attribute(k).isDate())) {
					Bq1.clear();
					Bq3.clear();
					Bmean.clear();
					Bmin.clear();
					Bmax.clear();
					mode.clear();
					mediane.clear();
					symetrie.clear();
					
					String [] vecteur;
					vecteur = new String[inst.size()];
					for(int j=0;j<inst.size();j++){
						vecteur[j]=(inst.instance(j).stringValue(k));
					}
					String name = inst.attribute(k).value( (int) (inst.meanOrMode(k)));
					
					mode.setText(name);
					
				}
				else {
					if((inst.attribute(k).isDate())){
						Bq1.clear();
						Bq3.clear();
						Bmean.clear();
						Bmin.clear();
						Bmax.clear();
						mode.clear();
						mediane.clear();
						symetrie.clear();
					}
						
					
				}
			}
			new Thread(()->{
				Platform.runLater(() -> barChartAttribute(k));

			}).start();
			Platform.runLater(() -> barChartAttribute(k));

		
		
	}
	
	public double calQ1(ArrayList<Double> values) {

		List<Double> l = new LinkedList<Double>();
		l.addAll(values);
		l.sort(Comparator.naturalOrder());
		//values.sort(Comparator.naturalOrder());
		return l.get(Math.round(l.size()/4));
	}
	public double calMedian(ArrayList<Double> values) {
		LinkedList<Double> l = new LinkedList<Double>();
		l.addAll(values);
		l.sort(Comparator.naturalOrder());
		System.out.println(l.toString());
		return l.get(Math.round(l.size()/2));
	}
	public double calQ3(ArrayList<Double> values) {
		//values.sort(Comparator.naturalOrder());
		LinkedList<Double> l = new LinkedList<Double>();
		l.addAll(values);
		l.sort(Comparator.naturalOrder());
		System.out.println(l.toString());
		return l.get(Math.round(l.size()*3/4));
	}
	public double calMean(ArrayList<Double> values) {
		LinkedList<Double> l = new LinkedList<Double>();
		l.addAll(values);
		l.sort(Comparator.naturalOrder());
		//values.sort(Comparator.naturalOrder());
		return l.get(Math.round(l.size()/2));
	}
	
	public double calMidR(ArrayList<Double> values) {
		double midR = (Calmax(values)+Calmin(values))/2;
		return midR;
	}
	public double Calmax(ArrayList<Double> values) {
		LinkedList<Double> l = new LinkedList<Double>();
		l.addAll(values);
		l.sort(Comparator.naturalOrder());
		
		//values.sort(Comparator.naturalOrder());
		Double max=l.get(0);
		for(int i=0;i<l.size();i++) {
			if(max<l.get(i)) max=l.get(i);
		}
		return max;
	}
	public Double Calmin(ArrayList<Double> values) {
		LinkedList<Double> l = new LinkedList<Double>();
		l.addAll(values);
		l.sort(Comparator.naturalOrder());
		//values.sort(Comparator.naturalOrder());
		Double min=l.get(0);
		for(int i=0;i<l.size();i++) {
			if(min>l.get(i)) min=l.get(i);
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
		mode.clear();
		midRange.clear();
		mediane.clear();
		



		filtres.getValue();
		if(filtres.getValue().equals("Aucun"))
			filtreChoix = 1;
		if(filtres.getValue().equals("Normalisation et missing values"))
			filtreChoix = 2;
		
		fileC.setInitialDirectory(new File("C:\\Program Files\\Weka-3-8\\data"));
		fileC.getExtensionFilters().addAll(
				new ExtensionFilter("ARFF files", "*.arff"));
		File selectedFile = fileC.showOpenDialog(null);
		if(selectedFile != null) {
			textF.setText(selectedFile.getAbsolutePath());
		}

		
		tableData(textF.getText());

		
		barChart.getData().clear();
		tableInstance.refresh();
		tableAttributes.refresh();
		  

	}
	
	public void BouttonActionBoiteAMous(ActionEvent event) throws Exception {
		 final BoxPlot demo = new BoxPlot("Boites à moustaches", textF.getText(), filtreChoix);
	     demo.pack();
	     RefineryUtilities.centerFrameOnScreen(demo);
	     demo.setVisible(true);

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
	
	
	
	public void tableData(String path) throws Exception {
		
		
		tableAttributes.getColumns().clear();
		tableInstance.getColumns().clear();
	
		 atts = FXCollections.observableArrayList();
		 dataInstance= FXCollections.observableArrayList();
		 dataAttribute= FXCollections.observableArrayList();
		
		 DataSource p= new DataSource(path);
		 inst = p.getDataSet();
		 if(filtreChoix==2) {
			 inst = missingValue(inst);
			 inst = normalization(inst);
		 }		 
		 linkedList = new LinkedList<LinkedList<String>>();
		 for(int i=0; i<inst.numAttributes(); i++) {
			 linkedList.add(new LinkedList<String>());
			 String ligne="";
			 for(int j=0; j<inst.numInstances(); j++) {
				 
				if((inst.attribute(i).isNumeric())&&(!inst.attribute(i).isDate())) {
					typeAttribute = 0;
					String d = df.format(inst.instance(j).value(i));


					//System.out.println("DMMMMMMMMM");
					//System.out.println(d);
					linkedList.get(i).add(d);
				}
				else {
					
					typeAttribute = 1;
					if((inst.attribute(i).isNominal())&&(!inst.attribute(i).isDate()))
						linkedList.get(i).add(inst.instance(j).stringValue(i));

				}
			 }
			
			//System.out.println(Arrays.asList(linkedList.get(i)));
		 }
		 
		 

		 int cpt = 0;
		 
		 nbrInst.setText(""+inst.numInstances());
		 int c = inst.numAttributes();
		 relation.setText(""+inst.relationName());
		 nbrAttr.setText(""+c);
		 		 
		 TableColumn<instances, String> colonne1 = new TableColumn<instances,String>("Num");
		 colonne1.setCellValueFactory(new PropertyValueFactory<instances, String>("numAttr"));
		 TableColumn<instances, String> colonne2 = new TableColumn<instances, String>("Instance");
		 colonne2.setCellValueFactory(new PropertyValueFactory<instances, String>("instance"));
		 colonne1.setMinWidth(5d);
		 colonne2.setMinWidth(300d);
		 tableInstance.getColumns().addAll(colonne1, colonne2);
		 tableInstance.setItems(dataInstance);
		 
		 
		 //Création du fichier
			File newFile = new File("C:\\Users\\USER\\Documents\\Master2\\DataMining\\dataSet.txt");
			
			newFile.createNewFile();
			
			FileWriter fileW = new FileWriter(newFile);
			BufferedWriter bw = new BufferedWriter(fileW);
			
			
			
			//String path = "C:\\Users\\USER\\Documents\\Master2\\DataMining\\dataSet.txt";
			
		 
		 
		 
		 

		 while(cpt<inst.size()) {
			 
			dataInstance.add(new instances(Integer.toString(cpt),inst.get(cpt).toString()));
			bw.write(inst.get(cpt).toString());
			String newLineChar = System.getProperty("line.separator");
			bw.write(newLineChar);
			//System.out.println( inst.get(cpt).toString());
			cpt++;
		}
		 bw.close();
		
		tableInstance.setItems(dataInstance);
		
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
			int k=0;
			
			for (int i=1;i<splitAttr.length;i=i+2)
			{
				 atts.add(new String(splitAttr[i].toString()));
				k=k+1;
			}
			
			
			
		}
		
		AttChooser.getItems().setAll(atts);
		tableAttributes.setItems(dataAttribute);
		
	}
	
	
	
	//Seulement pour les attributs nominaux
	public HashMap<String, Integer> calculateFreqAttributes(int numAttr){
		HashMap<String, Integer> hashMap = new HashMap<String, Integer>();

		for(int i=0; i<linkedList.get(numAttr).size(); i++) {
			
			String key = linkedList.get(numAttr).get(i);
			if(hashMap.containsKey(key)) {
				//System.out.println("rymaaa");
				int tmp = hashMap.get(key);
				//System.out.println(numAttr);
				hashMap.put(key, tmp+1);
			}
			else {
				String strAttr =linkedList.get(numAttr).get(i);
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
	
	
	public ArrayList<Double> diviserAttribute(int numAttr, ArrayList<Double> array) {
		array.sort(Comparator.naturalOrder());
		ArrayList<Double> a = new ArrayList<Double>();
		
		a.add((double) (array.indexOf((calQ1(array)))));
		a.add((double) ((array.indexOf(calMean(array)))-(array.indexOf(calQ1(array)))+1));
		a.add((double) ((array.indexOf(calQ3(array)))-(array.indexOf(calMean(array)))+1));
		a.add((double) ((array.indexOf(Calmax(array)))-(array.indexOf(calQ3(array)))+1));
		return a;
		
	}
	
	public void barChartAttribute(int numAttr) {
		barChart.getData().clear();
		XYChart.Series series = new XYChart.Series();
		int type=0;
		
		HashMap<String, Integer> newHash = new HashMap<String, Integer>();
		ArrayList<Double> array = new ArrayList<Double>();

			 for(int j=0; j<inst.numInstances(); j++) {
				if((inst.attribute(numAttr).isNumeric())&&(!inst.attribute(numAttr).isDate())) {
					type = 0;
					array.add(inst.instance(j).value(numAttr));
				}
				else {
					type = 1;

					}
				}
		
		if(type == 0) {
			//numérique 
			//System.out.println("c'est un double "+array.get(0).getClass().getSimpleName()+"   == "+array.get(0));
			ArrayList<Double> ar = new ArrayList<Double>();
			ar = diviserAttribute(numAttr, array);
			series.getData().add(new XYChart.Data("Min-Q1", ar.get(0)));
	 		series.getData().add(new XYChart.Data("Q1-Médiane", ar.get(1)));
	 		series.getData().add(new XYChart.Data("Médiane-Q3", ar.get(2)));
	 		series.getData().add(new XYChart.Data("Q3-Max", ar.get(3)));
	 		
		}
		else {
			newHash = calculateFreqAttributes(numAttr);
			for (String key:newHash.keySet()){
	            //System.out.println("Key:" + key +" Value:" + newHash.get(key));// Get Key and value 
	             	
	 				series.getData().add(new XYChart.Data(key, newHash.get(key)));

	        }
		}
		barChart.getData().add(series);
	}
	
	
	/***************************************** Partie 2 ****************************************/

	
	public void tableData(double minS, double minConf) throws IOException {
		
		
		
				
				String path = "C:\\Users\\USER\\Documents\\Master2\\DataMining\\dataSet.txt";
				
		
		
		
		 dataItemFreq= FXCollections.observableArrayList();
		 dataRules= FXCollections.observableArrayList();


		 
			
		TableColumn<FreqItemSet, String> colonne1 = new TableColumn<FreqItemSet,String>("Num");
		colonne1.setCellValueFactory(new PropertyValueFactory<FreqItemSet, String>("numFreqItem"));
		TableColumn<FreqItemSet, String> colonne2 = new TableColumn<FreqItemSet, String>("ItemSet");
		colonne2.setCellValueFactory(new PropertyValueFactory<FreqItemSet, String>("itemSet"));
		colonne1.setMinWidth(5d);
		colonne2.setMinWidth(300d);
		tableFreqItems.getColumns().addAll(colonne1, colonne2);
		tableFreqItems.setItems(dataItemFreq);		 
		
		TableColumn<rules, String> colonne3 = new TableColumn<rules,String>("Num");
		colonne3.setCellValueFactory(new PropertyValueFactory<rules, String>("numFreqItem"));
		TableColumn<rules, String> colonne4 = new TableColumn<rules, String>("Rules");
		colonne4.setCellValueFactory(new PropertyValueFactory<rules, String>("itemSet"));
		colonne3.setMinWidth(5d);
		colonne4.setMinWidth(300d);
		tableRulesAssociation.getColumns().addAll(colonne3, colonne4);
		tableRulesAssociation.setItems(dataRules);
		
	
		
		//Apriori apriori = new Apriori();
		//Apriori.representation(minS);
		System.out.println("minuuus == "+ (int)(minS * Integer.parseInt(nbrInst.getText())));
		int min = (int)(minS * Integer.parseInt(nbrInst.getText()));
		
		new Thread(()->{
			try {
				Apriori.representation(this,path, min, minConf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		/*for(int i=0; i<apriori.rules.size(); i++) {
			dataItemFreq.add(new FreqItemSet(Integer.toString(i+1), apriori.rules.get(i)));
			System.out.println(apriori.rules.get(i));
		}*/
		tableFreqItems.setItems(dataItemFreq);

		tableRulesAssociation.setItems(dataRules);
		

		
	}
	
	
	public void Boutton2Action(ActionEvent event) throws Exception {

		
		minSup.textProperty().addListener((observable, oldValue, newValue) -> {
		minS = Double.valueOf(minSup.getText());
		});
		
		//System.out.println(Double.valueOf(minSup.getText()));
		
		confMin.textProperty().addListener((observable, oldValue, newValue) -> {
		minConf = Integer.valueOf(confMin.getText());
		});
		
		System.out.println("min Suppppppp == "+Double.valueOf(minSup.getText()));
		System.out.println("conf miiiiiin == "+confMin.getText());
		tableData(Double.valueOf(minSup.getText()), Double.valueOf(confMin.getText()));
	
	}

	/**************************************Partie KNN**********************************************/
	
	public void tableKnnData(int nbVoisins, float rate) {
		
		String path = "C:\\Users\\USER\\Documents\\Master2\\DataMining\\dataSet.txt";
		

		
		 dataKnn= FXCollections.observableArrayList();
		 
		 

		TableColumn<FreqItemSet, String> colonne3 = new TableColumn<FreqItemSet,String>("Num");
		colonne3.setCellValueFactory(new PropertyValueFactory<FreqItemSet, String>("numFreqItem"));
		TableColumn<FreqItemSet, String> colonne4 = new TableColumn<FreqItemSet, String>("ItemSet");
		colonne4.setCellValueFactory(new PropertyValueFactory<FreqItemSet, String>("itemSet"));
		colonne3.setMinWidth(5d);
		colonne4.setMinWidth(300d);
		tableKnn.getColumns().addAll(colonne3,colonne4);
		tableKnn.setItems(dataKnn);	
		
		float rate2 = Float.parseFloat(textFKNN.getText());
		int nbV = Integer.parseInt(textFVoisins.getText());
		//Double r=0.0;
		double ra = 0.0;
		new Thread(()->{
			try {
				Classify.splitData((float)rate2, Classify.getData(path));
				acc.setText(""+Classify.accuracy(nbV, this));
			//ra = r;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		//tableFreqItems.setItems(dataItemFreq);
		
		tableKnn.setItems(dataKnn);
		
	}
	
	public void Boutton3Action(ActionEvent event) throws Exception {
		
		
		textFKNN.textProperty().addListener((observable, oldValue, newValue) -> {
		rate = Float.valueOf(textFKNN.getText());
		});
		
		//System.out.println(Double.valueOf(minSup.getText()));
		
		textFVoisins.textProperty().addListener((observable, oldValue, newValue) -> {
		//minConf = Integer.valueOf(confMin.getText());
		});
		
		System.out.println("rate == "+Double.valueOf(textFKNN.getText()));
		System.out.println("voisins == "+textFVoisins.getText());
		tableKnnData(Integer.parseInt(textFVoisins.getText()), Float.parseFloat(textFKNN.getText()));
	
	}
	
	
	/*************************************** Partie DB Scan ************************************************/
	
	public void tableDBScanData(int minPts, double eps) {
		String path = "C:\\Users\\USER\\Documents\\Master2\\DataMining\\dataSet.txt";

		
		 dataDBScan = FXCollections.observableArrayList();
		 
		 

		TableColumn<FreqItemSet, String> colonne3 = new TableColumn<FreqItemSet,String>("Num");
		colonne3.setCellValueFactory(new PropertyValueFactory<FreqItemSet, String>("numFreqItem"));
		TableColumn<FreqItemSet, String> colonne4 = new TableColumn<FreqItemSet, String>("Cluster");
		colonne4.setCellValueFactory(new PropertyValueFactory<FreqItemSet, String>("itemSet"));
		colonne3.setMinWidth(5d);
		colonne4.setMinWidth(700d);
		tableDBScan.getColumns().addAll(colonne3,colonne4);
		tableDBScan.setItems(dataDBScan);	
		
		//double eps = Double.parseDouble(textFEps.getText());
		//int minPts = Integer.parseInt(textFMinPts.getText());
		System.out.println("minPts == "+minPts);
		System.out.println("Eps == "+eps);
		//int minPts = 5;
		//double eps = 0.2;
		
		new Thread(()->{
			try {
				Traitement.DBScan.dbScan(minPts, eps, this);
			//ra = r;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		tableDBScan.setItems(dataDBScan);

		
	}
	
	public void Boutton4Action(ActionEvent event) throws Exception {
		
		textFMinPts.textProperty().addListener((observable, oldValue, newValue) -> {
			minP = Integer.valueOf(textFMinPts.getText());
			});
		
		textFEps.textProperty().addListener((observable, oldValue, newValue) -> {
			epsilon = Double.valueOf(textFEps.getText());
			});
			
			
		
		System.out.println("Commencement de DB Scan");
		tableDBScanData(Integer.parseInt(textFMinPts.getText()), Double.parseDouble(textFEps.getText()));
	
	}

	
}


