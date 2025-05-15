package quiz;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import fitb.create.FITBCreateController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import multipleChoice.create.multipleChoiceCreateController;
import tof.create.tofCreateController;
import util.Util;
import util.controller;
import util.user;

public class quizController implements Initializable{
	
	@FXML
	TextField titleTF;
	
	@FXML
	Label errorT;
	
	List<Map<String, Object>> list = new ArrayList<>();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		try {
			
			
			String getUserString = null;
			String getUserQuizString = null;
			
			if(user.isTeacher) {
				getUserString = "application/classes/" + user.currentClass + "/" + user.currentClass + ".db";
				getUserQuizString = "select announcement, isQuiz  from classes";
			}
			else {
				getUserString = "application/user/student/" + user.currentUser + "/quiz/quiz.db";
				getUserQuizString = "select `quiz name` from quiz";
			}
			
			Connection getUserConnection = DriverManager.getConnection("jdbc:sqlite:" + getUserString);
			Statement getUserStatement = getUserConnection.createStatement();
			
			List<Map<String, Object>> userList = Util.getList(getUserStatement, getUserQuizString);
			
			if(user.isTeacher) {
				
				List<Map<String, Object>> quizList = new ArrayList<>();
				
				for(Map<String, Object> i: userList) 
					if((Integer) i.get("isQuiz") == 1) quizList.add(i);
				
				list = quizList;
				
			} else list = userList;
				
			
			
		} catch (Exception e) { System.out.println("User haven't created any quiz yet"); }
		
	}

	public void backBtnFn(ActionEvent event) {

		if(user.isTeacher) new controller().changeScene(event, "/classHomepage/teacher/Homepage.fxml");
		else if(!user.isTeacher && user.currentClass != null) new controller().changeScene(event, "/classHomepage/student/Homepage.fxml");
		else if(!user.isTeacher) new controller().changeScene(event, "/homepage/student/Homepage.fxml");

	}

	public void fitbBtnFn(ActionEvent event) throws IOException { new categoryFn("/fitb/create/FITB.fxml", event, categoryType.fitb); }
	
	public void multipleChoiceBtnFn(ActionEvent event) throws IOException { new categoryFn("/multipleChoice/create/MultipleChoice.fxml", event, categoryType.multipleChoice); }
	
	public void tofBtnFn(ActionEvent event) throws IOException { new categoryFn("/tof/create/Tof.fxml", event, categoryType.tof); }
	
	enum categoryType {
		fitb,
		multipleChoice,
		tof
	}
	
	class categoryFn {
		
		categoryFn(String url, ActionEvent event, categoryType category) throws IOException {
			
			if(checkTitleText()){
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
				Parent root = loader.load();
				
//				FITBCreateController controller = loader.getController();
//				controller.setTitle(titleTF.getText());
				
				switch(category) {
				case fitb:
					FITBCreateController controller = loader.getController();
					controller.setTitle(titleTF.getText());
					break;
				case multipleChoice:
					multipleChoiceCreateController controller1 = loader.getController();
					controller1.setTitle(titleTF.getText());
					break;
				case tof:
					tofCreateController controller2 = loader.getController();
					controller2.setTitle(titleTF.getText());
					break;
				default:
					break;
				
				}
//				
//				FITB controller = loader.getController();
//				controller.setTitle(titleTF.getText());
				
				Stage stage = (Stage)((Node)((EventObject)event).getSource()).getScene().getWindow();
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
				
			}
			
		}
		
	}
	
	public void backBtnFn(MouseEvent e) throws IOException {
		
		String goToPage = (user.isTeacher) ? "/classHomepage/teacher/Homepage.fxml" : "/homepage/student/Homepage.fxml";
		new controller().changeScene(e, goToPage);
		
	}
	
 	enum checkStat {
		normal,
		duplicate,
		error
	}
	
	private boolean checkTitleText() {
		
		if(titleTF.getText().isEmpty()) {
			
			errorT.setText("The quiz title is empty");
			return false;
			
		}
		
		
		
		checkStat stat = checkDuplicate(list);
		
		switch(stat) {
		case normal:
			return true;
		default:
			return false;
		}
		
	}
	
	private checkStat checkDuplicate(List<Map<String, Object>> list) {
		
		
		if(user.isTeacher) {
			
			for(Map<String, Object> i: list) {
				
				String announcement = titleTF.getText();
				
				if(announcement.equals((String) i.get("announcement"))) {
					errorT.setText("You already have " + announcement + "in your library");
					return checkStat.duplicate;
				}
					
				
			}
			
			return checkStat.normal;
			
		}
		
		for(Map<String, Object> i: list) {
			
			String quizName = titleTF.getText();
			
			if(quizName.equals((String) i.get("quiz name"))) {
				errorT.setText("You already have " + quizName + "in your library");
				return checkStat.duplicate;
			}
				
			
		}
		
		return checkStat.normal;
		
	}
	
	
}
