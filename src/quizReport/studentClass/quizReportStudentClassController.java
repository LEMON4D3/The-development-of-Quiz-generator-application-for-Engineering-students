package quizReport.studentClass;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import util.Util;
import util.controller;
import util.user;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class quizReportStudentClassController implements Initializable {

    @FXML
    private Label quizTitleT;

    @FXML
    private VBox scoreContainer;

    @FXML
    private Label studentGradeT;

    @FXML
    private Label studentT;

    @FXML
    private Label totalQuizT;

    @FXML
    private Label totalStudentT;

    Map<String, Object> userQuiz;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        getQuizReport();

    }

    public void backBtnFn(ActionEvent event) throws IOException {
        new controller().changeScene(event, "/classHomepage/student/Homepage.fxml");
    }

    public void initQuizReport(double average, String totalStudent, String totalQuiz) {

        quizTitleT.setText(user.currentQuiz);
        studentT.setText(user.currentUser);

        totalStudentT.setText(totalStudent);
        totalQuizT.setText(totalQuiz);

        studentGradeT.setText(average + "%");

    }

    quizListContainerStruct containerStruct = new quizListContainerStruct();

    private void getQuizReport() {

        List<Map<String, Object>> quizList =  quizList = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz + "List", false);
        for(Map<String, Object> quiz : quizList) {
            if(((String) quiz.get("username")).equals(user.currentUser)) {

                userQuiz = quiz;
                break;
            }
        }

        List<Map<String, Object>> baseQuizList = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz, false);
        for (Map<String, Object> baseQuiz : baseQuizList) {

            containerStruct.totalPoint += (Integer) baseQuiz.get("point");

        }

        containerStruct.totalStudent = quizList.size();
        containerStruct.studentTotalQuiz = (Integer) userQuiz.get("total point");

        containerStruct.initVariable(userQuiz);
        containerStruct.totalQuiz = containerStruct.quizQuestion.size();


        initQuizReport();



    }

    private void initQuizReport() {

        for(int i = 0; i < containerStruct.quizQuestion.size(); i++) {

            quizListContainer container = new quizListContainer(containerStruct.quizQuestion.get(i), containerStruct.quizAnswer.get(i), containerStruct.quizPoint.get(i));
            scoreContainer.getChildren().add(container);

        }

        quizTitleT.setText(containerStruct.quizTitle);
        totalStudentT.setText(containerStruct.totalStudent + "");
        totalQuizT.setText(containerStruct.totalQuiz + "");
        studentGradeT.setText(containerStruct.studentTotalQuiz + "/" + containerStruct.totalPoint);
        studentT.setText(containerStruct.username);

    }

    class quizListContainerStruct {

        String username, quizTitle;
        List<String> quizQuestion, quizAnswer, quizPoint;

        int totalStudent, totalQuiz, studentTotalQuiz;
        Integer  totalPoint = 0;


        public void initVariable(Map<String, Object> quizList) {

            quizTitle = (String) quizList.get("quiz title");

            String quizAnswerS = (String) quizList.get("quiz answer");
            quizAnswer = Arrays.asList(quizAnswerS.replace("[", "").replace("]", "").split(", "));

            String quizPointsS = (String) quizList.get("quiz point");
            quizPoint = Arrays.asList(quizPointsS.replace("[", "").replace("]", "").split(", "));

            String quizQuestionS = (String) quizList.get("quiz question");
            quizQuestion = Arrays.asList(quizQuestionS.replace("[", "").replace("]", "").split(", "));

            totalPoint = (Integer) quizList.get("total point");

            List<Map<String, Object>> studentList = Util.getClassOrUserListDB(Util.dataClass.User);
            for(Map<String, Object> student : studentList) {
                if(((String) student.get("username")).equals(user.currentUser)) {
                    String lastName = (String) student.get("last name");
                    String firstName = (String) student.get("first name");
                    String middleName = (String) student.get("middle name");
                    username = lastName + ", " + firstName + " " + middleName;
                    break;
                }
            }

        }

        quizListContainerStruct(){}

    }

    private class quizListContainer extends GridPane {

        String question, answer, point;

        quizListContainer(String Question, String Answer, String Point) {

            question = Question;
            answer = Answer;
            point = Point;

            GridPane.setFillWidth(this, true);
            this.setMinHeight(75);
            this.setPadding(new Insets(15, 15, 15, 15));
            this.setVgap(15);


            this.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

            topPane();
            bottomPane();

        }

        void topPane() {

            for (int i = 0; i < 3; i++) {
                ColumnConstraints column = new ColumnConstraints();
                column.setPercentWidth(100.0 / 3);
                this.getColumnConstraints().add(column);
            }

            Label quizNameT = new Label("Quiz Question");
            Label scoreT = new Label("Quiz Answer");
            Label quizPointT = new Label("Quiz Point");

            GridPane.setHalignment(quizNameT, HPos.CENTER);
            GridPane.setHalignment(scoreT, HPos.CENTER);
            GridPane.setHalignment(quizPointT, HPos.CENTER);

            this.add(quizNameT, 0, 0);
            this.add(scoreT, 1, 0);
            this.add(quizPointT, 2, 0);

        }

        void bottomPane() {

            Label quizNameB = new Label(question);
            Label scoreB = new Label(answer);
            Label timeStartedB = new Label(point);

            GridPane.setHalignment(quizNameB, HPos.CENTER);
            GridPane.setHalignment(scoreB, HPos.CENTER);
            GridPane.setHalignment(timeStartedB, HPos.CENTER);

            this.add(quizNameB, 0, 1);
            this.add(scoreB, 1, 1);
            this.add(timeStartedB, 2, 1);

        }

    }

}