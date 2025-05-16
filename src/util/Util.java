package util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.fxml.userPopUp;

public class Util {

	public static List<Map<String, Object>> getList(Statement statement, String statementString) throws SQLException {
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		ResultSet rs = statement.executeQuery(statementString);
		ResultSetMetaData rsmd = rs.getMetaData();
		
		while(rs.next()) {
			
			Map<String, Object> temp = new HashMap();
			
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				
				temp.put(rsmd.getColumnName(i), rs.getObject(i));
				
			}
			
			list.add(temp);
			
		}
		
		return list;
	}
	
	public static boolean compareListString(List<Map<String, Object>> list, String listObject, String compare) {
		
		for(Map<String, Object> i: list) {
			if(((String) i.get(listObject)).equals(compare))
				return false;
		}
		
		return true;
	}

	public <T> void userPopUp(T event) {

		try {

			Stage mainStage = new controller().getStage(event);

			Stage miniStage = new Stage();
			miniStage.initModality(Modality.APPLICATION_MODAL);
			miniStage.initOwner(mainStage);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/util/fxml/userPopUp.fxml"));
			Parent root = loader.load();

			userPopUp controller = loader.getController();
			controller.setMainStage(mainStage);

			miniStage.setScene(new Scene(root));
			miniStage.showAndWait();

		} catch (Exception exception) { exception.printStackTrace(); }

	}

	/*
	 ==================================== class database function section ====================================
	 - getQuizStudentResultDB is use for getting the quiz result from student
	  	example:
	  	-> getQuizStudentResultDB("tableName") will return user quiz result

	  - getQuizOrAnnouncementClassListDB is use for getting the quiz from class database
	  	example:
	  	-> getQuizOrAnnouncementClassListDB("tableName", false) will return tableName database list
		-> getQuizOrAnnouncementClassListDB("tableName", false) will return announcement list database

	  - getClassConnectionDB is use for getting the class connection database
		example:
	  	-> getClassConnectionDB() will return tableName database
	*/

	public static List<Map<String, Object>> getQuizStudentResultDB(String tableName) {

		try {

			Connection classConnection = getClassConnectionDB();

			String getQuizListString = "select * from `" + user.currentQuiz + "List`";
			Statement getQuizListStatement = classConnection.createStatement();

			return getList(getQuizListStatement, getQuizListString);

		} catch (Exception exception) { exception.printStackTrace(); }

        return null;
    }

	public static List<Map<String, Object>> getQuizOrAnnouncementClassListDB(String tableName, boolean isAnnouncement) {

		try {

			String getDBString = "application/classes/" + user.currentClass + "/" + user.currentClass + ".db";
			Connection connection = DriverManager.getConnection("jdbc:sqlite:" + getDBString);

			Statement getAnnouncementStatement = connection.createStatement();
			String getAnnouncementString = "select * from classes";
			if(isAnnouncement) return getList(getAnnouncementStatement, getAnnouncementString);

			String getTableString = "select * from `" + tableName + "`";
			Statement tableStatement = connection.createStatement();

			String appointment = null;

			return getList(tableStatement, getTableString);

		} catch (Exception exception) { exception.printStackTrace(); }

		return null;

	}

	public static Connection getClassConnectionDB() {

		try {

			String getDBString = "application/classes/" + user.currentClass + "/" + user.currentClass + ".db";
			return DriverManager.getConnection("jdbc:sqlite:" + getDBString);


		} catch (Exception exception) { exception.printStackTrace(); }

		return null;

	}

	/*
	===================================== student database function section ====================================
	- 	getStudentListDB is use for getting the student database
		example:
	  	-> getStudentListDB(true) will return student class
	  	-> getStudentListDB(false) will return student quiz deck

  	- 	getStudentConnectionDB is use for getting the student connection
		example:
	  	-> getStudentConnectionDB() will return student connection
	*/

	public static Connection getStudentQuizConnectionDB() {
		try {

			String getQuizDBString = "application/user/student/" + user.currentUser + "/quiz/quiz.db";
			return DriverManager.getConnection("jdbc:sqlite:" + getQuizDBString);

		} catch (Exception exception) { exception.printStackTrace(); }

		return null;
	}

	public static List<Map<String, Object>> getStudentQuizListDB(boolean isList) {
		try {

			String getQuizDBString = "";
			getQuizDBString = "application/user/student/" + user.currentUser + "/quiz/quiz.db";

			Connection quizConnection = DriverManager.getConnection("jdbc:sqlite:" + getQuizDBString);

			if(isList) {
				String getQuizListString = "select * from `" + user.currentQuiz + "List`";
				Statement quizListStatement = quizConnection.createStatement();

				return getList(quizListStatement, getQuizListString);
			}

			String getQuizTableString = "select * from `" + user.currentQuiz + "`";
			Statement quizTableStatement = quizConnection.createStatement();

			return getList(quizTableStatement, getQuizTableString);

		} catch (Exception exception) { exception.printStackTrace(); }
        return null;

    }

	public static List<Map<String, Object>> getStudentListDB(boolean getClasses) {


		try {

			String getDBString = "application/user/student/" + user.currentUser + "/" + user.currentUser + ".db";
			Connection dbConnection = DriverManager.getConnection("jdbc:sqlite:" + getDBString);

			String getTableString = (getClasses) ? "select * from classes" : "select * from decks";
			Statement tableStatement = dbConnection.createStatement();

			return getList(tableStatement, getTableString);

		} catch (Exception exception) { exception.printStackTrace(); }

		return null;

	}

	public static Connection getStudentConnectionDB() {
		try {

			String getDBString = "application/user/student/" + user.currentUser + "/" + user.currentUser + ".db";
			return DriverManager.getConnection("jdbc:Sqlite:" + getDBString);

		} catch (Exception exception) { exception.printStackTrace(); }

        return null;
    }

	/*
	  ===================================== teacher database function section ====================================
	-	getTeacherClassListDB is use for getting the teacher database
		example:
	  	-> getTeacherClassListDB() will return teacher class

	-	getTeacherConnectionDB is use for getting the teacher connection
		example:
	  	-> getTeacherConnectionDB() will return teacher connection
	*/

	public static List<Map<String, Object>> getTeacherClassListDB() {

		try {

			String getDBString = "application/user/teacher/" + user.currentUser + "/" + user.currentUser + ".db";
			Connection dbConnection = DriverManager.getConnection("jdbc:Sqlite:" + getDBString);

			String getTableString = "select * from classes";
			Statement tableStatement = dbConnection.createStatement();

			return getList(tableStatement, getTableString);

		} catch (Exception exception) { exception.printStackTrace(); }

		return null;
	}

	public static Connection getTeacherConnectionDB() {

		try {

			String getDBString = "application/user/teacher/" + user.currentUser + "/" + user.currentUser + ".db";
			return DriverManager.getConnection("jdbc:Sqlite:" + getDBString);

		} catch (Exception exception) { exception.printStackTrace(); }

        return null;
    }

	/*
	======================== data class database function section ==========================
	-	getClassOrUserListDB is use for getting the class and user database
		example:
	  	-> getClassOrUserListDB(dataClass.Class) will return registered class list database
	  	-> getClassOrUserListDB(dataClass.User) will return registered user list database
	-	getClassOrUserConnectionDB is use for getting the class and user connection
		example:
	  	-> getClassOrUserConnectionDB(dataClass.Class) will return class connection
	  	-> getClassOrUserConnectionDB(dataClass.User) will return user connection
	*/

	public enum dataClass{ Class, User }

	public static List<Map<String, Object>> getClassOrUserListDB(dataClass dataStat) {

		try {

			String getDBString = null;
			String getDBDataString = null;

			switch (dataStat) {
				case Class:
					getDBString = "application/classes/classes.db";
					getDBDataString = "select * from classes";
					break;
				case User:
					getDBString = "application/user/user.db";
					getDBDataString = "select * from user";
					break;
				default:
					return null;
			}

			Connection connection = DriverManager.getConnection("jdbc:sqlite:" + getDBString);
			Statement statement = connection.createStatement();

			return getList(statement, getDBDataString);

		} catch (Exception exception) { exception.printStackTrace(); }

		return null;

	}

	public static Connection getClassOrUserConnectionDB(dataClass dataStat) {

		try {

			String getDBString = null;

			switch (dataStat) {
				case Class:
					getDBString = "application/classes/classes.db";
					break;
				case User:
					getDBString = "application/user/user.db";
					break;
				default:
					return null;
			}

			return DriverManager.getConnection("jdbc:sqlite:" + getDBString);

		} catch (Exception exception) { exception.printStackTrace(); }

		return null;

	}

}
