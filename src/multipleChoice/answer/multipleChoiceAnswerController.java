package multipleChoice.answer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.quizCreateControllerExtend;

import java.net.URL;
import java.util.*;

public class multipleChoiceAnswerController extends quizCreateControllerExtend implements Initializable {

	@FXML
	Label questionT, questionNumberT;

	@FXML
	Button backBtn, nextBtn;

	@FXML
	HBox choiceContainer;

	@FXML
	ScrollPane scrollContainer;

	@FXML
    private ComboBox<String> categoryCombo, pointCombo, timeCombo;

	List<questionContainer> questionContainerList = new ArrayList<>();


	// initialize and button function section


	public void backBtnFn(ActionEvent e) {

		addUserAnswer();
		backListFn(e);

	}

	private void addUserAnswer() {

		List<String> answerList = new ArrayList<>();
		for(questionContainer container : questionContainerList) {

			if(container.isAnswer) answerList.add(container.optionT.getText());

		}

		try {
			userAnswerList.set(quizIndex, answerList);
		} catch (Exception e) {
			userAnswerList.add(answerList);
		}

		System.out.println("User Answer: " + userAnswerList);

	}

	public void nextBtnFn(ActionEvent e) {

		addUserAnswer();
		nextListFn(e);

	}

	// backend function section
	// initialize quiz question
	@Override
    public void initQuiz(Map<String, Object> quizInfo) {

		List<String> optionList = Arrays.asList(((String) quizInfo.get("quiz option")).split(","));
		questionT.setText((String) quizInfo.get("quiz question"));

		for(String text: optionList) {

			multipleChoiceAnswerController.questionContainer container = new multipleChoiceAnswerController.questionContainer(text);
			choiceContainer.getChildren().add(container);
			questionContainerList.add(container);

		}

	}

	public void initialize(URL url, ResourceBundle resourceBundle) {

		questionNumberT.setText("Question " + (quizListScene.size() + 1));

	}


	// Multiple choice class container
	private class questionContainer extends VBox {

		Label optionT = new Label();
		boolean isAnswer = false;
		VBox container;

		questionContainer(String option) {

			container = this;

			container.getStyleClass().add("questionContainer");
			this.setOnMouseClicked(event -> {

				isAnswer = !isAnswer;

				if (isAnswer) container.getStyleClass().add("answerBox-activated");
				else container.getStyleClass().remove("answerBox-activated");

			});

			this.setPrefWidth(250);
			this.setMinHeight(225);
			this.setPadding(new Insets(15, 25, 15, 25));
			this.setSpacing(15);
			this.setAlignment(Pos.CENTER);

			optionT.setText(option);
			optionT.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
			this.getChildren().add(optionT);

		}

	}



}
