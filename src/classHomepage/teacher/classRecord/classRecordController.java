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
                    if((Integer) quiz.get("isQuiz") == 1) {
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
                    TableColumn<tableViewStruct, Integer> quizColumn = new TableColumn<>(quizName);
                    quizColumn.setCellValueFactory(cellData -> {

                        return new SimpleObjectProperty<>(cellData.getValue().getScoreByIndex(scoreIndex));

                    });


                    tableViewContainer.getColumns().add(quizColumn);

                    index++;
                }

                TableColumn<tableViewStruct, Float> averageColumn = new TableColumn<>("Average");
                averageColumn.setCellValueFactory(cellData -> {
                    return new SimpleObjectProperty<>(cellData.getValue().getAverageValue());
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
                    List<Integer> scores = new ArrayList<>();

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
                                break;
                            }
                        }
                        if (!found) {
                            scores.add(0); // Default score if not found
                        }
                    }

                    tableViewStruct row = new tableViewStruct(studentName, scores);
                    tableViewList.add(row);
                }

                System.out.println(tableViewList);
                tableViewContainer.setItems(tableViewList);

            } catch (Exception exception) { exception.printStackTrace(); }

        }
    }

    class tableViewStruct {

        private String studentName;
        private List<Integer> score;

        float averageValue = 0;

        tableViewStruct(String studentName, List<Integer> score) {

            this.studentName = studentName;
            this.score = score;

            for(int Score : score)
                averageValue += Score;

            averageValue /= score.size();
        }

        tableViewStruct(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentName() { return studentName; }

        public List<Integer> getScore() { return score; }

        public Integer getScoreByIndex(int index) {

            Integer independentScore = (index < score.size()) ? score.get(index) : 0;
            System.out.println("Independent Score: " + independentScore);
            return independentScore;

        }

        public float getAverageValue() {  return averageValue; }

    }

}
