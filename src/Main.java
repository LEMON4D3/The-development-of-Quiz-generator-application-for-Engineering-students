import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.Init;
import util.Util;
import util.user;
import util.quizCreateControllerExtend;

import multipleChoice.answer.multipleChoiceAnswerController;

public class Main extends Application {

	public static void main(String[] args) throws SQLException  { 
		
		
		new Init();
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {


		// Student
		user.currentUser = "xie";
		user.isTeacher = false;

/*

		// Teacher
		user.currentUser = "xien";
		user.isTeacher = true;
*/


		Parent root = FXMLLoader.load(getClass().getResource("/homepage/student/Homepage.fxml"));
//		Parent root = FXMLLoader.load(getClass().getResource("/compiler/create/Compiler.fxml"));

		stage.setScene(new Scene(root));
		stage.setTitle("QWIZY");
		stage.setResizable(false);
		stage.show();

	}


}
