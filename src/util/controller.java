package util;

import java.io.IOException;
import java.util.EventObject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class controller {
	
	public enum categoryList{
		
		fitb,
		match,
		multipleChoice,
		tof
		
	}
	
	public <T> void categoryChangeScene(categoryList category, T event) {
		
		try {
		
			String categoryLocation = "";
			
			switch(category) {
			case fitb:
				categoryLocation = "/fitb/create/FITB.fxml";
				break;
			case multipleChoice:
				categoryLocation = "/multipleChoice/create/MultipleChoice.fxml";
				break;
			case tof:
				categoryLocation = "/tof/create/Tof.fxml";
				break;
			default:
				throw new Error("We haven't implemented this yet lil bro");
				
			}
			
			changeScene(event, categoryLocation);
		} 
		catch(Error categoryError) { System.out.println(categoryError.getMessage()); }
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public Stage getStage(Node node) { return (Stage) node.getScene().getWindow(); }
	
	public <T> Stage getStage (T e) { return (Stage)((Node)((EventObject)e).getSource()).getScene().getWindow();}
	
	public <T> void changeScene(T e, String url) {

		try {

			Parent root = FXMLLoader.load(getClass().getResource(url));
			Stage stage = (Stage) ((Node) ((EventObject) e).getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();

		} catch (Exception exception) { exception.printStackTrace(); }
		
	}
	
}
