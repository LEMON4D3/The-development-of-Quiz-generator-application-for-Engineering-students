package homepage.teacher.report;

import classHomepage.teacher.studentWork.studentWorkController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import util.Util;
import util.controller;
import util.user;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class reportController implements Initializable {

    @FXML
    private ComboBox<String> classCombo;

    @FXML
    private VBox container;

    @FXML
    private GridPane quizContainer;
    Label noClass = new Label("No Quiz Created Yet");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        new initComboBox();

    }

    @FXML
    public void backBtnFn(ActionEvent event) {
        new util.controller().changeScene(event, "/homepage/teacher/Homepage.fxml");
    }

    public void userBtnFn(MouseEvent event) { new Util().userPopUp(event); }

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
                container.getChildren().removeAll(noClass);

                quizContainer.getChildren().clear();
                initRequireComponent();
                getQuizName();
                getTotalParticipants();

                if(containerList.isEmpty()) {

                    noClass.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
                    noClass.setTextAlignment(TextAlignment.CENTER);
                    noClass.setAlignment(Pos.CENTER);
                    noClass.setPrefWidth(Double.MAX_VALUE);
                    container.getChildren().add(noClass);
                    return;

                }

                for(int i = 0, x = 0, y = 1; i < containerList.size(); i++, y++) {

                    Map<String, Object> container = containerList.get(i);
                    quizContainer.add((Label) container.get("quiz name"), x, y);
                    quizContainer.add((Label) container.get("participant"), x + 1, y);

                    x = 0;
                }

                String reportLocation = "";

                for(Node node : quizContainer.getChildren()) {

                    Integer rowIndex = GridPane.getRowIndex(node);
                    Integer columnIndex = GridPane.getColumnIndex(node);

                    boolean columnCheck = (columnIndex != null && columnIndex == 0);
                    if(columnCheck) {
                        reportLocation = ((Label) node).getText();
                    }

                    final String reportLocationFinal = reportLocation;

                    if(rowIndex != null && rowIndex > 0 && (node != quizNameT && node != participantsT)) {
                        node.setOnMouseClicked(event -> {

                            try {

                                user.currentQuiz = reportLocationFinal;
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/classHomepage/teacher/studentWork/StudentWork.fxml"));
                                Parent root = loader.load();

                                studentWorkController controller = loader.getController();
                                controller.setBackStatus();

                                Stage stage = new controller().getStage(event);
                                stage.setScene(new Scene(root));
                                stage.show();


                            } catch (Exception exception) { exception.printStackTrace(); }
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
