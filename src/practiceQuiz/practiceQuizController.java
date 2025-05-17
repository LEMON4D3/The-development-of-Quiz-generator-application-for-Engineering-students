package practiceQuiz;

import fitb.create.FITBCreateController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import multipleChoice.create.multipleChoiceCreateController;
import quizCard.quizCardController;
import tof.create.tofCreateController;
import util.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class practiceQuizController extends quizCreateControllerExtend.classController implements Initializable {

	@FXML
	private VBox quizContainer;

	@FXML
	private Label quizTitleT;

	practiceQuizController mainController;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		mainController = this;
		refreshList();

	}

	@Override
	public void setContainerList(QuizClass.quizContainer container, int index) {

		try {

			Connection studentQuizConnection = Util.getStudentQuizConnectionDB();
			String prepareUpdateQuiz = "update `" + user.currentQuiz + "` set `quiz question` = ?, `quiz answer` = ?, `quiz option` = ?, `quiz hint` = ?, `category` = ?, `point` = ?, `time` = ? where id = '" + container.id + "'";
			PreparedStatement preparedUpdateQuiz = studentQuizConnection.prepareStatement(prepareUpdateQuiz);

			preparedUpdateQuiz.setString(1, container.quizQuestion);
			preparedUpdateQuiz.setString(2, container.quizAnswer);
			preparedUpdateQuiz.setString(3, container.quizOptions);
			preparedUpdateQuiz.setString(4, container.hint);
			preparedUpdateQuiz.setString(5, container.quizCategory);
			int pointFinal = Integer.parseInt(container.point.split(" ")[0]);
			preparedUpdateQuiz.setInt(6, pointFinal);
			preparedUpdateQuiz.setString(7, container.time);
			preparedUpdateQuiz.executeUpdate();
			preparedUpdateQuiz.close();
			studentQuizConnection.close();
			refreshList();



		} catch (Exception exception) { exception.printStackTrace(); }

	}

	public void deleteContainerList(QuizClass.quizContainer container) {

		try {

			Connection studentQuizConnection = Util.getStudentQuizConnectionDB();

			String prepareDeleteQuiz = "delete from `" + user.currentQuiz + "` where id = '" + container.id + "'";
			Statement studentQuizStatement = studentQuizConnection.createStatement();
			studentQuizStatement.executeUpdate(prepareDeleteQuiz);
			refreshList();


		} catch (Exception exception) { exception.printStackTrace(); }

	}

	@Override
	public void addContainerList(QuizClass.quizContainer addContainer) {

		try {

			Connection studentQuizConnection = Util.getStudentQuizConnectionDB();

			String prepareInsertQuiz = "insert into `" + user.currentQuiz + "` (`quiz question`, `quiz answer`, `quiz option`, `quiz hint`, `category`, `point`, `time`) values (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = studentQuizConnection.prepareStatement(prepareInsertQuiz);

			preparedStatement.setString(1, addContainer.quizQuestion);
			preparedStatement.setString(2, addContainer.quizAnswer);
			preparedStatement.setString(3, addContainer.quizOptions);
			preparedStatement.setString(4, addContainer.hint);
			preparedStatement.setString(5, addContainer.quizCategory);

			int pointFinal = Integer.parseInt(addContainer.point.split(" ")[0]);
			preparedStatement.setInt(6, pointFinal);
			preparedStatement.setString(7, addContainer.time);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			studentQuizConnection.close();

			refreshList();

		} catch (Exception exception) { exception.printStackTrace(); }
	}

	@Override
	public void refreshList() {

		quizTitleT.setText(user.currentQuiz);

		quizContainer.getChildren().clear();
		cardContainerList.clear();

		List<Map<String, Object>> quizList = Util.getStudentQuizListDB(false);
		if(quizList == null)  return;

		for(Map<String, Object> tableInfo : quizList) {

			QuizClass.quizContainer container = new QuizClass.quizContainer();
			container.id = (Integer) tableInfo.get("id");
			container.quizAnswer = (String) tableInfo.get("quiz answer");
			container.quizQuestion = (String) tableInfo.get("quiz question");
			container.quizOptions = (String) tableInfo.get("quiz option");

			container.hint = (String) tableInfo.get("quiz hint");
			container.quizCategory = (String) tableInfo.get("category");
			container.point = (Integer) tableInfo.get("point") + " Points";
			container.time = (String) tableInfo.get("time");

			cardContainerList.add(container);

		}

		for(int i = 0; i < cardContainerList.size(); i++) {

			quizContainer.getChildren().add(new cardContainer(cardContainerList.get(i), i));

		}

	}

	public void addQuiz(ActionEvent event) {

		try {

			Stage mainStage = new controller().getStage(event);
			user.userQuizOption = user.quizOption.Add;

			Stage miniStage = new Stage();
			miniStage.initModality(Modality.APPLICATION_MODAL);
			miniStage.initOwner(mainStage);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fitb/create/FITB.fxml"));
			Parent root = loader.load();

			FITBCreateController controller = loader.getController();
			controller.setQuizCardController(this, 0);

			miniStage.setScene(new Scene(root));
			miniStage.showAndWait();

		} catch (Exception exception) { exception.printStackTrace(); }

	}

	public void backBtnFn(ActionEvent event) {

		new controller().changeScene(event, "/homepage/student/Homepage.fxml");
		user.currentClass = null;

	}

	public void startBtnFn(ActionEvent event) {

		Stage mainStage = new controller().getStage(event);

		List<Map<String, Object>> quizList = Util.getStudentQuizListDB(false);

		Map<String, Object> quiz = quizList.get(0);
		System.out.println("Quiz List: " + quizList);

		quizCreateControllerExtend quizController = new quizCreateControllerExtend();
		quizController.prepareQuiz(quizList);

		mainStage.setScene(quizController.quizListScene.getFirst());
		mainStage.show();

	}

	private class cardContainer extends VBox{

		QuizClass.quizContainer container;
		int containerIndex;

		VBox superContainer;


		cardContainer(QuizClass.quizContainer Container, int index) {

			this.superContainer = quizContainer;

			container = Container;
			containerIndex = index;

			this.getStyleClass().add("quizContainer");
			this.setPrefHeight(165);
			this.setFillWidth(true);

			this.setPadding(new Insets(15, 25, 15, 25));

			this.getChildren().add(new topPane());

			Label questionT = new Label("Question: ");
			questionT.setPadding(new Insets(5, 0, 0, 0));
			this.getChildren().add(questionT);

			Label quizQuestionT = new Label(container.quizQuestion);
			this.getChildren().add(quizQuestionT);

			Label answerT = new Label("Answer: ");
			this.getChildren().add(answerT);

			Label quizAnswerT = new Label(container.quizAnswer);
			this.getChildren().add(quizAnswerT);

			Label optionT = new Label("Quiz option: ");
			this.getChildren().add(optionT);

			HBox optionContainer = new HBox();

			// quizOptions
			Label quizOptionT = new Label(container.quizOptions);
			this.getChildren().add(quizOptionT);


			this.getChildren().add(optionContainer);

			this.setSpacing(5);

		}

		class topPane extends HBox {

			topPane() {




				this.setPadding(new Insets(0, 15, 0, 15));

				HBox leftContainer = new HBox();
				leftContainer.setAlignment(Pos.CENTER_LEFT);
				leftContainer.setSpacing(25);

				Label typeQuestionT = new Label(container.quizCategory);
				typeQuestionT.getStyleClass().add("whiteBlackContainer");
				typeQuestionT.setStyle("-fx-font-size: 16;");
				typeQuestionT.setPrefWidth(163);
				typeQuestionT.setAlignment(Pos.CENTER);

				ObservableList<String> pointList = FXCollections.observableArrayList();
				for(int i = 1; i <= 100; i++)
					pointList.add(i + " points");

				ComboBox pointCombo = new ComboBox(pointList);
				pointCombo.setOnAction(f -> { container.point = pointCombo.getValue().toString(); });
				pointCombo.setValue(container.point);
				pointCombo.getStyleClass().add("whiteBlackContainer");
				pointCombo.setPrefWidth(100);

				leftContainer.getChildren().addAll(typeQuestionT, pointCombo);


				HBox rightContainer = new HBox();
				rightContainer.setAlignment(Pos.CENTER_RIGHT);
				rightContainer.setSpacing(25);

				Button editBtn = new Button("Edit");
				editBtn.getStyleClass().add("whiteBlackContainer");
				editBtn.setPrefWidth(50);
				editBtn.setOnAction(event -> {

					//setContainerList(container, containerIndex);
					editQuiz(event);

				});

				Image img = new Image(getClass().getResource("/quizCard/rsc/delete.png").toExternalForm());
				ImageView imgView = new ImageView(img);
				imgView.setFitWidth(25);
				imgView.setFitHeight(25);

				Button deleteBtn = new Button("Delete", imgView);
				deleteBtn.setOnAction(event -> {

					System.out.println("CLicking");
					deleteContainerList(container);
					System.out.println("Deleted!");

				});


				rightContainer.getChildren().addAll(editBtn, deleteBtn);
				this.getChildren().addAll(leftContainer, rightContainer);

				Platform.runLater(() -> {

					Bounds bounds = superContainer.localToScene(superContainer.getBoundsInLocal());

					leftContainer.setPrefWidth(bounds.getWidth() / 2);
					rightContainer.setPrefWidth(bounds.getWidth() / 2);

				});
			}

			private <T> void editQuiz(T event) {

				try {

					user.userQuizOption = user.quizOption.Edit;
					System.out.println("Hitting those backshots");

					Stage mainStage = new controller().getStage(event);

					Stage miniStage = new Stage();
					miniStage.initModality(Modality.APPLICATION_MODAL);
					miniStage.initOwner(mainStage);

					String url = null;

					if(container.quizCategory.contains("Fill"))
						url = "/fitb/create/FITB.fxml";
					else if(container.quizCategory.contains("Multiple"))
						url = "/multipleChoice/create/MultipleChoice.fxml";
					else if(container.quizCategory.contains("True"))
						url = "/tof/create/Tof.fxml";

					FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
					Parent root = loader.load();

					miniStageFn(miniStage, loader);

					Scene miniScene = new Scene(root);

					miniStage.setScene(miniScene);
					miniStage.showAndWait();

				} catch(Exception e) {
					e.printStackTrace();
				}

			}

			private void miniStageFn(Stage miniStage, FXMLLoader loader) {

				if(container.quizCategory.contains("Fill")) {

					FITBCreateController controller = loader.getController();
					controller.setQuizCardController(mainController, containerIndex, container);


				} else if(container.quizCategory.contains("Multiple")) {

					multipleChoiceCreateController controller = loader.getController();
					controller.setQuizCardController(mainController, containerIndex, container);


				} else if(container.quizCategory.contains("True")) {

					tofCreateController controller = loader.getController();
					controller.setQuizCardController(mainController, containerIndex, container);

				}

			}

		}

	}



}
