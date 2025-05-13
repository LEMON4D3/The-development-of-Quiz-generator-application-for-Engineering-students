package util;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import quizCard.quizCardController;

public class QuizClass {

	// public quizContainerVariable(String category, String point, String hint, String time, String question, String answer, List<String> questionOptions) {



	public static class quizContainer{

		public int id;
		
		public String quizCategory, point, hint, time, quizQuestion, quizAnswer, quizTitle;
		public String quizOptions;
		
	}
	
	public <T> void categoryChangeBtnFn(T event, ComboBox<String> categoryCombo, ObservableList<String> categoryList) {
		
		System.out.println("changing it lil bro");
		
		String categoryComboString = categoryCombo.getValue().toString();
		int categoryLocationNumber = 0;
		
		for(int i = 0; i < categoryList.size(); i++)
			if(categoryList.get(i).equals(categoryComboString)) {
				categoryLocationNumber = i;
				break;
			}
		
		new controller().categoryChangeScene(controller.categoryList.values()[categoryLocationNumber], event);
		
	}
	
	
	public static String checkRequirementTF(TextArea textArea, TextField... textfield) {
		
		String output = null;
		
		if(textArea.getText().isEmpty()) 
			return textArea.getId();
		
		for(TextField i: textfield) {
			
			if(i.getText().isEmpty()) {
				output = i.getId();
				break;
			}
			
		}
		
		return output;
	}
	
	public <T> void saveChangeScene(T event, quizContainer prepareQuiz) {
		
		try {
			
			Stage mainStage = new controller().getStage(event);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizCard/QuizCard.fxml"));
			Parent root = loader.load();
			
			quizCardController controller = loader.getController();
			controller.addContainerList(prepareQuiz);
			
			Scene newScene = new Scene(root);
			
			mainStage.setScene(newScene);
			mainStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
