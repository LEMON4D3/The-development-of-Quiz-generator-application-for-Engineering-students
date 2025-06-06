package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import fitb.answer.FITBAnswerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import multipleChoice.answer.multipleChoiceAnswerController;
import tof.answer.TofAnswerController;
import util.QuizClass.quizContainer;

public class quizCreateControllerExtend {
	
	public static quizContainer prepareQuiz = new quizContainer();

	@FXML
	protected ComboBox categoryCombo, pointCombo;


	// meant to be Override
	public void initComponents() { }

	// meant to be Override
	public void setPrepareQuiz() {}

	protected void initComboBox(int categoryNumber) {

		ObservableList<String> categoryList = FXCollections.observableArrayList("Fill in the Blank", "Multiple Choice", "True or False");

		categoryCombo.setOnAction(f -> {


			setPrepareQuiz();

			categoryChangeBtnFn(f, categoryCombo, categoryList);
			prepareQuiz.quizCategory = categoryCombo.getValue().toString();
			initComponents();

		});
		
		categoryCombo.setItems(categoryList);
		categoryCombo.setValue(categoryList.get(categoryNumber));
		
		ObservableList<String> pointList = FXCollections.observableArrayList();
		for(int i = 1; i <= 100; i++)
			pointList.add(i + " points");
		
		pointCombo.setValue(pointList.get(0));
		pointCombo.setItems(pointList);
		
	}

	protected <T> void categoryChangeBtnFn(T event, ComboBox<String> categoryCombo, ObservableList<String> categoryList) {

		System.out.println("changing it lil bro");

		String categoryComboString = categoryCombo.getValue().toString();
		int categoryLocationNumber = 0;

		for(int i = 0; i < categoryList.size(); i++)
			if(categoryList.get(i).equals(categoryComboString)) {
				categoryLocationNumber = i;
				break;
			}

		new controller().categoryChangeScene( prepareQuiz, util.controller.categoryList.values()[categoryLocationNumber], event);

	}



	public void setTitle(String title) { 
		
		prepareQuiz.quizTitle = title;
		
	}
	
	protected <T> void stageClose(T event) { 
		
		Stage mainStage = new controller().getStage(event);
		mainStage.close();
		
	}
	
	public void backBtnFn(ActionEvent e) throws SQLException, IOException {
		
		if(user.userQuizOption == user.quizOption.Edit) {

			Stage mainStage = new controller().getStage(e);
			mainStage.close();

		}
		else if (user.userQuizOption == user.quizOption.New) new controller().changeScene(e, "/quiz/Quiz.fxml");
	
	}


	// errorPopUp
	protected enum textFieldErr{
		
		questionEmpty,
		answerEmpty
		
	}
	
	protected void errorPopUp(ActionEvent e, textFieldErr error) {
		
		Stage mainStage = (Stage)((Node)((EventObject)e).getSource()).getScene().getWindow();
		
		Stage miniStage = new Stage();
		miniStage.initModality(Modality.APPLICATION_MODAL);
		miniStage.initOwner(mainStage); 
		
		VBox pane = new VBox();
		
		pane.setPrefHeight(250);
		pane.setPrefWidth(250);
		pane.setAlignment(Pos.CENTER);
		pane.setSpacing(25);
		
		String errorString = (error == textFieldErr.questionEmpty) ? "The Question TextArea is empty" : "The Answer Area is empty";
		Label errorT = new Label(errorString);
		
		Button okayBtn = new Button("okay");
		okayBtn.setOnAction(f -> {
			
			miniStage.close();
			
		});
		
		pane.getChildren().addAll(errorT, okayBtn);
		
		Scene scene = new Scene(pane);
		
		miniStage.setScene(scene);
		miniStage.showAndWait();
		
	}
	
	// setQuizCardController
	
	protected void finalSave(ActionEvent event) {
		
		try {

			if(!prepareQuiz.quizCategory.equals("Multiple Choice")) {
				prepareQuiz.quizOptions = null;
			}

			System.out.println("Prepare Quiz Category: " + prepareQuiz.quizCategory + "\nPrepare Quiz Point: " + prepareQuiz.point + "\nPrepare Quiz Question: " + prepareQuiz.quizQuestion + "\nPrepare Quiz Answer: " + prepareQuiz.quizAnswer + "\nPrepare Quiz Options: " + prepareQuiz.quizOptions);

			if(controller != null && user.userQuizOption != user.quizOption.New && user.isTeacher) {
				
				if(user.userQuizOption == user.quizOption.Edit) controller.setContainerList(prepareQuiz, containerIndex);
				else if(user.userQuizOption == user.quizOption.Add) controller.addContainerList(prepareQuiz);



				stageClose(event);
				user.userQuizOption = null;
				return;
				
			} else if(controller != null && user.userQuizOption != user.quizOption.New && !user.isTeacher) {

				//System.out.println("This is a practice quiz");
				//FXMLLoader loader = new FXMLLoader(getClass().getResource("/practiceQuiz/practiceQuiz.fxml"));

				Stage stage = new controller().getStage(event);
				stage.close();

				if(user.userQuizOption == user.quizOption.Add) controller.addContainerList(prepareQuiz);
				else if(user.userQuizOption == user.quizOption.Edit) controller.setContainerList(prepareQuiz, containerIndex);

				user.userQuizOption = null;
				return;

			}

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizCard/QuizCard.fxml"));
			Parent root = loader.load();
			
			classController controller = loader.getController();
			controller.addContainerList(prepareQuiz);

			Stage stage = new controller().getStage(event);
			stage.setScene(new Scene(root));
			stage.show();
			
		} catch (Exception exception) { exception.printStackTrace(); }
		
		//new QuizClass().saveChangeScene(event, prepareQuiz);
		
	}

