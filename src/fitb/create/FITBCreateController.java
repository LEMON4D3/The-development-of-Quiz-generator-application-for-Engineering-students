package fitb.create;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import quizCard.quizCardController;
import util.QuizClass;
import util.QuizClass.quizContainer;
import util.controller;
import util.quizCreateControllerExtend;
import util.user;

public class FITBCreateController extends quizCreateControllerExtend implements Initializable {

	@FXML
	Button backBtn, saveBtn;

	@FXML
	TextField answerTF, hintTF;
	
	@FXML
	TextArea questionTA;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		initComboBox(0);

	}

	@Override
	public void initComponents() {

		answerTF.setText(prepareQuiz.quizAnswer);
		questionTA.setText(prepareQuiz.quizQuestion);
		hintTF.setText(prepareQuiz.hint);
		categoryCombo.setValue(prepareQuiz.quizCategory);
		pointCombo.setValue(prepareQuiz.point);

	}


	public void setQuizCardController(classController controller, int containerIndex, QuizClass.quizContainer container) {
		
		answerTF.setText(container.quizAnswer);
		questionTA.setText(container.quizQuestion);
		hintTF.setText(container.hint);
		categoryCombo.setValue(container.quizCategory);
		pointCombo.setValue(container.point);

		this.prepareQuiz.id = container.id;
		this.controller = controller;
		this.containerIndex = containerIndex; 
		user.userQuizOption = user.quizOption.Edit;

	}
	
	public void saveBtnFn(ActionEvent event) throws IOException, SQLException {
		
		String errorText = QuizClass.checkRequirementTF(questionTA, answerTF);
		if(errorText != null) {
			errorPopUp(event, (errorText.equals(questionTA.getId()) ? textFieldErr.questionEmpty : textFieldErr.answerEmpty));
			return;
		}
		
		prepareQuiz.quizAnswer = answerTF.getText();
		prepareQuiz.quizQuestion = questionTA.getText();
		prepareQuiz.hint = hintTF.getText();
		prepareQuiz.quizCategory = categoryCombo.getValue().toString();
		prepareQuiz.point = pointCombo.getValue().toString();
		
		finalSave(event);
		
	}

	@Override
	public void setPrepareQuiz() {

		prepareQuiz.quizAnswer = answerTF.getText();
		prepareQuiz.quizQuestion = questionTA.getText();
		prepareQuiz.hint = hintTF.getText();
		prepareQuiz.quizCategory = categoryCombo.getValue().toString();
		prepareQuiz.point = pointCombo.getValue().toString();

	}

}
