package quizCard;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import fitb.create.FITBCreateController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import tof.create.tofCreateController;
import util.QuizClass;
import util.Util;
import util.controller;
import util.quizCreateControllerExtend;
import util.user;


public class quizCardController extends quizCreateControllerExtend.classController implements Initializable{
	
	@FXML
	VBox quizContainer;
	
	@FXML
	Label quizTitleT, questionPointT;

	quizCardController mainController = null;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		mainController = this;
		refreshList();
		
		Thread getMainStage = new Thread(() -> {
			
			Platform.runLater(() -> {
				Stage tempStage = (Stage) quizContainer.getScene().getWindow();
				if(tempStage != null && tempStage != mainStage)
					mainStage = tempStage;
			});
			
		});

		getMainStage.run();
	}

	// back button function section
	public void backBtnFn(ActionEvent event) throws IOException {

		if(user.isTeacher) new controller().changeScene(event, "/classHomepage/teacher/Homepage.fxml");
		else new controller().changeScene(event, "/homepage/student/Homepage.fxml");

	}

	@Override
	public void refreshList() {

		if(quizTitle != null) quizTitleT.setText(quizTitle);

		if(!cardContainerList.isEmpty() && quizTitle == null)
			quizTitleT.setText(cardContainerList.get(0).quizTitle);

		int totalPoints = 0;

		quizContainer.getChildren().clear();
		for(int i = 0; i < cardContainerList.size(); i++) {

			quizContainer.getChildren().add(new cardContainer(cardContainerList.get(i), i));
			totalPoints += Integer.parseInt(cardContainerList.get(i).point.split(" ")[0]);

		}

		for(QuizClass.quizContainer i: cardContainerList) {
			System.out.println("Quiz Container: " + i.quizQuestion);
		}

		int totalQuestion = cardContainerList.size();
		questionPointT.setText(totalQuestion + " Questions ( " + totalPoints + " Points )");


	}
	
	public void addQuiz(ActionEvent e) {
		
		try {
		
			user.userQuizOption = user.quizOption.Add;
			Stage mainStage = new controller().getStage(e);
			
			Stage miniStage = new Stage();
			miniStage.initModality(Modality.APPLICATION_MODAL);
			miniStage.initOwner(mainStage);
		
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fitb/create/FITB.fxml"));
			Parent root = loader.load();
			
			FITBCreateController controller = loader.getController();
			controller.setQuizCardController(this, 0);
			
			Scene scene = new Scene(root);
			miniStage.setScene(scene);
			miniStage.showAndWait();
			
		} catch(Exception f) {
			
			f.printStackTrace();
			
		}
		
	}
	
	private Stage mainStage = null;
	
	// cardContainer
	
 	private class cardContainer extends VBox{
		
 		QuizClass.quizContainer container;
		int containerIndex;
		
		
		cardContainer(QuizClass.quizContainer Container, int index) {
			
			
			container = Container;
			containerIndex = index;
			
			this.setStyle("-fx-background-color: #5A95BA; -fx-background-radius: 20;");
			this.setPrefHeight(165);
			
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
		
		class topPane extends GridPane{
			
			topPane() {
				
				this.setPadding(new Insets(0, 15, 0, 15));
				
				
				Label typeQuestionT = new Label(container.quizCategory);
				typeQuestionT.setStyle("-fx-text-fill: black; -fx-background-color: white; -fx-background-radius: 20;");
				typeQuestionT.setStyle("-fx-font-size: 16;");
				typeQuestionT.setPrefWidth(163);
				typeQuestionT.setAlignment(Pos.CENTER);
				
				this.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(20); }});
				this.add(typeQuestionT, 0, 0);
				
				ObservableList<String> pointList = FXCollections.observableArrayList();
				for(int i = 0; i <= 100; i++)
					pointList.add(i + " points");
				
				ComboBox pointCombo = new ComboBox(pointList);
				pointCombo.setOnAction(f -> {

					container.point = pointCombo.getValue().toString();
					setContainerList(container, containerIndex);

				});
				pointCombo.setValue(container.point);
				pointCombo.getStyleClass().add("whiteBlackContainer");
				pointCombo.setPrefWidth(100);
				this.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(70); }});
				this.add(pointCombo, 1, 0);
				
				Button editBtn = new Button("Edit");
				editBtn.getStyleClass().add("whiteBlackContainer");
				editBtn.setPrefWidth(50);
				editBtn.setOnAction(event -> {
					
					//setContainerList(container, containerIndex);
					editQuiz(event);
					
				});
				this.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(8); }});
				this.add(editBtn, 2, 0);
				
				Image img = new Image(getClass().getResource("/quizCard/rsc/delete.png").toExternalForm());
				ImageView imgView = new ImageView(img);
				imgView.setOnMouseClicked(f -> { deleteContainerList(containerIndex); System.out.println("Deleted!"); });
				
				imgView.setFitWidth(24);
				imgView.setFitHeight(24);
				
				this.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(0); }});
				this.add(imgView, 3, 0);
				
			}
			
			private <T> void editQuiz(T event) {
				
				try {
					
					user.userQuizOption = user.quizOption.Edit;
					
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

					quizCreateControllerExtend controller = loader.getController();
					controller.setQuizCardController(mainController, containerIndex);

					System.out.println("Container: " + cardContainerList);
					controller.prepareQuiz = container;
					controller.containerIndex = containerIndex;
					controller.initComponents();

					//miniStageFn(miniStage, loader);
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
	
 	// save button function
 	
 	String  quizTitle = null;
 	
 	public void setQuizTitle( String quizTitle) {
 		this.quizTitle = quizTitle;
 	}
 	
 	public void saveBtnFn(ActionEvent event) throws IOException {
 		
  		String url = (user.isTeacher) ? teacherSaveFn(event) : studentSaveFn(event);
 		
 		new controller().changeScene(event, url);
 		
 	}

	 private String teacherEditFn(ActionEvent event) {

		 try {

			 Connection classQuizConnection = Util.getClassConnectionDB();

			 String deleteQuizString = "delete from `" + quizTitle + "`";
			 Statement deleteQuizStmt = classQuizConnection.createStatement();
			 deleteQuizStmt.execute(deleteQuizString);

			 String prepareInsertQuizString = "insert into `" + quizTitle + "`("
						+ "`quiz answer`, `quiz question`, `quiz option`, `quiz hint`,"
						+ "category, point) values (?, ?, ?, ?, ?, ?)";
			 PreparedStatement insertQuizPrepare = classQuizConnection.prepareStatement(prepareInsertQuizString);

			 for(int i = 0; i < cardContainerList.size(); i++) {

				 QuizClass.quizContainer container = cardContainerList.get(i);

				 insertQuizPrepare.setString(1, container.quizAnswer);
				 insertQuizPrepare.setString(2, container.quizQuestion);
				 insertQuizPrepare.setString(3, container.quizOptions);
				 insertQuizPrepare.setString(4, container.hint);
				 insertQuizPrepare.setString(5, container.quizCategory);
				 insertQuizPrepare.setInt(6, Integer.parseInt(container.point.split(" ")[0]));

				 insertQuizPrepare.executeUpdate();
			 }

			 return "/classHomepage/teacher/Homepage.fxml";
		 } catch (Exception exception) {
			 exception.printStackTrace();
		 }

         return "";
     }

 	private String teacherSaveFn(ActionEvent event) {
 		
 		try {

			 quizTitle = quizTitleT.getText();
			 if(status == quizStatus.Edit) return teacherEditFn(event);


 			String getClassConnectionString = "application/classes/" + user.currentClass + "/" + user.currentClass + ".db";
 			Connection classConnection = DriverManager.getConnection("jdbc:sqlite:" + getClassConnectionString);
 			
 			String insertAnnouncement = "insert into classes(announcement, isQuiz) values (?, ?)";
 			PreparedStatement prepareClassInsert = classConnection.prepareStatement(insertAnnouncement);
 			
 			prepareClassInsert.setString(1, quizTitle);
 			prepareClassInsert.setInt(2, 1);
 			
 			prepareClassInsert.execute();
 			
 			String createNewTableString = "create table if not exists `" + quizTitle + "` ("
 					+ "id integer primary key autoincrement,"
 					+ "`quiz answer` text,"
 					+ "`quiz question` text,"
 					+ "`quiz option` text,"
 					+ "`quiz hint` text,"
 					+ "category text,"
 					+ "point integer"
 					+ ")";
 			Statement createNewTableStatement = classConnection.createStatement();
 			createNewTableStatement.execute(createNewTableString);
 			
 			String insertQuizString = "insert into `" + quizTitle + "` ("
 					+ "id, `quiz answer`, `quiz question`, `quiz option`, `quiz hint`,"
 					+ "category, point) values (?, ?, ?, ?, ?, ?, ?)";
 			PreparedStatement prepareInsertQuiz = classConnection.prepareStatement(insertQuizString);
 			
 			for(int i = 0; i < cardContainerList.size(); i++) {
 				
 				QuizClass.quizContainer container = cardContainerList.get(i);
 				
 				prepareInsertQuiz.setInt(1, i);
 				prepareInsertQuiz.setString(2, container.quizAnswer);
 				prepareInsertQuiz.setString(3, container.quizQuestion);
 				prepareInsertQuiz.setString(4, container.quizOptions);
 				prepareInsertQuiz.setString(5, container.hint);
 				prepareInsertQuiz.setString(6, container.quizCategory);
 				prepareInsertQuiz.setInt(7, Integer.parseInt(container.point.split(" ")[0]));
 				
 				prepareInsertQuiz.executeUpdate();
 				
 			}

			 String createQuizListDBString = "create table if not exists `" + quizTitle + "List` (" +
					 "id integer primary key autoincrement," +
					 "username text," +
					 "`quiz title` text," +
					 "`quiz answer` text," +
					 "`quiz point` text," +
					 "`total point` integer," +
					 "`quiz question` text" +
					 ")";

			 Statement createQuizListDBStatement = classConnection.createStatement();
			 createQuizListDBStatement.execute(createQuizListDBString);

			 System.out.println("Successfully created table: " + quizTitle);

 		} catch(Exception exception) {
 			exception.printStackTrace();
 		}
 		
 		return "/classHomepage/teacher/Homepage.fxml";
 	}

 	private String studentSaveFn(ActionEvent event) {
 		
		try {

			quizTitle = quizTitleT.getText();

			String checkRequireDirString = "application/user/student/" + user.currentUser + "/quiz/";
			File checkRequireDirFile = new File(checkRequireDirString);
			if(!checkRequireDirFile.exists() && !checkRequireDirFile.isDirectory())
				checkRequireDirFile.mkdirs();
			
			String checkRequireDBString = checkRequireDirString + "quiz.db";
			File checkRequireDBFile = new File(checkRequireDBString);
			checkRequireDBFile.createNewFile();
			
 			Connection quizConnection = DriverManager.getConnection("jdbc:sqlite:" + checkRequireDBString);
 			String createRequireTableString = "create table if not exists quiz("
 					+ "id integer not null primary key autoincrement,"
 					+ "`quiz name` text not null"
 					+ ")";
 			Statement createRequireTableStatement = quizConnection.createStatement();
 			createRequireTableStatement.execute(createRequireTableString);
 			
 			String insertQuizString = "insert into quiz(`quiz name`) values (?)";
 			PreparedStatement prepareClassInsert = quizConnection.prepareStatement(insertQuizString);
 			
 			prepareClassInsert.setString(1, quizTitleT.getText());
 			prepareClassInsert.execute();
 			
 			String createNewTableString = "create table if not exists `" + quizTitleT.getText() + "`("
 					+ "id integer not null primary key autoincrement,"
 					+ "`quiz answer` text,"
 					+ "`quiz question` text,"
 					+ "`quiz option` text,"
 					+ "`quiz hint` text,"
 					+ "category text,"
 					+ "point integer"
 					+ ")";
 			
 			Statement createNewTableStatement = quizConnection.createStatement();
 			createNewTableStatement.execute(createNewTableString);
 			
 			String insertQuizString2 = "insert into `" + quizTitle + "` ("
 					+ "id, `quiz answer`, `quiz question`, `quiz option`, `quiz hint`,"
 					+ "category, point) values (?, ?, ?, ?, ?, ?, ?)";
 			PreparedStatement prepareInsertQuiz = quizConnection.prepareStatement(insertQuizString2);
 			
 			for(int i = 0; i < cardContainerList.size(); i++) {
 				
 				QuizClass.quizContainer container = cardContainerList.get(i);

 				prepareInsertQuiz.setInt(1, i);
 				prepareInsertQuiz.setString(2, container.quizAnswer);
 				prepareInsertQuiz.setString(3, container.quizQuestion);
 				prepareInsertQuiz.setString(4, container.quizOptions);
 				prepareInsertQuiz.setString(5, container.hint);
 				prepareInsertQuiz.setString(6, container.quizCategory);
 				prepareInsertQuiz.setInt(7, Integer.parseInt(container.point.split(" ")[0]));
 				
 				prepareInsertQuiz.executeUpdate();
 				
 			}
 			
 		} catch(Exception exception) {
 			exception.printStackTrace();
 		}
 		
 		return "/homepage/student/Homepage.fxml";
 		
 	}
 	
 	// edit button function
 	quizStatus status = quizStatus.New;
 	
 	enum quizStatus {
 		
 		New,
 		Edit
 		
 	}
 	
 	public void editQuizFn(String tableName) {
 		
 		try {

 			
	 		status = quizStatus.Edit;
	 		this.quizTitle = tableName;
			this.quizTitleT.setText(tableName);

	 		String getClassNameString = "application/classes/" + user.currentClass + "/" + user.currentClass + ".db";
	 		Connection classNameConnection = DriverManager.getConnection("jdbc:sqlite:" + getClassNameString);
	 		
	 		String getTableString = "select * from `" + tableName + "`";
	 		Statement tableInfoStatement = classNameConnection.createStatement();
	 		
	 		List<Map<String, Object>> tableList = Util.getList(tableInfoStatement, getTableString);
	 		cardContainerList.clear();

	 		for(int i = 0; i < tableList.size(); i++) {

				QuizClass.quizContainer container = new QuizClass.quizContainer();
	 			Map<String, Object> tableInfo = tableList.get(i);
	 			container.quizAnswer = (String) tableInfo.get("quiz answer");
	 			container.quizQuestion = (String) tableInfo.get("quiz question");
				container.quizOptions = (String) tableInfo.get("quiz option");
	 			
	 			container.hint = (String) tableInfo.get("quiz hint");
	 			container.quizCategory = (String) tableInfo.get("category");
	 			container.point = (Integer) tableInfo.get("point") + " Points";

			 	cardContainerList.add(container);

	 		}

	 		refreshList();
	 		
 		} catch(Exception exception) { exception.printStackTrace(); }
 	}
 	
 	
}


