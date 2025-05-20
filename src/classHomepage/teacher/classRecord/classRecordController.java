package classHomepage.teacher.classRecord;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import util.Util;
import util.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static java.util.Arrays.stream;

public class classRecordController implements Initializable {

    @FXML
    private TableView<tableViewStruct> tableViewContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tableViewContainer.setPlaceholder(new Label("No Students Enrolled :("));
        new initTableView();

    }

    @FXML
    public void backBtnFn(ActionEvent event) {

        new controller().changeScene(event, "/classHomepage/teacher/Homepage.fxml");

    }



    class initTableView{

        List<String> columnNameList = new ArrayList<>();
        ObservableList<tableViewStruct> tableViewList = FXCollections.observableArrayList();

        initTableView() {

            setTableColumn();
            setTableRow();

        }

        public void setTableColumn() {

            try {

                Connection quizConnection = Util.getClassConnectionDB();

                String getQuizString = "select * from classes";
                Statement getQuizStatement = quizConnection.createStatement();

                List<Map<String, Object>> quizList = Util.getList(getQuizStatement, getQuizString);
                for(Map<String, Object> quiz : quizList) {
                    if((Integer) quiz.get("isQuiz") != 0) {
                        columnNameList.add((String) quiz.get("announcement"));
                    }
                }

                TableColumn<tableViewStruct, String> studentNameColumn = new TableColumn<>("Student Name");
                studentNameColumn.setCellValueFactory(cellData -> {

                    return new SimpleObjectProperty<>(cellData.getValue().getStudentName());

                });

                tableViewContainer.getColumns().add(studentNameColumn);
                int index = 0;
                for(String quizName : columnNameList) {

                    final int scoreIndex = index;
                    TableColumn<tableViewStruct, String> quizColumn = new TableColumn<>(quizName);
                    quizColumn.setCellValueFactory(cellData -> {

                        return new SimpleObjectProperty<>(cellData.getValue().getScoreByIndex(scoreIndex));

                    });


                    tableViewContainer.getColumns().add(quizColumn);

                    index++;
                }

                TableColumn<tableViewStruct, String> averageColumn = new TableColumn<>("Average");
                averageColumn.setCellValueFactory(cellData -> {
                    return new SimpleObjectProperty<>(cellData.getValue().getAverageValue() + "%");
                });
                tableViewContainer.getColumns().add(averageColumn);


            } catch (Exception e) { e.printStackTrace(); }

        }

        public void setTableRow() {

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
                    List<String> scores = new ArrayList<>();
                    List<String> codeExerciseList = new ArrayList<>();
                    List<Integer> defaultScore = new ArrayList<>();

                    for (List<Map<String, Object>> quizList : studentQuizList) {
                        boolean found = false;



                        for (Map<String, Object> quiz : quizList) {

                            if (quiz.get("username").toString().equals(studentName)) {

                                if(quiz.size() > 1) {

                                    String[] scoreString = quiz.get("quiz point").toString().replace("[", "").replace("]", "").split(",");
                                    List<String> scoreList = new ArrayList<>();
                                    for (String score : scoreString) {
                                        scoreList.add(score.trim());
                                    }

                                    scores.addAll(scoreList);
                                    found = true;

                                    defaultScore.add((Integer) quiz.get("total point"));

                                    /*System.out.println("Quiz Point: " + quiz.get("quiz point"));
                                    if(List.of(quiz.get("quiz point").toString().substring(1, quiz.get("total point").toString().length() - 1).split(",")).stream().map(String::trim).map(Integer::parseInt).toList() == null) {
                                        defaultScore.add(0);
                                    } else {

                                        List<Integer> temp = List.of(quiz.get("quiz point").toString().substring(1, quiz.get("total point").toString().length() - 1).split(",")).stream().map(String::trim).map(Integer::parseInt).toList();
                                        for(Integer tempScore : temp)
                                            defaultScore.add(tempScore);
                                    }*/

                                    System.out.println("Default Score: " + defaultScore);
                                    break;
                                } else {
                                    codeExerciseList.add("Passed");
                                    found = true;
                                }
                            }

                        }

                        if (!found) {
                            scores.add("Quiz/Code Exercise Not Attempted"); // Default score if not found
                        }
                    }

                    tableViewStruct row = new tableViewStruct(studentName, scores, defaultScore, codeExerciseList);
                    tableViewList.add(row);
                }

                System.out.println(tableViewList);
                tableViewContainer.setItems(tableViewList);

            } catch (Exception exception) { exception.printStackTrace(); }

        }
    }

    class tableViewStruct {

        private String studentName;
        private List<String> score;
        private List<String> codeExerciseList;

        float averageValue = 0;

        tableViewStruct(String studentName, List<String> score, List<Integer> defaultScore, List<String> codeExerciseList) {

            this.codeExerciseList = codeExerciseList;
            this.studentName = studentName;
            this.score = score;

            float defaultScoreFinal = 0;
            for(int Score : defaultScore)
                defaultScoreFinal += Score;

            try {
                for(String Score : score) {
                    averageValue += Integer.parseInt(Score);
                }

                System.out.println(averageValue + " + " + defaultScoreFinal + " =" + ((averageValue / defaultScoreFinal) * 100));

            } catch (Exception exception) { exception.printStackTrace(); }

            averageValue = (averageValue / defaultScoreFinal) * 100;
        }

        tableViewStruct(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentName() { return studentName; }

        public List<String> getScore() { return score; }

        public String getScoreByIndex(int index) {

            String independentScore = "";

            if(index < score.size()) {
                independentScore = score.get(index);
            } else {

                try {
                    independentScore = codeExerciseList.get(codeExerciseList.size() - index);
                } catch (Exception e) {
                    independentScore = "Quiz/Code Exercise Not Attempted";
                }
            }

            System.out.println("Independent Score: " + independentScore);
            return independentScore;

        }

        public float getAverageValue() {  return averageValue; }

        public void getCodeExerciseList() {

        }

    }

}