	public static abstract class classController {

		public List<quizContainer> cardContainerList = new ArrayList<>();

		public void setContainerList(quizContainer setContainer, int index) {

			cardContainerList.set(index, setContainer);
			refreshList();

		}

		public void addContainerList(quizContainer addContainer) {

			cardContainerList.add(addContainer);
			refreshList();

		}

		public void deleteContainerList(int containerIndex) { cardContainerList.remove(containerIndex); refreshList(); }

		public void refreshList(){}
	}

	protected static classController controller;
	public static int containerIndex;
	
	public void setQuizCardController(classController controller, int containerIndex ) {

		prepareQuiz = new quizContainer();

		this.controller = controller;
		this.containerIndex = containerIndex; 
		
	}

	// meant to be override
	public void initQuiz(Map<String, Object> quizInfo) { }

	// quiz next and back button function section

	public static List<Map<String, Object>> quizList = new ArrayList<>();
	public static int quizIndex = 0;
	public static List<Scene> quizListScene = new ArrayList<>();

	public static List<List<String>> userAnswerList = new ArrayList<>();

	public void prepareQuiz(List<Map<String, Object>> quizList) {

		quizListScene.clear();
		this.quizList = quizList;
		for(Map<String, Object> quiz : quizList) {

			quizListScene.add(getScene(quiz));

		}

		System.out.println("Quiz List Parent: " + quizListScene.size());

	}

	public void nextListFn(ActionEvent event) {

		quizIndex++;
		System.out.println("Next Quiz List: " + quizListScene.size() + "\n Quiz Index: " + quizIndex);

		if (quizIndex >= quizListScene.size()) {

			System.out.println("This is the last question");
			quizIndex--;
			finalizeAnswerPopUp(event);

		} else {

			Stage mainStage = new controller().getStage(event);
			mainStage.setScene(quizListScene.get(quizIndex));
			mainStage.show();

		}

	}

	public void backListFn(ActionEvent event) {

		System.out.println("Back Quiz List: " + quizListScene.size());

		quizIndex--;
		if(quizIndex < 0) {

			System.out.println("This is the first question");
			quizIndex++;
			return;

		} else {

			System.out.println(quizListScene.get(quizIndex).getRoot());
			Stage mainStage = new controller().getStage(event);
			mainStage.setScene(quizListScene.get(quizIndex));
			mainStage.show();

		}
	}

	public void finalizeAnswerPopUp(ActionEvent event) {

		Stage mainStage = new controller().getStage(event);

		Stage miniStage = new Stage();
		miniStage.initModality(Modality.APPLICATION_MODAL);
		miniStage.initOwner(mainStage);

		VBox pane = new VBox();
		pane.setPrefHeight(250);
		pane.setPrefWidth(250);
		pane.setAlignment(Pos.CENTER);
		pane.setSpacing(25);

		Label announcementT = new Label("Are you sure you want to finalize your answer?");


		HBox bottomPane = new HBox();
		bottomPane.setAlignment(Pos.CENTER);
		bottomPane.setSpacing(25);

		Button okayBtn = new Button("Okay");
		okayBtn.setOnAction(f -> {

			try {

				getUserTotalPoints();
				miniStage.close();

				Parent root = FXMLLoader.load(getClass().getResource("/quizReport/student/QuizReport.fxml"));


				mainStage.setScene(new Scene(root));
				mainStage.show();

			} catch (IOException exception) { exception.printStackTrace(); }
		});

		Button cancelBtn = new Button("Cancel");
		cancelBtn.setOnAction(f -> {
			miniStage.close();
		});

		bottomPane.getChildren().addAll(okayBtn, cancelBtn);
		pane.getChildren().addAll(announcementT, bottomPane);

		miniStage.setScene(new Scene(pane));
		miniStage.showAndWait();

	}

