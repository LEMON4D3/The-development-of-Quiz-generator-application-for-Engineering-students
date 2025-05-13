package util.fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.controller;

import java.io.IOException;

public class userPopUp {

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

}
