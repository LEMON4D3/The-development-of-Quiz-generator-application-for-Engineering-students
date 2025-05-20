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

	public static void main(String[] args)  {
		
		 try {

			 new Init();
			 launch(args);

		 } catch (Exception exception) { exception.printStackTrace(); }
	}
	
	@Override
	public void start(Stage stage) throws Exception {

//		user.currentUser = "xien";
//		user.currentQuiz = "AightBro";

//		Parent root = FXMLLoader.load(getClass().getResource("/multipleChoice/create/MultipleChoice.fxml"));

		//		user.currentUser = "xien";
//		Parent root = FXMLLoader.load(getClass().getResource("/homepage/teacher/Homepage.fxml"));

//		Parent root = FXMLLoader.load(getClass().getResource("/signUp/signUp.fxml"));


//		Parent root = FXMLLoader.load(getClass().getResource("/compiler/answer/Compiler.fxml"));

//		Parent root = FXMLLoader.load(getClass().getResource("/classHomepage/teacher/Homepage.fxml"));
		Parent root = FXMLLoader.load(getClass().getResource("/login/Login.fxml"));

		stage.setScene(new Scene(root));
		stage.setTitle("QWIZY");
		stage.setResizable(false);
		stage.show();

	}


}
