package homepage.lib;

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
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import homepage.teacher.homepageController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.Util;
import util.user;

public class popUpController implements Initializable{

	@FXML
	Label errorText;
	
	@FXML
	TextField classNameTF;
	
	@FXML
	Button doneBtn;
	
	public void doneBtnFn(ActionEvent e) throws SQLException, IOException {
		
		errorText.setStyle("-fx-text-fill: red;");
		
		Connection connection = DriverManager.getConnection("jdbc:sqlite:application/classes/classes.db");
		
		String getDataClassString = "select `class name`, `class code` from classes";
		Statement getDataClassStatement = connection.createStatement();
		
		List<Map<String, Object>> list = Util.getList(getDataClassStatement, getDataClassString);
		
		if(classNameTF.getText().isEmpty()) {
			
			errorText.setText("Class Name is empty");
			return;
			
		}
		
		for(Map<String, Object> i: list) {
			
			if(classNameTF.getText().equals(i.get("class name"))) {
				
				errorText.setText("Class name have a duplicate name");
				return;
				
			}
			
		}
		
		String classCode = "";
		
		String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		
		while(true) {
			
			Random random = new Random();
	        StringBuilder sb = new StringBuilder(5);
	        
	        boolean notDuplicate = false;
	        
	        for (int i = 0; i < 5; i++) {
	            int index = random.nextInt(CHARACTERS.length());
	            sb.append(CHARACTERS.charAt(index));
	        }
			
	        for(Map<String, Object> i : list) {
	        	
	        	if(i.get("class code").equals(sb.toString())) {
	        		System.out.println(i.get("class code") + ": " + sb.toString());
	        		notDuplicate = true;
	        		break;
	        	}
	        	else {
	        		
	        		classCode = sb.toString();
	        		notDuplicate = false;
	        		
	        	}
	        	
	        }
	        
	        if(list.isEmpty() || !notDuplicate) {
	        	
	        	classCode = sb.toString();
	        	break;
	        	
	        }
	        	
	        
		}
		
		String sqlInsertStatement = "insert into classes (`class name`, `class code`) values (?, ?)";
		PreparedStatement prepStatement = connection.prepareStatement(sqlInsertStatement);
		
		prepStatement.setString(1, classNameTF.getText());
		prepStatement.setString(2, classCode);
		
		prepStatement.executeUpdate();
		
		System.out.println("Class Name: " + classNameTF.getText() + "\nClass Code: " + classCode);
		
		// class creation
		File createDir = new File("application/classes/" + classNameTF.getText());
		
		boolean isExist = createDir.exists() && createDir.isDirectory();
		
		if(!isExist) {
			
			createDir.mkdirs();
			
			File createDB = new File(createDir + "/" + classNameTF.getText() + ".db");
			createDB.createNewFile();
			
			connection = DriverManager.getConnection("jdbc:sqlite:" + createDB);
			Statement createTableDeckStatement = connection.createStatement();
			
			String createTable = "create table if not exists deck("
					+ "id integer primary key autoincrement,"
					+ "deck name text,"
					+ "isQuiz int"
					+ "description text"
					+ ")";
			
			createTableDeckStatement.execute(createTable);
		}
		
		// teacher class list creation
		String userDir = "application/user/teacher/" + user.currentUser + "/";
		String getUserDB = userDir + "/" + user.currentUser + ".db";
		
		connection = DriverManager.getConnection("jdbc:sqlite:" + getUserDB);
		Statement createTableClassesStatement = connection.createStatement();
		String createTable = "create table if not exists classes("
				+ "id integer primary key autoincrement,"
				+ "`class name` text"
				+ ")";
		createTableClassesStatement.execute(createTable);
		
		
		sqlInsertStatement = "insert into classes (`class name`) values (?)";
		PreparedStatement prepareStatement = connection.prepareStatement(sqlInsertStatement);
		prepareStatement.setString(1, classNameTF.getText());
		prepareStatement.executeUpdate();
		
		File userClassDir = new File(userDir + "/" + classNameTF.getText() + "/");
		isExist = userClassDir.exists() && userClassDir.isDirectory();
		
		if(!isExist)
			userClassDir.mkdirs();
		
		File createClassDB = new File(userClassDir + "/" + classNameTF.getText() + ".db");
		createClassDB.createNewFile();
		
		connection = DriverManager.getConnection("jdbc:sqlite:" + createClassDB);
		Statement createTableDeckStatement = connection.createStatement();
		createTable = "create table if not exists deck("
				+ "id integer primary key autoincrement,"
				+ "deck name text,"
				+ "Score int"
				+ ")";
		createTableDeckStatement.execute(createTable);
		
		Stage stage = (Stage)((Node)((EventObject)e).getSource()).getScene().getWindow();
		stage.close();
		
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

	
}
