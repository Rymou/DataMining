package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class myMain extends Application{


	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		Pane anc = (Pane) FXMLLoader.load(myMain.class.getResource("principale.fxml"));
		Scene scene = new Scene(anc);
       primaryStage.setTitle("DataMining");

		primaryStage.setScene(scene);
		primaryStage.show();		
	}
	
	public static void main(String[] args) {
       launch(args);

	}

}
