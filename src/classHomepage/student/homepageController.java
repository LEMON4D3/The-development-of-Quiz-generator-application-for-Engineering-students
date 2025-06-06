package classHomepage.student;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.Util;
import util.controller;
import util.quizCreateControllerExtend;
import util.user;

public class homepageController implements Initializable{

	@FXML
	VBox announcementContainer, postedContainer;
	
	@FXML
	Label announcementT, classNameT, ssrT;



	List<Map<String, Object>> announcementContainerList = new ArrayList<>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		String classNameString = user.currentClass;
		classNameT.setText(classNameString);

		List<Map<String, Object>> classList = Util.getClassOrUserListDB(Util.dataClass.Class);
		String ssrString = "";
		for(Map<String, Object> list : classList) {
			if(list.get("class name").equals(user.currentClass)) {

				ssrString = (String) list.get("section") + "/" + (String) list.get("subject") + "/" + (String) list.get("room");
				break;

			}
		}
		ssrT.setText(ssrString);


		getAnnouncement();

	}

	// Button Function Section

	public void backBtnFn(ActionEvent event) throws IOException {

		user.currentClass = null;
		new controller().changeScene(event, "/homepage/student/Homepage.fxml");

	}

	public void userBtnFn(MouseEvent event) throws IOException {

		new Util().userPopUp(event);

	}

	// Backend Function Section

	List<Map<String, Object>> announcementList = new ArrayList<>();

	private void getAnnouncement() {
		try {

			announcementList = Util.getQuizOrAnnouncementClassListDB("", true);
			if(announcementList.isEmpty()) return;

			announcementContainer.getChildren().clear();
			announcementContainer.setSpacing(15);

			for(Map<String, Object> container : announcementList) {

				if((Integer) container.get("isQuiz") == 2)
					announcementContainer.getChildren().add(new codeExerciseTemplate(container));
				else if((Integer) container.get("isQuiz") == 1)
					announcementContainer.getChildren().add(new quizTemplate(container));
				else if((Integer) container.get("isQuiz") == 0)
					announcementContainer.getChildren().add(new announcementTemplate(container));

			}


		} catch (Exception exception) {exception.printStackTrace();}
	}

	private class quizTemplate extends VBox {

		private String announcementStyle = "-fx-background-color: #5A95BA; -fx-background-radius: 20;";

		Map<String, Object> announcementGlobal;

		Button startBtn = new Button();

		quizTemplate(Map<String, Object> announcement) {

			announcementGlobal = announcement;

			this.setPadding(new Insets(15, 15, 15, 15));
			this.setMinHeight(105);
			this.setPrefWidth(654);
			this.setStyle(announcementStyle);
			this.setSpacing(5);

			GridPane topPane = new GridPane();
			topPane.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(60); }});

			String quizTitleString = "Quiz Title: " + announcement.get("announcement");
			Label quizTitleT = new Label(quizTitleString);
			quizTitleT.setFont(new Font("Inter", 20));
			quizTitleT.setStyle("-fx-text-fill: white;");
			GridPane.setHalignment(quizTitleT, HPos.LEFT);
			topPane.add(quizTitleT, 0, 0);

			HBox bottomPane = new HBox();
			bottomPane.setAlignment(Pos.CENTER_RIGHT);

			// Start button setup
			getStartBtn();

			bottomPane.getChildren().add(startBtn);
			this.getChildren().addAll(topPane, bottomPane);



		}

		private void getStartBtn() {

			List<Map<String, Object>> quizList = Util.getQuizOrAnnouncementClassListDB(announcementGlobal.get("announcement").toString() + "List", false);
			for(Map<String, Object> quiz : quizList) {
				if(quiz.get("username").equals(user.currentUser)) {

					startBtn.setText("See result");
					startBtn.setStyle("-fx-background-color: #00799A; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-size: 20px;");
					startBtn.setOnAction(event -> {

						user.currentQuiz = (String) announcementGlobal.get("announcement");
						new controller().changeScene(event, "/quizReport/student/QuizReport.fxml");

					});
					GridPane.setHalignment(startBtn, HPos.RIGHT);

					return;
				}
			}

			startBtn = new Button("Start Taking Quiz");
			startBtn.setStyle("-fx-background-color: #00799A; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-size: 20px;");
			startBtn.setOnAction(event -> startQuizFn(event));
			GridPane.setHalignment(startBtn, HPos.RIGHT);

		}

		private void startQuizFn(ActionEvent event) {

			Stage mainStage = new controller().getStage(event);

			user.currentQuiz = (String) announcementGlobal.get("announcement");

			List<Map<String, Object>> quizList = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz, false);

			Map<String, Object> quiz = quizList.get(0);
			System.out.println("Current Quiz: " + quiz);

			quizCreateControllerExtend quizController = new quizCreateControllerExtend();
			quizController.prepareQuiz(quizList);

			mainStage.setScene(quizController.quizListScene.getFirst());
			mainStage.show();

		}

	}

	private class announcementTemplate extends VBox {

		private String announcementStyle = "-fx-background-color: #5A95BA; -fx-background-radius: 20;";

		announcementTemplate(Map<String, Object> list) {

			this.setAlignment(Pos.TOP_LEFT);
			this.setFillWidth(true);
			this.setPrefHeight(VBox.USE_COMPUTED_SIZE);
			this.setPrefWidth(654);
			this.setSpacing(15);
			this.setStyle(announcementStyle);
			this.setPadding(new Insets(25, 15, 25, 15));

			Label accountNameT = new Label((String) list.get("announcement"));
			accountNameT.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
			this.getChildren().add(accountNameT);

			String announcementTStyle = "-fx-background-color: white; -fx-background-radius: 20; -fx-text-fill: black; -fx-font-size: 18px;";
			Label announcementT = new Label((String) list.get("announcement description"));
			announcementT.setWrapText(true);
			announcementT.setPrefHeight(Label.USE_COMPUTED_SIZE);
			announcementT.setPrefWidth(Double.MAX_VALUE);
			announcementT.setStyle(announcementTStyle);
			announcementT.setPadding(new Insets(15, 20, 15, 20));

			this.getChildren().add(announcementT);
		}

	}

	private class codeExerciseTemplate extends VBox {

		private String announcementStyle = "-fx-background-color: #5A95BA; -fx-background-radius: 20;";

		Map<String, Object> announcementGlobal;

		codeExerciseTemplate(Map<String, Object> container) {

			announcementGlobal = container;

			this.setFillWidth(true);
			this.setPrefHeight(VBox.USE_COMPUTED_SIZE);
			this.setPadding(new Insets(25, 15, 25, 15));
			this.setSpacing(10);

			this.setStyle(announcementStyle);

			this.getChildren().addAll(topPane(), middlePane(), bottomPane());

		}

		VBox topPane() {

			VBox pane = new VBox();
			pane.setSpacing(5);

			Label typeT = new Label("Code Exercise");
			typeT.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

			Label titleT = new Label("Code Exercise Title: " + announcementGlobal.get("announcement").toString());
			titleT.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

			pane.getChildren().addAll(typeT, titleT);

			return pane;

		}

		TextArea middlePane() {

			List<Map<String, Object>> codeList = Util.getQuizOrAnnouncementClassListDB(announcementGlobal.get("announcement").toString(), false);
			Map<String, Object> problem = codeList.get(0);

			TextArea problemArea = new TextArea();
			problemArea.setText(problem.get("quiz question").toString());
			problemArea.setEditable(false);

			return problemArea;

		}

		HBox bottomPane() {

			HBox pane = new HBox();
			pane.setSpacing(25);
			pane.setAlignment(Pos.CENTER);

			try {

				List<Map<String, Object>> userList = Util.getQuizOrAnnouncementClassListDB(announcementGlobal.get("announcement").toString() + "List", false);
				for(Map<String, Object> var : userList) {

					if(var.get("username").equals(user.currentUser)) {

						Button passedBtn = new Button("Passed");
						passedBtn.setStyle("-fx-background-color: #044456; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-size: 20px;");
						passedBtn.setDisable(true);

						pane.getChildren().add(passedBtn);
						return pane;

					}

				}

				Button codeBtn = new Button("Code");
				codeBtn.setStyle("-fx-background-color: #00799A; -fx-background-radius: 20; -fx-text-fill: white; -fx-font-size: 20px;");
				codeBtn.setOnAction(e -> {

					user.currentQuiz = (String) announcementGlobal.get("announcement");
					new controller().changeScene(e, "/compiler/answer/Compiler.fxml");

				});
				pane.getChildren().addAll(codeBtn);

			} catch (Exception exception) { exception.printStackTrace(); }

			return pane;

		}

	}

}
