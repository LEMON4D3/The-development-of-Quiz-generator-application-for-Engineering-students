package homepage.teacher.report;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import util.Util;
import util.user;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class reportController implements Initializable {

    @FXML
    private ComboBox<String> classCombo, dateCombo;

    @FXML
    private GridPane quizContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        new initComboBox();

    }

    @FXML
    public void backBtnFn(ActionEvent event) {
        new util.controller().changeScene(event, "/homepage/teacher/Homepage.fxml");
    }

    class initComboBox{

        List<String> classNameList = new ArrayList<>();

        initComboBox() {

            getClassList();
            ObservableList<String> classList = FXCollections.observableArrayList(classNameList);
            classCombo.setItems(classList);
            classCombo.setValue(classNameList.get(0));
            classCombo.setOnAction(event -> {
                new initGridComponents(classCombo.getValue());
            });

            new initGridComponents(classCombo.getValue());

        }

        void getClassList() {

            try {

                Connection quizConnection = Util.getTeacherConnectionDB();

                String getClassString = "select `class name` from classes";
                Statement getClassStatement = quizConnection.createStatement();

                ResultSet getClassRS = getClassStatement.executeQuery(getClassString);
                while(getClassRS.next()) {
                    classNameList.add(getClassRS.getString("class name"));
                }

            } catch (Exception exception) { exception.printStackTrace(); }

        }

        class initGridComponents {

            Label quizNameT = new Label("Quiz Name");
            Label participantsT = new Label("Total Participants");

            String className;

            List<Map<String, Object>> containerList = new ArrayList<>();

            initGridComponents(String className) {

                this.className = className;

                quizContainer.getChildren().clear();
                initRequireComponent();
                getQuizName();
                getTotalParticipants();

                for(int i = 0, x = 0, y = 1; i < containerList.size(); i++, y++) {

                    Map<String, Object> container = containerList.get(i);
                    quizContainer.add((Label) container.get("quiz name"), x, y);
                    quizContainer.add((Label) container.get("participant"), x + 1, y);

                    x = 0;
                }

                for(Node node : quizContainer.getChildren()) {
                    Integer rowIndex = GridPane.getRowIndex(node);
                    if(rowIndex != null && rowIndex > 0 && (node != quizNameT && node != participantsT)) {
                        node.setOnMouseClicked(event -> {

                            System.out.println("Clicked: " + ((Label) node).getText());

                        });
                    }
                }

            }

            void getTotalParticipants() {

                try {

                    for(int i = 0; i < containerList.size(); i++) {

                        Map<String, Object> container = containerList.get(i);
                        String quizName = (String) ((Label) container.get("quiz name")).getText();
                        List<Map<String, Object>> studentList = Util.getQuizOrAnnouncementClassListDB(quizName + "List", false);

                        Map<String, Object> tempContainer = new HashMap<>();
                        Label participants = new Label(String.valueOf(studentList.size()));
                        participants.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px;");
                        participants.setPrefWidth(Double.MAX_VALUE);
                        participants.setAlignment(Pos.CENTER);
                        container.put("participant", participants);

                        containerList.set(i, container);
                    }

                    System.out.println("containerList: " + containerList);

                } catch (Exception exception) { exception.printStackTrace(); }

            }

            void getQuizName() {
                try {

                    user.currentClass = className;
                    List<Map<String, Object>> quizList = Util.getQuizOrAnnouncementClassListDB(user.currentClass, true);
                    for(Map<String, Object> quiz : quizList) {
                        if((Integer) quiz.get("isQuiz") == 1) {

                            Map<String, Object> container = new HashMap<>();
                            Label quizNameT = new Label((String) quiz.get("announcement"));
                            quizNameT.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 18px;");
                            quizNameT.setPrefWidth(Double.MAX_VALUE);
                            quizNameT.setAlignment(Pos.CENTER);

                            container.put("quiz name", quizNameT);

                            containerList.add(container);
                        }
                    }

                    System.out.println("containerList: " + containerList);

                } catch (Exception exception) { exception.printStackTrace(); }
            }

            void initRequireComponent() {

                String headerStyle = "-fx-font-size: 18px; -fx-text-fill: white;";



                quizNameT.setStyle(headerStyle);
                participantsT.setStyle(headerStyle);

                GridPane.setHalignment(quizNameT, HPos.CENTER);
                GridPane.setHalignment(participantsT, HPos.CENTER);

                quizContainer.add(quizNameT, 0, 0);
                quizContainer.add(participantsT, 1, 0);



            }

        }

    }



}
