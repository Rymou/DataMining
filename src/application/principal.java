package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class principal implements Initializable{
	
	@FXML
	Button partie1;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public void buttonPart1(ActionEvent event) throws IOException{
		//System.out.println("gaguaaaaa");
		
              // launch(controller.class);
            	//faire appel a lautre interface
				System.out.println("noooo");
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("uiWithTabs.fxml"));
				Parent root1 = (Parent) fxmlLoader.load();
				Scene scene = new Scene(root1);
				Stage stage = new Stage(StageStyle.DECORATED);
				stage.setScene(scene);  
				stage.show();
	}
	

}
