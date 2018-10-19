package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
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
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
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
	TextField Bq1,Bq3,Bmax,Bmin,Bmean, mode, mediane;
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
	DecimalFormat df = new DecimalFormat("#.##");

	public static LinkedList<LinkedList<String>> linkedList;
	

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

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
	
	
	public void matlabPlots(Instances i) throws MatlabInvocationException, MatlabConnectionException {
		//matlabPlots(i);
		
		 MatlabProxyFactory factory = new MatlabProxyFactory();
		 MatlabProxy proxy = factory.getProxy();
		 int q=0;
		 int cpt=0;
		 while (q<i.numAttributes() )
		 {cpt=0;
		 proxy.eval( "state={};");		 

		 if (i.attribute(q).isNominal() && !(i.attribute(q).isDate())  )
			{
		 		while (cpt<i.size())
		 			{
			 			proxy.eval( "state(end+1)={'"+i.instance(cpt).stringValue(q)+"'};");
			 			cpt++;
			 		}
		 		proxy.eval( "state = categorical(state);");
				proxy.eval( "hist(state)" );
				proxy.eval( "saveas(gcf,'fig"+q+".png');" );
			 }

		 if (i.attribute(q).isNumeric() && !(i.attribute(q).isDate())  )
			{
		 		while (cpt<i.size())
		 			{
			 			proxy.eval( "state(end+1)={'"+i.instance(cpt).value(q)+"'};");
			 			cpt++;
			 		}
		 		proxy.eval( "state = categorical(state);");
				proxy.eval( "h=pie(state)" );
		 		proxy.eval( "set(findobj(h,'type','text'),'fontsize',6);");
		 		proxy.eval( " H=findobj(gca,'Type','text');\r\n" + 
		 				" set(H,'Rotation',60); % tilt");
				proxy.eval( "saveas(gcf,'fig"+q+".png');" );
			 }
		 
		 
			q++;
		 }
			 	
		 //boxplot
		 cpt=0;q=0;
			proxy.eval( "f= zeros("+i.size()+","+i.numAttributes()+")");
				while (q<i.numAttributes() )
				 {cpt=0;
				 
				 if (i.attribute(q).isNumeric() && !(i.attribute(q).isDate())  )
					{
				 	while (cpt<i.size())
				 		{
						proxy.eval( "f("+(cpt+1)+","+(q+1)+")="+i.instance(cpt).value(q)+";\r\n");
						cpt++;
				 		}
					}
				 	q++;
				 }
				proxy.eval( "boxplot(f);");
				proxy.eval( "saveas(gcf,'figure.png');" );
				proxy.eval( "quit;" );
	}
			
	
	
	
	public void choice(ActionEvent event)throws Exception {
		String s=AttChooser.getValue();
		int k=atts.indexOf(s);
			if(inst.attribute(k).isNumeric()){
				double [] vecteur;
				vecteur = new double [inst.size()];
				for(int j=0;j<inst.size();j++){
					vecteur[j]=inst.instance(j).value(k);
					//System.out.println("vecteuruuuus");
					//System.out.println(vecteur[j]);
				}	
				Bq1.clear();
				Bq3.clear();
				Bmean.clear();
				Bmin.clear();
				Bmax.clear();
				mode.clear();
				vecteur = sort(vecteur,inst.size()); 
				Bq1.setText(String.valueOf(calQ1(vecteur)));
				Bq3.setText(String.valueOf(calQ3(vecteur)));
				Bmean.setText(Double.toString(inst.meanOrMode(inst.attribute(k))));
				Bmin.setText(Double.toString(inst.attributeStats(k).numericStats.min));
				Bmax.setText(Double.toString(inst.attributeStats(k).numericStats.max));
				mode.setText(Double.toString(inst.meanOrMode(inst.attribute(k))));
				

			}
			else {
				if((inst.attribute(k).isNominal())&& (!inst.attribute(k).isDate())) {
					Bq1.clear();
					Bq3.clear();
					Bmean.clear();
					Bmin.clear();
					Bmax.clear();
					mode.clear();
					
					String [] vecteur;
					vecteur = new String[inst.size()];
					for(int j=0;j<inst.size();j++){
						vecteur[j]=(inst.instance(j).stringValue(k));
					}
					String name = inst.attribute(k).value( (int) (inst.meanOrMode(k)));
					
					mode.setText(name);
					
				}
			}
			new Thread(()->{
				Platform.runLater(() -> barChartAttribute(k));

			}).start();
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
		mode.clear();


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
		 final BoxPlot demo = new BoxPlot("Boites à moustaches", textF.getText());
	     demo.pack();
	     RefineryUtilities.centerFrameOnScreen(demo);
	     demo.setVisible(true);

	}
	
	
	public Instances missingValue(Instances inst) throws Exception {
		for(int i=0; i<inst.numInstances(); i++) {
			 for(int j=0; j<inst.numAttributes(); j++) {
				 if(inst.attribute(j).isNumeric()) {
				if( Double.toString(inst.instance(i).value(j)).contentEquals("NaN") )
				{
					inst.instance(i).setValue(j, inst.meanOrMode(inst.attribute(j)));
				}
					}
				 if(inst.attribute(j).isNominal())
					 {
					 if(inst.instance(i).stringValue(j).contentEquals("?")) {
						 inst.instance(i).setValue(j, inst.meanOrMode(inst.attribute(j)));
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
		 inst = missingValue(inst);
		 inst = normalization(inst);

//		 System.out.println(inst.instance(1).value(1));
		 
		 linkedList = new LinkedList<LinkedList<String>>();
		 for(int i=0; i<inst.numAttributes(); i++) {
			 linkedList.add(new LinkedList<String>());
			 for(int j=0; j<inst.numInstances(); j++) {
				if((inst.attribute(i).isNumeric())&&(!inst.attribute(i).isDate())) {
					String d = df.format(inst.instance(j).value(i));
					System.out.println("DMMMMMMMMM");
					System.out.println(d);
					linkedList.get(i).add(d);
				}
				else {
					if((inst.attribute(i).isNominal())&&(!inst.attribute(i).isDate()))
						linkedList.get(i).add(inst.instance(j).stringValue(i));

				}
			 }
			System.out.println(Arrays.asList(linkedList.get(i)));
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
		 

		 while(cpt<inst.size()) {
			 
			dataInstance.add(new instances(Integer.toString(cpt),inst.get(cpt).toString()));
			//System.out.println( inst.get(cpt).toString());
			cpt++;
		}
		
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
		
		//matlabPlots(inst);

			
		 
	}
	
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
	
	
	public void barChartAttribute(int numAttr) {
		barChart.getData().clear();
		XYChart.Series series = new XYChart.Series();
		
		HashMap<String, Integer> newHash = new HashMap<String, Integer>();
		newHash = calculateFreqAttributes(numAttr);
		int x=0;
		
		 for (String key:newHash.keySet()){
            System.out.println("Key:" + key +" Value:" + newHash.get(key));// Get Key and value 
             	
 				series.getData().add(new XYChart.Data(key, newHash.get(key)));

        }

		barChart.getData().add(series);

	}

}