	public void getUserTotalPoints() {

		int correctAnswer = 0;
		int totalPoints = 0;

		List<Integer> pointList = new ArrayList<>();

		for(int i = 0; i < quizList.size(); i++) {

			Map<String , Object> quiz = quizList.get(i);
			String quizAnswer = (String) quiz.get("quiz answer");

			List<String> UserAnswer = userAnswerList.get(i);
			String userAnswer = String.join(",", UserAnswer);

			System.out.println("\nComparing\nQuiz Answer: " + quizAnswer + "\nUser Answer: " + userAnswer + "\n\n");

			int quizPoint = (Integer) quiz.get("point");

			if(quizAnswer.equals(userAnswer)) {

				correctAnswer += quizPoint;
				pointList.add(quizPoint);

			} else pointList.add(0);

			totalPoints += quizPoint;

		}

		System.out.println("The user: " + user.currentUser + " got " + correctAnswer + " out of " + totalPoints + " points");

		try {

			Connection classConnection = null;
			if(user.currentClass != null) classConnection = Util.getClassConnectionDB();
			else classConnection = Util.getStudentQuizConnectionDB();

			String checkRequiredTable = "create table if not exists `" + user.currentQuiz + "List`(id integer primary key autoincrement, `username` varchar(50), `quiz title` varchar(50), `quiz answer` varchar(500), `quiz point` varchar(500), `total point` int, `quiz question` varchar(500))";
			Statement checkRequiredTableStatement = classConnection.createStatement();
			checkRequiredTableStatement.executeUpdate(checkRequiredTable);
			checkRequiredTableStatement.close();

			System.out.println("Successfully created " + user.currentQuiz + "List table");

			String prepareUserInputString = "insert into `" + user.currentQuiz + "List`(`username`, `quiz title`, `quiz answer`, `quiz point`, `total point`, `quiz question`) values(?, ?, ?, ?, ?, ?)";
			PreparedStatement userInputPrepare = classConnection.prepareStatement(prepareUserInputString);

			String userAnswerFinal = userAnswerList.stream()
					.map(innerList -> innerList.toString())
					.reduce((a, b) -> a + ", " + b)
					.orElse("");

			List<Map<String, Object>> questionParentList = new ArrayList<>();
			if (user.currentClass != null) questionParentList = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz, false);
			else questionParentList = Util.getStudentQuizListDB(false);

			List<String> questionList = new ArrayList<>();
			for(Map<String, Object> question : questionParentList) {
				questionList.add(question.get("quiz question").toString());
			}

			String question = questionList.toString();

			userInputPrepare.setString(1, user.currentUser);
			userInputPrepare.setString(2, user.currentQuiz);
			userInputPrepare.setString(3, userAnswerFinal);
			userInputPrepare.setString(4, pointList.toString());
			userInputPrepare.setInt(5, totalPoints);
			userInputPrepare.setString(6, question);

			userInputPrepare.executeUpdate();

			System.out.println("\n\nUSERNAME: " + user.currentUser +
					"\nQUIZ TITLE: " + user.currentQuiz +
					"\nQUIZ ANSWER: " + userAnswerFinal +
					"\nQUIZ POINT: " + pointList.toString() +
					"\nTOTAL POINT: " + correctAnswer);


		} catch (Exception exception) { exception.printStackTrace(); }

	}

	private Scene getScene(Map<String, Object> quizRoot) {

		try {

			Parent root = null;

			String category = (String) quizRoot.get("category");

			if (category.contains("Fill")) {

				FXMLLoader loader = getLoader(questionType.FillInTheBlank);
				root = loader.load();

				FITBAnswerController controller = loader.getController();
				controller.initQuiz(quizRoot);

			} else if(category.contains("Multiple")) {

				FXMLLoader loader = getLoader(questionType.MultipleChoice);
				root = loader.load();

				multipleChoiceAnswerController controller = loader.getController();
				controller.initQuiz(quizRoot);

			} else if(category.contains("True")) {

				FXMLLoader loader = getLoader(questionType.TrueOrFalse);
				root = loader.load();

				TofAnswerController controller = loader.getController();
				controller.initQuiz(quizRoot);

			}

			return new Scene(root);

		} catch (Exception exception) { exception.printStackTrace(); }

        return null;

    }

	// get FXMLLoader
	enum questionType {
		MultipleChoice,
		FillInTheBlank,
		TrueOrFalse
	}
	protected FXMLLoader getLoader(questionType question){
		String FXMLLoaderString = null;
		switch(question) {
			case MultipleChoice:
				FXMLLoaderString = "/multipleChoice/answer/MultipleChoice.fxml";
				break;
			case FillInTheBlank:
				FXMLLoaderString = "/fitb/answer/FITB.fxml";
				break;
			case TrueOrFalse:
				FXMLLoaderString = "/tof/answer/Tof.fxml";
				break;
			default:
				throw new Error("We haven't implemented this yet lil bro");
		}

		return new FXMLLoader(getClass().getResource(FXMLLoaderString));

	}
	
}
