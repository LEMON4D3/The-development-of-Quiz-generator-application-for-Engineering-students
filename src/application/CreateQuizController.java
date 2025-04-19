package application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Cursor;
import javafx.scene.control.Button;

public class CreateQuizController {

    @FXML private Button pdfBtn;
    @FXML private Button fillInTheBlankBtn;
    @FXML private Button matchBtn;
    @FXML private Button multipleChoiceBtn;
    @FXML private Button trueOrFalseBtn;
    @FXML private Button homeBtn; // Assuming your "logo" is a button

    @FXML
    public void initialize() {
        // Set hand cursor on hover
        pdfBtn.setCursor(Cursor.HAND);
        fillInTheBlankBtn.setCursor(Cursor.HAND);
        matchBtn.setCursor(Cursor.HAND);
        multipleChoiceBtn.setCursor(Cursor.HAND);
        trueOrFalseBtn.setCursor(Cursor.HAND);
        homeBtn.setCursor(Cursor.HAND);
    }

    @FXML
    public void goToHomepage(ActionEvent event) throws IOException {
        Parent homepage = FXMLLoader.load(getClass().getResource("Homepage.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(homepage));
        stage.setTitle("Quiz Generator");
    }

    @FXML
    public void fillInTheBlank(ActionEvent event) {
        // handle Fill in the Blank logic
    }

    @FXML
    public void matchQuestions(ActionEvent event) {
        // handle Match logic
    }

    @FXML
    public void multipleChoice(ActionEvent event) {
        // handle Multiple Choice logic
    }

    @FXML
    public void trueOrFalse(ActionEvent event) {
        // handle True/False logic
    }

    @FXML
    public void importPDF(ActionEvent event) {
        // handle PDF import logic
    }
}
