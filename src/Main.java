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
		
		// student: xie
		// teacher: rein

//		student
		user.isTeacher = false;
		user.currentUser = "xien";

//		teacher
//		user.isTeacher = true;
//		user.currentUser = "5";
//		user.currentClass = "1";
//		user.currentQuiz = "dadadadad";

		testNormalDB(stage);



		
	}

	public void testQuestionDB(Stage stage) {

		List<Map<String, Object>> quizList = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz, false);

		Map<String, Object> quiz = quizList.get(0);
		System.out.println("Current Quiz: " + quiz);

		quizCreateControllerExtend quizController = new quizCreateControllerExtend();
		quizController.prepareQuiz(quizList);

		stage.setScene(quizController.quizListScene.getFirst());
		stage.show();

	}

	public void testNormalDB(Stage stage) {

		try {

			//Parent root = FXMLLoader.load(getClass().getResource("/quizReport/QuizReport.fxml"));
			Parent root = FXMLLoader.load(getClass().getResource("/homepage/student/Homepage.fxml"));
			//Parent root = FXMLLoader.load(getClass().getResource("/homepage/teacher/Homepage.fxml"));

			stage.setScene(new Scene(root));
			stage.show();

		} catch (Exception exception) {exception.printStackTrace();}
	}

}
