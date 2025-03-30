package Util;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class controller {
	
	private Stage stage;
	private Scene scene;
	
	public Parent root;
	
	public static controller ct;
	
	public void switchScene(ActionEvent e, String url) throws IOException  {
		
		
		root = FXMLLoader.load(getClass().getResource(url));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
}
