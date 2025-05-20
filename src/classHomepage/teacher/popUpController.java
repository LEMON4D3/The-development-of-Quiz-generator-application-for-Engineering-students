package classHomepage.teacher;

import java.io.IOException;
import java.net.URL;
import java.security.cert.PolicyNode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.controller;
import util.user;

public class popUpController {

	@FXML
	TextArea postTA;

	@FXML
	ComboBox<String> categoryCombo;

	public Map<String, Object> list;
	public Stage mainStage;

	editType editStat = null;

	/*

	*/
	public void initController(Stage mainStage) {

		this.mainStage = mainStage;
		new initComboBox();

	}

	class initComboBox{

		initComboBox() {

			setComboBox();

		}

		public void setComboBox() {

			ObservableList<String> categoryList = FXCollections.observableArrayList( "Announcement", "Create quiz", "Coding Exercise" );
			categoryCombo.setItems(categoryList);
			categoryCombo.setValue("Announcement");

			categoryCombo.setOnAction(event -> {
				try {

					Stage miniStage = new controller().getStage(event);

					if (categoryCombo.getValue().equals("Create quiz")) {

						user.userQuizOption = user.quizOption.New;


						miniStage.close();

						Parent root = FXMLLoader.load(getClass().getResource("/quiz/Quiz.fxml"));
						mainStage.setScene(new Scene(root));
						mainStage.show();


					} else if(categoryCombo.getValue().equals("Coding Exercise")) {

						miniStage.close();

						Parent root = FXMLLoader.load(getClass().getResource("/compiler/create/Compiler.fxml"));
						mainStage.setScene(new Scene(root));
						mainStage.show();

					}

				} catch (Exception e) {e.printStackTrace();}

			});

		}

	}

	enum editType {
		
		edit,
		write
		
	}

	@FXML
	GridPane topPane;


	public void setEditPane(Map<String, Object> list, editType editEnum) {


		topPane.getChildren().remove(categoryCombo);
		this.list = list;
		postTA.setText((String) list.get("announcement description"));
		editStat = editEnum;
		
	}
	
	public void saveBtnFn(ActionEvent e) {
		
		if(editStat != null)
			editAnnouncement();
		else
			writeAnnouncement();
		
		Stage mainStage = (Stage)((Node)((EventObject)e).getSource()).getScene().getWindow();
		mainStage.close();
		
	}
	
	private void editAnnouncement() {
		
		try {
			
			String getClassString = "application/classes/" + user.currentClass + "/" + user.currentClass + ".db";
			Connection getClassConnection = DriverManager.getConnection("jdbc:sqlite:" + getClassString);
			
			String updateAnnouncementString = "update classes set `announcement description` = ? where id = " + (Integer) list.get("id");
			PreparedStatement prepareUpdate = getClassConnection.prepareStatement(updateAnnouncementString);
			
			prepareUpdate.setString(1, postTA.getText());
			
			int rowUpdate = prepareUpdate.executeUpdate();
			if(rowUpdate > 0) System.out.println("Done update the row");
			
			
		} catch (Exception e) { e.printStackTrace(); }
		
	}
	
	private void writeAnnouncement() {
		
		try {
			
			String getClassString = "application/classes/" + user.currentClass + "/" + user.currentClass + ".db";
			Connection getClassConnection = DriverManager.getConnection("jdbc:sqlite:" + getClassString);
			
			String insertClassString = "insert into classes(announcement, `announcement description`, isQuiz) values (?, ?, ?)";
			PreparedStatement prepareStatementInsert = getClassConnection.prepareStatement(insertClassString);
			
			prepareStatementInsert.setString(1, user.currentUser);
			prepareStatementInsert.setString(2, postTA.getText());
			prepareStatementInsert.setInt(3, 0);
			
			prepareStatementInsert.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void cancelBtnFn(ActionEvent e) throws IOException {
		
		Stage mainStage = (Stage)((Node)((EventObject)e).getSource()).getScene().getWindow();
		mainStage.close();
		
	}
	
}
