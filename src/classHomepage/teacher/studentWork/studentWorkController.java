package classHomepage.teacher.studentWork;

import classHomepage.teacher.classRecord.classRecordController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.Util;
import util.controller;
import util.user;

import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

public class studentWorkController implements Initializable {

    @FXML
    private VBox gradeContainer;

    @FXML
    private Label quizTitleT;

    @FXML
    private VBox studentContainer;

    @FXML
    private Label totalQuestionT;

    @FXML
    private Label totalStudentT;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        new initStudentContainer();
        new initGradeContainer();
        new initComponents();

    }

    public void userBtnFn(MouseEvent event) {
        new Util().userPopUp(event);
    }

    @FXML
    public void backBtnFn(ActionEvent event) {

        if(currentStatus == status.REPORT) new controller().changeScene(event, "/homepage/teacher/report/Report.fxml");
        else if(currentStatus == status.CLASS) new util.controller().changeScene(event, "/classHomepage/teacher/Homepage.fxml");

    }

    status currentStatus = status.CLASS;

    enum status {
        CLASS,
        REPORT
    }

    public void setBackStatus() {
        currentStatus = status.REPORT;
    }

    class initComponents {

        initComponents() {

            initQuizTitle();
            initTotalStudent();
            initTotalQuiz();

        }

        void initTotalQuiz() {
            List<Map<String, Object>> quiz = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz, false);
            int totalQuestionPoints = 0;
            for(Map<String, Object> quizList : quiz) {
                totalQuestionPoints += (Integer) quizList.get("point");
            }

            totalQuestionT.setText(totalQuestionPoints + "");
        }

        void initTotalStudent() {
            try {

                List<Map<String, Object>> student = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz + "List", false);
                totalStudentT.setText(student.size() + "");

            } catch (Exception exception) { exception.printStackTrace(); }
        }

        void initQuizTitle() {
            quizTitleT.setText(user.currentQuiz);
        }

    }

    class initStudentContainer {

        List<Map<String, Object>> studentList = new ArrayList<>();

        initStudentContainer() {

            studentList = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz + "List", false);

            for(Map<String, Object> student : studentList) {
                studentContainer.getChildren().add(new container(student));
            }

        }

        class container extends GridPane {

            Map<String, Object> student;
            String containerStyle = "-fx-background-color: white;";
            String labelStyle = "-fx-text-fill: black;";

            container(Map<String, Object> student) {

                this.student = student;
                this.setStyle(containerStyle);
                this.setPadding(new Insets(15, 25, 15, 25));

                this.setPrefHeight(75);
                this.setHgap(50);
                this.setVgap(15);

                topPane();
                bottomPane();
            }

            void topPane() {

                Label studentNameT = new Label("Student Name");
                Label score = new Label("Score");

                studentNameT.setStyle(labelStyle);
                score.setStyle(labelStyle);

                this.add(studentNameT, 0, 0);
                this.add(score, 1, 0);

            }

            void bottomPane() {

                Label studentNameT = new Label((String) student.get("username"));
                GridPane.setHalignment(studentNameT, HPos.CENTER);

                String quizPointsS = (String) student.get("quiz point").toString().substring(1, student.get("quiz point").toString().length() - 1);
                String[] quizPointsList = quizPointsS.split(",");
                List<Integer> quizPoint = new ArrayList<>();

                for(String quizPointT : quizPointsList) {
                    quizPoint.add(Integer.parseInt(quizPointT));
                }

                int studentPoint = 0;

                for(int i : quizPoint) {
                    studentPoint += i;
                }

                Label score = new Label(studentPoint + "");
                GridPane.setHalignment(score, HPos.CENTER);

                studentNameT.setStyle(labelStyle);
                score.setStyle(labelStyle);

                this.add(studentNameT, 0, 1);
                this.add(score, 1, 1);

            }

        }

    }

    class initGradeContainer {

        /*
            containerList attributes
            - username
            - average
        */

        List<Map<String, Object>> containerList = new ArrayList<>();

        List<String> columnNameList = new ArrayList<>();
        List<Integer> scoreList = new ArrayList<>();

        private void getColumnNameList() {

            try {

                Connection quizConnection = Util.getClassConnectionDB();

                String getQuizString = "select * from classes";
                Statement getQuizStatement = quizConnection.createStatement();

                List<Map<String, Object>> quizList = Util.getList(getQuizStatement, getQuizString);
                for (Map<String, Object> quiz : quizList) {
                    if ((Integer) quiz.get("isQuiz") == 1) {
                        columnNameList.add((String) quiz.get("announcement"));
                    }
                }

            } catch (Exception exception) { exception.printStackTrace(); }
        }

        private void setContainerList() {

            try {

                Connection studentClassConnection = Util.getClassConnectionDB();

                String getStudentListString = "select * from student";
                Statement getStudentListStatement = studentClassConnection.createStatement();

                List<Map<String, Object>> studentList = Util.getList(getStudentListStatement, getStudentListString);
                List<List<Map<String, Object>>> studentQuizList = new ArrayList<>();

                for(String quizName : columnNameList)
                    studentQuizList.add(Util.getQuizOrAnnouncementClassListDB(quizName + "List", false));

                for (Map<String, Object> student : studentList) {

                    String studentName = student.get("student name").toString();
                    List<Integer> scores = new ArrayList<>();
                    float totalQuestionPoints = 0;

                    for (List<Map<String, Object>> quizList : studentQuizList) {
                        boolean found = false;

                        for (Map<String, Object> quiz : quizList) {

                            if (quiz.get("username").toString().equals(studentName)) {

                                String[] scoreString = quiz.get("quiz point").toString().replace("[", "").replace("]", "").split(",");
                                List<Integer> scoreList = new ArrayList<>();
                                for (String score : scoreString) {
                                    scoreList.add(Integer.parseInt(score.trim()));
                                }

                                scores.addAll(scoreList);
                                found = true;
                                totalQuestionPoints += (Integer) quiz.get("total point");
                                break;
                            }
                        }
                        if (!found) {
                            scores.add(0); // Default score if not found
                        }
                    }

                    Map<String, Object> tempContainer = new HashMap<>();

                    float totalUserScore = 0;

                    for(int score: scores)
                        totalUserScore += score;

                    totalUserScore = (totalUserScore / totalQuestionPoints) * 100;

                    tempContainer.put("username", studentName);
                    tempContainer.put("average", totalUserScore);

                    containerList.add(tempContainer);

                    System.out.println("Added: " + tempContainer);

                }

            } catch (Exception exception) { exception.printStackTrace(); }

        }

        initGradeContainer() {

            getColumnNameList();
            setContainerList();

            for(Map<String, Object> container : containerList) {

                gradeContainer.getChildren().add(new container(container));

            }

        }

        class container extends GridPane {

            Map<String, Object> container;

            String containerStyle = "-fx-background-color: white;";
            String labelStyle = "-fx-text-fill: black;";
            
            container(Map<String, Object> container) {

                this.setStyle(containerStyle);
                this.setPadding(new Insets(15, 25, 15, 25));
                this.setPrefHeight(75);
                this.setHgap(50);
                this.setVgap(15);

                this.container = container;

                topPane();
                bottomPane();
            }

            void topPane() {

                Label nameT = new Label("Student Name");
                Label averageT = new Label("Average");
                
                nameT.setStyle(labelStyle);
                averageT.setStyle(labelStyle);
                
                this.add(nameT, 0, 0);
                this.add(averageT, 1, 0);
            }

            void bottomPane() {

                Label nameT = new Label((String) container.get("username"));
                GridPane.setHalignment(nameT, HPos.CENTER);

                Label averageT = new Label((Float) container.get("average") + "%");
                GridPane.setHalignment(averageT, HPos.CENTER);

                nameT.setStyle(labelStyle);
                averageT.setStyle(labelStyle);

                this.add(nameT, 0, 1);
                this.add(averageT, 1, 1);
            }

        }

    }

}
