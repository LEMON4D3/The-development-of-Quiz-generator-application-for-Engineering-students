package homepage.student;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javax.swing.GroupLayout.Alignment;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import util.Util;
import util.controller;
import util.fxml.userPopUp;
import util.user;
import homepage.lib.emptyContainer;
import homepage.student.*;

public class homepageController implements Initializable{
	
	@FXML
	HBox classesHBox, userDeckHBox;
	
	@FXML
	Button joinClass;

	@FXML
	VBox noClassT, noQuizT;

	List<String> className = new ArrayList<>();
	List<String> userDeckName = new ArrayList<>();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		classesHBox.setSpacing(20);
		userDeckHBox.setSpacing(20);
		
		getList();
		
	}
	
	private void getList() {
		
		String userDirString = "application/user/student/" + user.currentUser + "/classes/";
		File userDirFile = new File(userDirString);
		if(!userDirFile.exists() && !userDirFile.isDirectory())
			return;
		
		refreshClassList();
		
		String deckDirString = "application/user/student/" + user.currentUser + "/quiz/";
		File deckDirFile = new File(deckDirString);
		if(!deckDirFile.exists() && !deckDirFile.isDirectory())
			return;
		
		refreshDeckList();
		
	}
	
	private void refreshClassList() {
		
		try {
		
			List<Map<String, Object>> classesList = new ArrayList<>();
			
			String userDir = "application/user/student/" + user.currentUser + "/";
			Connection userClassesConnection = DriverManager.getConnection("jdbc:sqlite:" + userDir + "classes/classes.db");
			
			String getClassesString = "select `class name` from classes";
			Statement getClassesStatement = userClassesConnection.createStatement();
			
			classesList = Util.getList(getClassesStatement, getClassesString);
			
			if(classesHBox == null)
				classesHBox = new HBox();
			
			classesHBox.getChildren().clear();
			if(classesList.isEmpty()) {

				classesHBox.setAlignment(Pos.CENTER);
				classesHBox.getChildren().add(noClassT);
				return;

			}

			classesHBox.setAlignment(Pos.CENTER_LEFT);
			for(Map<String, Object> i : classesList)
				classesHBox.getChildren().add(new classContainer((String) i.get("class name")));

		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void refreshDeckList() {
		
		
		List<Map<String, Object>> quizList = new ArrayList<>();
		
		try {
			
			
			String userDir = "application/user/student/" + user.currentUser + "/";
			Connection userQuizConnection = DriverManager.getConnection("jdbc:sqlite:" + userDir + "quiz/quiz.db");
			
			String getQuizString = "select `quiz name` from quiz";
			Statement getQuizStatement = userQuizConnection.createStatement();
			
			quizList = Util.getList(getQuizStatement, getQuizString);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		if(userDeckHBox == null)
			userDeckHBox = new HBox();

		userDeckHBox.getChildren().clear();
		if(quizList.isEmpty()) {

			userDeckHBox.setAlignment(Pos.CENTER);
			userDeckHBox.getChildren().add(noQuizT);
			return;

		}

		userDeckHBox.setAlignment(Pos.CENTER_LEFT);
		for(Map<String, Object> i: quizList)
			userDeckHBox.getChildren().add(new quizContainer((String) i.get("quiz name")));
			
		
	}

	public void joinBtnFn(ActionEvent e) {
		
		popUp(e);
		
	}

	private void popUp(ActionEvent e) {
		
		Stage stage = (Stage)((Node)((EventObject)e).getSource()).getScene().getWindow();
		
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.initOwner(stage); 
		
		VBox pane = new VBox();
		pane.setAlignment(Pos.CENTER);
		pane.setStyle("-fx-background-color: white;");
		
		pane.setPrefWidth(500);
		pane.setMinHeight(500);
		
		pane.setSpacing(10);
		
		Label temp = new Label("Class Code");
		pane.getChildren().add(temp);
		
		TextField classCodeTF = new TextField();
		classCodeTF.setMaxWidth(500 / 2);
		pane.getChildren().add(classCodeTF);
		
		Label errorTF = new Label();
		pane.getChildren().add(errorTF);
		
		Button doneBtn = new Button("Done");
		doneBtn.setOnAction(f -> {
			
			addUserStat stat = getClassCode(classCodeTF.getText());
			
			switch(stat) {
			case normal:
				popupStage.close();
				getList();
				break;
			case notFound:
				errorTF.setText("There are no such thing lil bro");
				break;
			case duplicate:
				errorTF.setText("You've already added it bro");
				break;
			case error:
				errorTF.setText("How TF do you even manage to get here?");
				break;
			default:
				errorTF.setText("Arunno the problem, maybe the problem really is you");
				break;
			
			}
				
		});
		
		pane.getChildren().add(doneBtn);
		
		Scene scene = new Scene(pane);
		popupStage.setScene(scene);
		
		popupStage.showAndWait();
		
	}
	
	private enum addUserStat {
		
		normal,
		duplicate,
		notFound,
		error
		
	}
	
	
	public void goQuizFn(MouseEvent e) throws IOException { new controller().changeScene( e, "/quiz/Quiz.fxml"); }
	public void goQuizActionFn(ActionEvent e) throws IOException { new controller().changeScene( e, "/quiz/Quiz.fxml"); }

 	private addUserStat addClassUser(String className) {
		
		try {
			
			
			// create class name dir and db
			String createClassStudentDirString = "application/user/student/" + user.currentUser + "/classes/" + className + "/";
			File createFolder = new File(createClassStudentDirString);
			if(!createFolder.exists() && !createFolder.isDirectory())
				createFolder.mkdirs();
			
			String createClassStudentDBString = createClassStudentDirString + "/" + className + ".db";
			File createClassStudentDBFile = new File(createClassStudentDBString);
			createClassStudentDBFile.createNewFile();
			
			Connection studentClassConnection = DriverManager.getConnection("jdbc:sqlite:" + createClassStudentDBString);
			Statement studentClassStatement = studentClassConnection.createStatement();
			
			String addRequiredClassTable = "create table if not exists `user status`("
					+ "id integer primary key autoincrement not null,"
					+ "`quiz name` text not null,"
					+ "`score` integer not null"
					+ ")";
			studentClassStatement.execute(addRequiredClassTable);
			
			// create user class list
			String createClassDirString = "application/user/student/" + user.currentUser + "/classes/";
			File createClassDirFile = new File(createClassDirString);
			if(!createClassDirFile.exists() && !createClassDirFile.isDirectory())
				createClassDirFile.mkdirs();
			
			String createClassDBString = createClassDirString + "classes.db";
			File createClassDBFile = new File(createClassDBString);
			createClassDBFile.createNewFile();
			
			Connection userClassConnection = DriverManager.getConnection("jdbc:sqlite:" + createClassDBString);
			Statement addRequiredClassDB = userClassConnection.createStatement();
			
			String checkTable = "create table if not exists classes("
					+ "id integer primary key autoincrement not null,"
					+ "`class name` text not null"
					+ ")";
			addRequiredClassDB.execute(checkTable);
			
			// get user class list
			String getUserClassesString = "select `class name` from classes";
			Statement getUserClassesStatement = userClassConnection.createStatement();
			
			List<Map<String, Object>> list = Util.getList(getUserClassesStatement, getUserClassesString);
			
			for(Map<String, Object> i: list) {
				
				if(className.equals(i.get("class name")))
					return addUserStat.duplicate;
				
			}
			
			// adding the new class into the class list
			String addClassName = "insert into classes (`class name`) values (?)";
			PreparedStatement prepareClassName = userClassConnection.prepareStatement(addClassName);
			
			prepareClassName.setString(1, className);
			prepareClassName.executeUpdate();
			
			// adding the new student into the class
			String getClassDBString = "application/classes/" + className + "/" + className + ".db";
			Connection classesConnection = DriverManager.getConnection("jdbc:sqlite:" + getClassDBString);
			
			String addStudentClassName = "insert into student(`student name`) values (?)";
			PreparedStatement prepareStudentClassName = classesConnection.prepareStatement(addStudentClassName);
			
			prepareStudentClassName.setString(1, user.currentUser);
			prepareStudentClassName.execute();
			
			return addUserStat.normal;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return addUserStat.error;
		
	}
	
	private addUserStat getClassCode(String classCode) {
		
		try {
		
			Connection connection = DriverManager.getConnection("jdbc:sqlite:application/classes/classes.db");
			
			String getClassDataString = "select `class code`, `class name` from classes";
			Statement getClassDataStatement = connection.createStatement();
			
			List<Map<String, Object>> list = Util.getList(getClassDataStatement, getClassDataString);
			
			for(Map<String, Object> i : list) {
				
				System.out.println("Class code: " + i.get("class code"));
				
				if(classCode.equals(i.get("class code"))) {
					
					addUserStat stat = addClassUser((String) i.get("class name")); 
					
					if(stat == addUserStat.duplicate)
						return addUserStat.duplicate;
					else if (stat != addUserStat.normal){
					
						System.out.println("Naaaahhh bro wtf are you doing?");
						return addUserStat.error;
						
					}
					
				}
				
			}
			
			return addUserStat.normal;
			
			
			
			
			
		} catch(Exception e) {
			
			e.printStackTrace();
			return addUserStat.error;
			
		}
		
	}	

	public void userBtnFn(MouseEvent event) {

		new Util().userPopUp(event);

	}

	class classContainer extends AnchorPane {

		classContainer(String className) {

			this.setOnMouseClicked(event -> {
				try {

					user.currentClass = className;
					new controller().changeScene(event, "/classHomepage/student/Homepage.fxml");

				} catch (Exception exception) { exception.printStackTrace(); }
			});

			String backgroundStyle = "-fx-background-color: white;"
					+ "-fx-background-radius: 20;";
			
			this.setStyle(backgroundStyle);
			
			this.setMaxWidth(235);
			this.setMaxHeight(185);
			
			String upperStyle = "-fx-background-color: #00799A;"
					+ "-fx-background-radius: 20;";
			
			AnchorPane upper = new AnchorPane();
			upper.setStyle(upperStyle);
			upper.setLayoutX(0);
			upper.setLayoutY(0);
			
			upper.setPrefWidth(235);
			upper.setPrefHeight(133);
			
			HBox titleContainer = new HBox();
			
			titleContainer.setMinWidth(235);
			titleContainer.setAlignment(Pos.CENTER);
			titleContainer.setLayoutX(0);
			titleContainer.setLayoutY(148);
			
			Label titleT = new Label(className);
			titleT.setStyle("-fx-font-size: 20; -fx-text-fill: black;");
			
			titleContainer.getChildren().add(titleT);
			
			this.getChildren().add(upper);
			this.getChildren().add(titleContainer);
			
		}
		
	}

	class quizContainer extends AnchorPane {

		String quizName;

		quizContainer(String quizName) {

			this.quizName = quizName;

			this.setOnMouseClicked(event -> {
				try {

					user.currentQuiz = quizName;
					new controller().changeScene(event, "/practiceQuiz/PracticeQuiz.fxml");

				} catch (Exception exception) { exception.printStackTrace(); }
			});

			String backgroundStyle = "-fx-background-color: white;"
					+ "-fx-background-radius: 20;";

			this.setStyle(backgroundStyle);

			this.setMaxWidth(235);
			this.setMaxHeight(185);

			String upperStyle = "-fx-background-color: #00799A;"
					+ "-fx-background-radius: 20;";

			AnchorPane upper = new AnchorPane();
			upper.setStyle(upperStyle);
			upper.setLayoutX(0);
			upper.setLayoutY(0);

			upper.setPrefWidth(235);
			upper.setPrefHeight(133);

			Image img = new Image("file:src/homepage/rsc/delete.png");
			ImageView imgView = new ImageView(img);
			imgView.setFitHeight(20);
			imgView.setFitWidth(20);

			Button deleteBtn = new Button("");
			deleteBtn.setStyle("-fx-background-color: transparent;");
			deleteBtn.setGraphic(imgView);
			deleteBtn.setLayoutX(200);
			deleteBtn.setLayoutY(10);
			deleteBtn.setOnAction(event -> {
				deleteBtnFn(event);
			});

			upper.getChildren().add(deleteBtn);

			HBox titleContainer = new HBox();

			titleContainer.setMinWidth(235);
			titleContainer.setAlignment(Pos.CENTER);
			titleContainer.setLayoutX(0);
			titleContainer.setLayoutY(148);

			Label titleT = new Label(quizName);
			titleT.setStyle("-fx-font-size: 20; -fx-text-fill: black;");

			titleContainer.getChildren().add(titleT);

			this.getChildren().add(upper);
			this.getChildren().add(titleContainer);

		}

		void deleteBtnFn(ActionEvent event) {

			try {

				Connection quizConnection = Util.getStudentQuizConnectionDB();

				String deleteQuizString = "delete from quiz where `quiz name` = ?";
				PreparedStatement deleteQuizStatement = quizConnection.prepareStatement(deleteQuizString);
				deleteQuizStatement.setString(1, quizName);
				deleteQuizStatement.executeUpdate();

				String dropTableQuizString = "drop table if exists `" + quizName + "`";
				Statement dropTableQuizStatement = quizConnection.createStatement();
				dropTableQuizStatement.execute(dropTableQuizString);

				String dropTableQuizListString = "drop table if exists `" +  quizName + "list`";
				Statement dropTableQuizListStatement = quizConnection.createStatement();
				dropTableQuizListStatement.execute(dropTableQuizListString);

				refreshDeckList();

			} catch (Exception e) { e.printStackTrace(); }

		}

	}

}