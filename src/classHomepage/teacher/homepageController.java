package classHomepage.teacher;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import classHomepage.teacher.classList.classListController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import quizCard.quizCardController;
import util.Util;
import util.controller;
import util.user;

public class homepageController implements Initializable{

	@FXML
	VBox listContainer, emptyContainer, studentContainerBtn;

	@FXML
	HBox announcementContainer;

	@FXML
	Label announcementT, classNameT, ssrT;

	@FXML
	Button studentListBtn, classRecordBtn;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		getClassInfo();
		getAnnouncementList();
		
	}

	public void studentListBtnFn(ActionEvent event) throws IOException {

		Stage mainStage = new controller().getStage(event);

		Stage miniStage = new Stage();
		miniStage.initModality(Modality.APPLICATION_MODAL);
		miniStage.initOwner(mainStage);

		// temporary will be change this soon
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/classHomepage/teacher/classList/ClassList.fxml"));
		classListController controller = loader.getController();
		Parent root = loader.load();

		miniStage.setScene(new Scene(root));
		miniStage.showAndWait();


	}

	private void getClassInfo() {
		
		try {
			
			String getClassString = "application/classes/classes.db";
			Connection classConnection = DriverManager.getConnection("jdbc:sqlite:" + getClassString);
			
			String getClassInfoString = "select * from classes where `class name`= '" + user.currentClass + "'";
			Statement getClassInfoStatement = classConnection.createStatement();
			
			List<Map<String, Object>> classInfoList = Util.getList(getClassInfoStatement, getClassInfoString);
			Map<String, Object> classInfo = classInfoList.get(0);
			
			classNameT.setText((String) classInfo.get("class name"));
			
			String prepareSSRT = (String) classInfo.get("section") + "/" + (String) classInfo.get("subject") + "/" + (String) classInfo.get("room");
			ssrT.setText(prepareSSRT);
			
			classConnection.close();
			
		} catch (Exception exception) { exception.printStackTrace(); }
		
	}
	
	List<Map<String, Object>> announcementList = new ArrayList<>();
	
	public void createQuizBtnFn(MouseEvent e) throws IOException {
		
		new controller().changeScene(e, "/quiz/Quiz.fxml");
		
	}
	
	private void ifAnnouncement() {

		listContainer.getChildren().clear();

		announcementContainer.setOnMouseClicked(e -> {
			
			makeAnnouncement(e);
			
		});


		listContainer.getChildren().add(announcementContainer);
		
		for(Map<String, Object> i: announcementList) {
			
			if(((Integer)i.get("isQuiz")) == 1) listContainer.getChildren().add(new quizTemplate(i));
			else listContainer.getChildren().add(new announcementTemplate(i));
			
		}

	}
	
	private void getAnnouncementList() {
		
		try {
		
			String getClassDB = "application/classes/" + user.currentClass + "/" + user.currentClass + ".db";
			
			Connection classConnection = DriverManager.getConnection("jdbc:sqlite:" + getClassDB);
			Statement classStatement = classConnection.createStatement();
			
			String getAnnouncementString = "select * from classes";
			
			announcementList = Util.getList(classStatement, getAnnouncementString);
			ifAnnouncement();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void makeAnnouncement(MouseEvent e) {
		
		try {
			
			Stage mainStage = (Stage)((Node)((EventObject)e).getSource()).getScene().getWindow();
			
			Stage miniStage = new Stage();
			miniStage.initModality(Modality.APPLICATION_MODAL);
			miniStage.initOwner(mainStage);
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/classHomepage/teacher/PopUp.fxml"));
			Parent miniRoot = loader.load();

			popUpController controller = loader.getController();
			controller.mainStage = mainStage;

			Scene scene = new Scene(miniRoot);
			miniStage.setScene(scene);
			miniStage.setOnHidden(f -> {
				getAnnouncementList();
			});
			
			miniStage.showAndWait();
		
		} catch (Exception f) {
			f.printStackTrace();
		}
	}
	
	public void backBtnFn(ActionEvent event) throws IOException {
		
		user.currentClass = null;
		new controller().changeScene(event, "/homepage/teacher/Homepage.fxml");
		
	}
	
	private class quizTemplate extends VBox {
		
		private String announcementStyle = "-fx-background-color: #5A95BA; -fx-background-radius: 20;";
		
		Map<String, Object> announcementGlobal;
		
		quizTemplate(Map<String, Object> announcement) {
			
			announcementGlobal = announcement;
			
			this.setPadding(new Insets(15, 15, 0, 15));
			this.setMinHeight(105);
			this.setPrefWidth(654);
			this.setStyle(announcementStyle);
			this.setSpacing(5);
			
			GridPane topPane = new GridPane();
			topPane.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(80); }});
			topPane.getColumnConstraints().add(new ColumnConstraints() {{ setPercentWidth(20); }});
			
			String quizTitleString = "Quiz Title: " + announcement.get("announcement");
			Label quizTitleT = new Label(quizTitleString);
			quizTitleT.setFont(new Font("Inter", 20));
			quizTitleT.setStyle("-fx-text-fill: white;");
			GridPane.setHalignment(quizTitleT, HPos.LEFT);
			topPane.add(quizTitleT, 0, 0);
			
			// Image img = new Image("file:src/quizCard/rsc/delete.png");
			Image img = new Image("file:src/classHomepage/rsc/delete.png");
			ImageView imgView = new ImageView(img);
			imgView.setOnMouseClicked(event -> deleteBtn(event));
			
			imgView.setFitWidth(24);
			imgView.setFitHeight(24);
			
			GridPane.setHalignment(imgView, HPos.RIGHT);
			topPane.add(imgView, 1, 0);
			topPane.setPadding(new Insets(0, 25, 0, 0));
			
			HBox bottomPane = new HBox();
			bottomPane.setAlignment(Pos.CENTER_RIGHT);
			bottomPane.setSpacing(25);

			String buttonStyle = "-fx-background-color: #00799A; -fx-background-radius: 20; -fx-text-fill: white;";
			Button editBtn = new Button("Edit");
			editBtn.setFont(new Font("Inter", 20));
			editBtn.setStyle(buttonStyle);
			editBtn.setOnAction(event -> editBtn(event));
			
			Button studentWorkBtn = new Button("Students Work");
			studentWorkBtn.setFont(new Font("Inter", 20));
			studentWorkBtn.setStyle(buttonStyle);
			studentWorkBtn.setOnAction(event -> {
				user.currentQuiz = (String) announcementGlobal.get("announcement");
				new controller().changeScene(event, "/classHomepage/teacher/studentWork/StudentWork.fxml");
			});


			bottomPane.getChildren().addAll(editBtn, studentWorkBtn);
			this.getChildren().addAll(topPane, bottomPane);
			
		}
		
		private void editBtn(ActionEvent event) {
			
			try {
				

				String tableName = (String) announcementGlobal.get("announcement");

				FXMLLoader loader = new FXMLLoader(getClass().getResource("/quizCard/QuizCard.fxml"));
				Parent root = loader.load();
				
				quizCardController controller = loader.getController();
				controller.editQuizFn(tableName);
				
				Stage stage = new controller().getStage(event);
				stage.setScene(new Scene(root));
				stage.show();
			
			} catch(Exception exception) { exception.printStackTrace(); }
			
		}
		
		private void deleteBtn(MouseEvent event) {
			
			try {
				
				int id = (Integer) announcementGlobal.get("id");
				String announcement = (String) announcementGlobal.get("announcement");
				
				String classNameString = "application/classes/" + user.currentClass + "/" + user.currentClass + ".db";
				Connection classNameConnection = DriverManager.getConnection("jdbc:sqlite:" + classNameString);
			
				Statement dropTableStatement = classNameConnection.createStatement();
				String dropTableString = "drop table if exists " + announcement;
				dropTableStatement.execute(dropTableString);

				String dropTableListString = "drop table if exists " + announcement + "List";
				Statement dropTableListStatement = classNameConnection.createStatement();
				dropTableListStatement.execute(dropTableListString);

				String deleteAnnouncementNameString = "delete from classes where id = '" + id + "'";
				Statement deleteAnnouncementNameStatement = classNameConnection.createStatement();
				
				deleteAnnouncementNameStatement.execute(deleteAnnouncementNameString);
				
				getAnnouncementList();
				
			} catch(Exception exception) { exception.printStackTrace(); }
			
		}
		
	}

	// ANNOUNCEMENT TEMPLATE

	private class announcementTemplate extends VBox {
		
		private String announcementStyle = "-fx-background-color: #5A95BA; -fx-background-radius: 20;";
		
		announcementTemplate(Map<String, Object> list) {
			
			this.setAlignment(Pos.TOP_CENTER);
			this.setMinHeight(167);
			this.setPrefWidth(654);
			this.setStyle(announcementStyle);
			this.setPadding(new Insets(25, 15, 25, 15));
			this.setSpacing(25);
			this.setFillWidth(true);

			HBox topPane = new HBox();
			topPane.setSpacing(25);

			Label accountNameT = new Label("ANNOUNCEMENT");
			accountNameT.setStyle("-fx-font-size: 24px");

			Region spacer = new Region();
			spacer.setPrefWidth(250);
			topPane.getChildren().addAll(accountNameT, spacer);

			Button editBtn = new Button("Edit");
			editBtn.setStyle("-fx-font-size: 24px; -fx-background-color: #00799A; -fx-background-radius: 20; -fx-text-fill: white;");
			editBtn.setOnAction(f -> {
				
				editBtnFn(list);
				
			});

			Button deleteBtn = new Button("Delete");
			deleteBtn.setStyle("-fx-font-size: 24px; -fx-background-color: #00799A; -fx-background-radius: 20; -fx-text-fill: white;");
			deleteBtn.setOnAction(f -> {
				
				deleteBtnFn((Integer) list.get("id"));
				
			});

			topPane.getChildren().addAll(editBtn, deleteBtn);
			this.getChildren().add(topPane);

			/*

				Bottom Pane

			 */

			String announcementTStyle = "-fx-background-color: white; -fx-background-radius: 20; -fx-text-fill: black; -fx-font-size: 20px;";
			Label announcementT = new Label((String) list.get("announcement description"));
			announcementT.setWrapText(true);
			announcementT.setStyle(announcementTStyle);
			announcementT.setPadding(new Insets(15, 20, 15, 20));
			
			this.getChildren().add(announcementT);
		}
		
		private void editBtnFn(Map<String, Object> list) {
			
			try {
			//Stage mainStage = (Stage)((Node)((EventObject)e).getSource()).getScene().getWindow();
			
				Stage mainStage = (Stage) this.getScene().getWindow();
				
				Stage miniStage = new Stage();
				miniStage.initModality(Modality.APPLICATION_MODAL);
				miniStage.initOwner(mainStage);
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/classHomepage/teacher/PopUp.fxml"));
				Parent miniRoot = loader.load();
				
				popUpController controller = loader.getController();
				controller.setEditPane(list, controller.editStat.edit);
				
				Scene scene = new Scene(miniRoot);
				miniStage.setScene(scene);
				
				miniStage.setOnHidden(f -> { getAnnouncementList(); });
				
				miniStage.showAndWait();
			
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
		private void deleteBtnFn(Integer id) {
			
			try {
				
				String getClassString = "application/classes/" + user.currentClass + "/" + user.currentClass + ".db";
				Connection userConnection = DriverManager.getConnection("jdbc:sqlite:" + getClassString);
				Statement userStatement = userConnection.createStatement();
				
				String deleteString = "delete from classes where id = " + id;
				int rowDeleted = userStatement.executeUpdate(deleteString);
				
				if(rowDeleted > 0) {
					
					System.out.println("Succesfully deleted!");
					getAnnouncementList();
					
				}
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}
	
	
	
}
