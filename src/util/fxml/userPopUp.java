package util.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.controller;
import util.user;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class userPopUp implements Initializable {

    /*
        * To call userPopUp use new Util().userPopUp(event);
     */

    @FXML
    Label isTeacherT, usernameT;

    Stage mainStage = null;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void logoutBtnFn(MouseEvent event) throws IOException {

        Stage stage = new controller().getStage(event);
        stage.close();

        Parent root = FXMLLoader.load(getClass().getResource("/login/Login.fxml"));
        mainStage.setScene(new Scene(root));
        mainStage.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        usernameT.setText(user.currentUser);
        isTeacherT.setText(user.isTeacher ? "Teacher" : "Student");

    }
}
