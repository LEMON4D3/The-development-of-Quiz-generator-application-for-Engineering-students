package multipleChoice.create;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import quizCard.quizCardController;
import util.QuizClass;
import util.quizCreateControllerExtend;
import util.QuizClass.quizContainer;

public class multipleChoiceCreateController extends quizCreateControllerExtend implements Initializable{

	@FXML
	TextArea questionTA;

	@FXML
	Button addBtn;
	
	@FXML
	HBox choiceContainer;

	@FXML
	ScrollPane scrollContainer;

	@FXML
    private ComboBox<String> categoryCombo, pointCombo, timeCombo;

	List<questionContainer> questionContainerList = new ArrayList<>();

	// questionContainerList function section

	private void deleteContainer(int index) {

		questionContainerList.remove(index);
		for(int i = 0; i < questionContainerList.size(); i++) {

			questionContainerList.get(i).containerIndex = i;

		}
		refreshQuestionList();

	}
	private void setContainer(int index, questionContainer container) {

		questionContainerList.set(index, container);
		refreshQuestionList();

	}

	private void addContainer(questionContainer container) {

		questionContainerList.add(container);
		refreshQuestionList();

	}

	// initialize and button function section

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		scrollContainer.addEventFilter(ScrollEvent.SCROLL, event -> {
			if (event.getDeltaY() != 0) {
				double deltaX = event.getDeltaY() * 2;
				scrollContainer.setHvalue(scrollContainer.getHvalue() - deltaX / scrollContainer.getWidth());
				event.consume();
			}
		});

		initComboBox(2);
		
		refreshQuestionList();
		
	}

	public void saveBtnFn(ActionEvent event) {

		List<String> questionAnswer = new ArrayList<>();
		List<String> questionOptions = new ArrayList<>();

		for(questionContainer container : questionContainerList) {

			if(container.isAnswerBox.isSelected()) questionAnswer.add(container.textTA.getText());

			questionOptions.add(container.textTA.getText());

		}

		System.out.println("Answer: " + questionAnswer);
		System.out.println("Options: " + questionOptions);

		String answer = String.join(",", questionAnswer);
		String options = String.join(",", questionOptions);

		prepareQuiz.quizAnswer = answer;
		prepareQuiz.quizOptions = options;
		prepareQuiz.quizQuestion = questionTA.getText();
		prepareQuiz.quizCategory = categoryCombo.getValue();
		prepareQuiz.point = pointCombo.getValue();
		prepareQuiz.time = timeCombo.getValue();

		finalSave(event);

	}

	public void backBtnFn(ActionEvent e) {



	}

	public void addBtnFn(ActionEvent e) {

		addContainer(new questionContainer(questionContainerList.size()));

	}

	// backend function section

	public void setQuizCardController(classController controller, int containerIndex, QuizClass.quizContainer container) {
		
		categoryCombo.setValue(container.quizCategory);
		timeCombo.setValue(container.time);
		pointCombo.setValue(container.point);
		questionTA.setText(container.quizQuestion);

		this.prepareQuiz.id = container.id;
		this.controller = controller;
		this.containerIndex = containerIndex; 
		
	}

	private void refreshQuestionList() {
		
		choiceContainer.getChildren().clear();
		for(questionContainer container: questionContainerList) choiceContainer.getChildren().add(container);

		choiceContainer.getChildren().add(addBtn);

	}
	
	public QuizClass.quizContainer getQuizComponents() {
		
		QuizClass.quizContainer prepareQuiz = new quizContainer();
		
		prepareQuiz.quizCategory = categoryCombo.getValue().toString();
		prepareQuiz.time = timeCombo.getValue().toString();
		prepareQuiz.point = pointCombo.getValue().toString();
		
		return prepareQuiz;
	
	}
	
	private class questionContainer extends VBox {

		public int containerIndex;
		questionContainer container;

		TextArea textTA = new TextArea();
		CheckBox isAnswerBox = new CheckBox();

		questionContainer(int index) {

			containerIndex = index;
			container = this;

			this.setStyle("-fx-background-color: #00799A; -fx-border-color: #FFEA00; -fx-border-width: 5; -fx-background-radius: 20; -fx-border-radius: 20;");
			
			this.setMaxWidth(250);
			this.setMinHeight(250);
			this.setPadding(new Insets(15, 25, 15, 25));
			this.setSpacing(15);
			
			this.getChildren().add(new topPane());
			this.getChildren().add(new textAreaContainer());
			
			
		}
		
		class topPane extends GridPane{
			
			topPane() {
				
				
				Button deleteBtn = new Button();
				deleteBtn.setOnAction(event -> deleteContainer(containerIndex));
				GridPane.setHalignment(deleteBtn, HPos.LEFT);
				this.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(50); }});
				this.add(deleteBtn, 0, 0);
				

				GridPane.setHalignment(isAnswerBox, HPos.RIGHT);
				this.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(50); }});
				this.add(isAnswerBox, 1, 0);
				
				
			}
			
		}
		
		class textAreaContainer extends HBox {
			
			textAreaContainer() {
				
				this.setAlignment(Pos.CENTER);
				this.setFillHeight(false);
				

				textTA.getStyleClass().clear();
				textTA.setPromptText("TYPE ANSWER OPTION HERE");
				textTA.setPrefHeight(TextArea.USE_COMPUTED_SIZE);
				textTA.getStyleClass().add("centered-textArea");
				textTA.setWrapText(true);
				
				this.getChildren().add(textTA);
				
			}
			
		}
		
	}
	
}
