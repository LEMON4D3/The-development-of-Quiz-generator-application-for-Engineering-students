package tof.create;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quizCard.quizCardController;
import util.QuizClass;
import util.controller;
import util.quizCreateControllerExtend;
import util.QuizClass.quizContainer;

public class tofCreateController extends quizCreateControllerExtend implements Initializable{


	@FXML
    private ComboBox<String> categoryCombo;

    @FXML
    private ComboBox<String> pointCombo;
    
    @FXML
    private ComboBox<String> timeCombo;
	
    @FXML
    private Button backBtn;
	 
    @FXML
    private TextArea questionTA;

    @FXML
    private Button saveBtn;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		initComboBox(2);
		trueBtn.getStyleClass().add("choiceBox-activate");

		
	}

	@Override
	public void setPrepareQuiz() {

		prepareQuiz.quizCategory = categoryCombo.getValue().toString();
		prepareQuiz.point = pointCombo.getValue().toString();
		prepareQuiz.quizQuestion = questionTA.getText();
		prepareQuiz.quizAnswer = quizAnswer;

	}

    @FXML
    void saveBtnFn(ActionEvent event) {

		setPrepareQuiz();
    	finalSave(event);
    	
    }
    
    
    // trueBtnFn & falseBtnFn
    
    @FXML
    Button trueBtn, falseBtn;
    String quizAnswer = "True";
    
    
    @FXML
    void trueBtnFn(ActionEvent event) {
    	
    	falseBtn.getStyleClass().removeAll("choiceBox-activate");
    	trueBtn.getStyleClass().add("choiceBox-activate");
    	quizAnswer = "True";
    	
    }
    
    @FXML
    void falseBtnFn(ActionEvent event) {
    	
    	trueBtn.getStyleClass().removeAll("choiceBox-activate");
    	falseBtn.getStyleClass().add("choiceBox-activate");
    	quizAnswer = "False";
    	
    }

	@Override
	public void initComponents() {

		questionTA.setText(prepareQuiz.quizQuestion);
		categoryCombo.setValue(prepareQuiz.quizCategory);
		pointCombo.setValue(prepareQuiz.point);

	}

    public void setQuizCardController(classController controller, int containerIndex, QuizClass.quizContainer container) {
    	
    	if(container.quizAnswer.equals("False")) {
    		
    		trueBtn.getStyleClass().removeAll("choiceBox-activate");
        	falseBtn.getStyleClass().add("choiceBox-activate");
        	quizAnswer = "False";
    		
    		
    	}
    	
		questionTA.setText(container.quizQuestion);
		categoryCombo.setValue(container.quizCategory);
		pointCombo.setValue(container.point);

		this.prepareQuiz.id = container.id;
		this.controller = controller;
		this.containerIndex = containerIndex; 
		
	}
    
}
