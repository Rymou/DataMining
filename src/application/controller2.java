package application;

import java.io.IOException;

import Traitement.Apriori;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class controller2 {
	
	@FXML
	TableView<FreqItemSet> tableFreqItems;
	public ObservableList<FreqItemSet> dataItemFreq;
	@FXML
	TableView<rules> tableRulesAssociation;
	public ObservableList<rules> dataRules;
	@FXML
	Button start;
	@FXML
	TextField minSup;
	int minS = 0;
	@FXML
	TextField confMin;
	double minConf = 0.0;
	
	public void tableData(int minS, double minConf) throws IOException {
		
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
		
		new Thread(()->{
			try {
				Apriori.representation(this, minS, minConf);
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
	
	
	public void Boutton1Action(ActionEvent event) throws Exception {
		
		minSup.textProperty().addListener((observable, oldValue, newValue) -> {
		minS = Integer.valueOf(minSup.getText());
		});
		
		confMin.textProperty().addListener((observable, oldValue, newValue) -> {
		minConf = Integer.valueOf(confMin.getText());
		});
		
		System.out.println("min Suppppppp == "+minSup.getText());
		System.out.println("conf miiiiiin == "+confMin.getText());
		tableData(Integer.valueOf(minSup.getText()), Double.valueOf(confMin.getText()));
	
	}

}
