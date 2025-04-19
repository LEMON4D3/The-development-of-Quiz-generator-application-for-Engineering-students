package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Util.Preferences;
import Util.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class HomepageController implements Initializable{
	
	@FXML
	AnchorPane main;
	
	@FXML
	FlowPane header;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		main.setStyle("-fx-background-color: #" + Preferences.mainBackground);
		
		header.setStyle(
			"-fx-background-color: #" + Preferences.headerBackground + ";" +
			"-fx-border-color: #" + Preferences.borderColor + ";" + 
			"-fx-border-width: 3"
		);
		
	}

	/*
	 * Button Functions section
	 */
	
	public void homeFn(ActionEvent e) {
		
		//Homepage Function
		
	}
	
	public void quizFn(ActionEvent e) {
		
		// Quizzes Function
		
	}
	
	public void settingFn(ActionEvent e) throws IOException {
		
		
		controller.ct.switchScene(e, "../Setting/Setting.fxml");
		
	}
	
	public void practiceExerciseFn(MouseEvent e) {
		
		// Practice Exercise Function
		
	}
	
	public void createQuizFn(ActionEvent e) throws IOException{
		try {
	        Parent createQuizRoot = FXMLLoader.load(getClass().getResource("CreateQuiz.fxml"));
	        
	        // Get the current stage from the event
	        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
	        
	        // Replace the scene content with the new root
	        stage.getScene().setRoot(createQuizRoot);

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}
}
