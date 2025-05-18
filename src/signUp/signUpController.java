package signUp;

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
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import util.Init;
import util.controller;

public class signUpController implements Initializable{
	
	@FXML
	Button studentBtn, teacherBtn, signUpBtn, loginBtn;
	
	@FXML
	TextField firstNameTF, middleNameTF, lastNameTF, usernameTF;
	
	@FXML
	PasswordField passwordPF, confirmPasswordPF;
	
	@FXML
	HBox problemPromptBox;
	
	int isTeacher = 0;
	
	Statement statement = null;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		studentBtn.getStyleClass().add("isTeacherChoose");
		problemPromptBox.getStyleClass().add("problemPrompt");
		
	}
	
	//start of login button function
	public void loginBtnFn(ActionEvent e) throws IOException {
		
		new controller().changeScene(e, "/login/Login.fxml");
		
	}
	
	// start of student button function
	public void studentBtnFn(ActionEvent e) {
		
		isTeacher = 0;
		
		teacherBtn.getStyleClass().removeAll("isTeacherChoose");
		studentBtn.getStyleClass().add("isTeacherChoose");
		
	}
	
	// end of student button function and start of teacher button function
	
	public void teacherBtnFn(ActionEvent e) {
		
		isTeacher = 1;
		
		studentBtn.getStyleClass().removeAll("isTeacherChoose");
		teacherBtn.getStyleClass().add("isTeacherChoose");
		
	}
	
	// end of teacher button function and start of sign up function
	
	Thread thread = new Thread(() -> {
		
		try {
			
			Thread.sleep(2000);
			
			
		} catch (InterruptedException e) { e.printStackTrace(); }
		
		
		Platform.runLater(() -> {
			 problemPromptBox.getChildren().clear();
        });
		
	});
	
	private enum duplicateStats {
		normal,
		name,
		username
	};
	
	private void checkRequiredFiles(boolean isTeacher) {
		
		try {
		
			String applicationDirString = "application/user/";
			File createUserDir = new File(applicationDirString);
			if(!createUserDir.exists() && !createUserDir.isDirectory())
				createUserDir.mkdirs();
			
			String applicationDBString = applicationDirString + "user.db";
			File createUserDB = new File(applicationDBString);
			createUserDB.createNewFile();
			
			Connection applicationConnection = DriverManager.getConnection("jdbc:sqlite:" + applicationDBString);
			Statement applicationStatement = applicationConnection.createStatement();
			
			String createUserTableString = "create table if not exists user("
					+ "id integer primary key autoincrement not null,"
					+ "username text not null,"
					+ "password text not null,"
					+ "isTeacher integer not null,"
					+ "`last name` text not null,"
					+ "`first name` text not null,"
					+ "`middle name` text not null"
					+ ")";
			applicationStatement.execute(createUserTableString);
			
			String userDirString = "application/user/" + ((isTeacher) ? "teacher/" : "student/") + usernameTF.getText() + "/";
			File userDirFile = new File(userDirString);
			if(!userDirFile.exists() && !userDirFile.isDirectory())
				userDirFile.mkdirs();
			
			String userDBString = userDirString + usernameTF.getText() + ".db";
			File userDBFile = new File(userDBString);
			userDBFile.createNewFile();
			
			Connection userConnection = DriverManager.getConnection("jdbc:sqlite:" + userDBString);
			Statement userStatement = userConnection.createStatement();
			
			String createClassTable = "create table if not exists classes("
					+ "id integer primary key autoincrement not null,"
					+ "`class name` text not null"
					+ ")";
			userStatement.execute(createClassTable);
			
			if(isTeacher) return;
			
			String createDeckTable = "create table if not exists deck("
					+ "id integer primary key autoincrement not null,"
					+ "`deck name` text not null"
					+ ")";
			userStatement.execute(createDeckTable);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void signUpFn(ActionEvent e) throws SQLException, IOException {
		
		if(isTeacher == 1) checkRequiredFiles(true);
		else checkRequiredFiles(false);
		
		switch(checkDuplicate() ) {
		case name:
			duplicatedName();
			return;
		case username:
			duplicatedUsername();
			return;
		default:
			break;
		}
		
		
		if(isFillUpTF())
			return;
			
		if(passwordCompare())
			return;
			
		String sql = "insert into user (`first name`, `middle name`, `last name`, username, password, isTeacher) values (?, ?, ?, ?, ?, ?)";
		
		try(Connection connection = DriverManager.getConnection("jdbc:sqlite:application/user/user.db")) {
			
			PreparedStatement pstm = connection.prepareStatement(sql);
			
			pstm.setString(1, firstNameTF.getText());
			pstm.setString(2, middleNameTF.getText());
			pstm.setString(3, lastNameTF.getText());
			pstm.setString(4, usernameTF.getText());
			pstm.setString(5, passwordPF.getText());
			pstm.setInt(6, isTeacher);
			
			pstm.executeUpdate();
			
		} catch(Exception f) { f.printStackTrace(); }
		
		new Init();
		
		new controller().changeScene(e, "/login/Login.fxml");
		
	}
	
	
	private boolean passwordCompare() {
		
		if(passwordPF.getText().equals(confirmPasswordPF.getText())) return false;
		
		else {
			
			Label label = new Label("no, you're wrong");
			label.getStyleClass().add("promptProblem");
			
			problemPromptBox.getChildren().add(label);
			
			thread.start();
			
			return true;
		}
	}
	
	private void duplicatedUsername() {
		
		Label label = new Label("Account Already Existing");
		label.getStyleClass().add("promptProblem");
		problemPromptBox.getChildren().add(label);
		
		thread.start();
	}
	
	private void duplicatedName() {
		
		Label label = new Label("Account Already Existing");
		label.getStyleClass().add("promptProblem");
		problemPromptBox.getChildren().add(label);
		
		thread.start();
		
	}
	
	private duplicateStats checkDuplicate() throws SQLException, IOException {
		
		duplicateStats stat = duplicateStats.normal;
		
		Connection connection = DriverManager.getConnection("jdbc:sqlite:application/user/user.db");
		statement = connection.createStatement();
		
		ResultSet rs = statement.executeQuery("select `first name`, `middle name`, `last name`, username from user");
		ResultSetMetaData rsmd = rs.getMetaData();
		
		List<Map<String, Object>> userData = new ArrayList();
		
		while(rs.next()) {
			
			Map<String, Object> temp = new HashMap<>();
			
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				
				temp.put(rsmd.getColumnName(i), rs.getObject(i));
				
			}
			
			userData.add(temp);
			
		}
		
		for(Map<String, Object> i: userData)
			System.out.println(i);
		
		for(Map<String, Object> i: userData) {
			
			String fullName = firstNameTF.getText() + middleNameTF.getText() + lastNameTF.getText();
			String testName = (String) i.get("first name") + i.get("middle name") + i.get("last name");
			
			if(fullName.toLowerCase().equals(testName.toLowerCase()))
				return duplicateStats.name;
			
			if(usernameTF.getText().equals(i.get("username")))
				return duplicateStats.username;
			
		}
		
		return stat;
	}
	
	private boolean isFillUpTF() {
		
		boolean fillUp = false;
		
		TextField TF[] = {firstNameTF, middleNameTF, lastNameTF, usernameTF};
		
		for(TextField i: TF) {
		
			if(i.getText().isEmpty()) {
			
				fillUp = true;
				fillMeUpTF(i);
			
			}
				
		
		}
		
		PasswordField PF[] = {passwordPF, confirmPasswordPF};
		
		for(PasswordField i: PF) {
			if(i.getText().isEmpty()) {
				
				fillUp = true;
				fillMeUpTF(i);
			
			}
		}
		
		return fillUp;
		
	}
	
	private void fillMeUpTF(TextField text) {
		
		String formerPrompt = text.getPromptText();
		
		text.setPromptText("Please fill me");
		text.getStyleClass().add("fillMeUp");
		
		Thread thread = new Thread(() -> {
			
			try {
				
				Thread.sleep(2000);
				
				text.setPromptText(formerPrompt);
		        text.getStyleClass().removeAll("fillMeUp");
		        text.getStyleClass().add("textFieldStyle");
				
			} catch (InterruptedException e) { e.printStackTrace(); }
			
			
		});
		
		thread.start();
	}
	// end of start function
}
