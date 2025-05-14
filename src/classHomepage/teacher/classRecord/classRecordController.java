package classHomepage.teacher.classRecord;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import util.Util;

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

        new initTableView();

    }

    @FXML
    public void backBtnFn() {



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
                tableViewContainer.getColumns().add(studentNameColumn);

                for(String quizName : columnNameList) {
                    TableColumn<tableViewStruct, Integer> quizColumn = new TableColumn<>(quizName);
                    tableViewContainer.getColumns().add(new TableColumn<>(quizName));
                }

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
                                scores.add((Integer) quiz.get("score"));
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

                System.out.println(studentQuizList);

            } catch (Exception exception) { exception.printStackTrace(); }

        }
    }

    class tableViewStruct {

        String studentName;
        List<Integer> score;

        tableViewStruct(String studentName, List<Integer> score) {

            this.studentName = studentName;
            this.score = score;

        }

        tableViewStruct(String studentName) {
            this.studentName = studentName;
        }

    }

}
