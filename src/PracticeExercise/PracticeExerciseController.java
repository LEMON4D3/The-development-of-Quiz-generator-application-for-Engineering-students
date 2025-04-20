package PracticeExercise;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Util.controller;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class PracticeExerciseController implements Initializable{
	
	String tableName;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
	}
	
	public void backFn(ActionEvent e) throws IOException {
		
		//controller control = new controller();
		//control.switchScene(e, "../problemSet/ProblemSet.fxml");
		
		controller.ct.switchScene(e, "../ProblemSet/ProblemSet.fxml");
		
	}
	
	public void init(String title) {
		
		tableName = title;
		
	}
}
