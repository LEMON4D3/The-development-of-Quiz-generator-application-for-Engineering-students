package classHomepage.teacher.classList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.Util;
import util.controller;
import util.user;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class classListController implements Initializable {

    @FXML
    private VBox emptyListContainer, studentContainerList;

    @FXML
    Button classCodeT, exitBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        new getInfo();

    }

    @FXML
    public void exitBtnFn(ActionEvent event) {

        Stage stage = new controller().getStage(event);
        stage.close();

    }

    @FXML
    public void classCodeBtnFn(ActionEvent event) {

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(classCodeT.getText());
        clipboard.setContent(content);

    }

    class getInfo {

        List<Map<String, Object>> studentListFinal = new ArrayList<>();

        getInfo() {

            getClassList();
            if(studentListFinal.isEmpty())
                setEmptyContainer();

        }

        void setEmptyContainer() {
            try {

                Connection classConnection = Util.getClassOrUserConnectionDB(Util.dataClass.Class);

                String getClassCodeString = "select * from classes where `class name` = '" + user.currentClass + "'";
                Statement classCodeStatement = classConnection.createStatement();

                ResultSet classCodeRS = classCodeStatement.executeQuery(getClassCodeString);
                String classCodeFinal = classCodeRS.getObject("class code").toString();

                classCodeT.setText(classCodeFinal);

            } catch (Exception e) { e.printStackTrace(); }
        }

        void getClassList() {

            try {

                Connection classConnection = Util.getClassConnectionDB();

                String getClassList = "select * from student";
                Statement classStatement = classConnection.createStatement();

                List<Map<String, Object>> studentList = Util.getList(classStatement, getClassList);
                if(studentList.isEmpty()) return;
                studentContainerList.getChildren().clear();
                studentContainerList.setAlignment(Pos.TOP_LEFT);

                Connection userConnection = Util.getClassOrUserConnectionDB(Util.dataClass.User);

                String getUserList = "select * from user";
                Statement userStatement = userConnection.createStatement();

                List<Map<String, Object>> userList = Util.getList(userStatement, getUserList);
                for(Map<String, Object> student : studentList) {

                    for(Map<String, Object> user : userList) {

                        if(student.get("student name").toString().equals(user.get("username").toString())) {

                            studentListFinal.add(user);
                            break;
                        }
                    }
                }


                for(Map<String, Object> user : studentListFinal) {

                    userContainer container = new userContainer(user);
                    studentContainerList.getChildren().add(container);

                }

            } catch (Exception e) { e.printStackTrace(); }

        }

        class userContainer extends HBox {

            userContainer(Map<String, Object> user) {

                this.setPadding(new Insets(0, 15, 0, 15));
                this.setSpacing(15);

                Image img = new Image("file:src/classHomepage/teacher/classList/rsc/user.png");
                ImageView imgView = new ImageView(img);

                imgView.setFitWidth(25);
                imgView.setFitHeight(25);

                String studentName = user.get("last name").toString() + ", " + user.get("first name").toString() + " " + user.get("middle name").toString();
                Label userNameT = new Label(studentName);
                userNameT.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

                this.getChildren().addAll(imgView, userNameT);
            }


        }

    }

}
