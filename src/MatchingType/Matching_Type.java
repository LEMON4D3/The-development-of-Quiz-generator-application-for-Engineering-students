package Matching_Type;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class Matching_Type extends Application {
    private int pairCount;
    private Button[] questionButtons;
    private Button[] answerButtons;
    private String[] questions;
    private String[] answers;
    private ArrayList<String[]> pairs;
    private int selectedQuestionIndex = -1;
    private int selectedAnswerIndex = -1;
    private int correctMatches = 0;
    private GridPane mainPane;

    public Matching_Type() {}

    public Matching_Type(ArrayList<String[]> inputPairs) {
        this.pairs = inputPairs;
        this.pairCount = inputPairs.size();
        this.questions = new String[pairCount];
        this.answers = new String[pairCount];

        for (int i = 0; i < pairCount; i++) {
            questions[i] = pairs.get(i)[0];
            answers[i] = pairs.get(i)[1];
        }

        ArrayList<String> shuffledAnswers = new ArrayList<>();
        Collections.addAll(shuffledAnswers, answers);
        Collections.shuffle(shuffledAnswers);
        answers = shuffledAnswers.toArray(new String[0]);
    }

    @Override
    public void start(Stage primaryStage) {
        QuestionsCount.launchForm(pairs -> {
            Matching_Type app = new Matching_Type(pairs);
            app.show(primaryStage);
        });
    }

    public void show(Stage stage) {
        mainPane = new GridPane();
        mainPane.setPadding(new Insets(20));
        mainPane.setHgap(10);
        mainPane.setVgap(10);
        mainPane.setStyle("-fx-background-color: #003153;");

        // Allow both columns to grow with the window
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        mainPane.getColumnConstraints().addAll(col1, col2);

        questionButtons = new Button[pairCount];
        answerButtons = new Button[pairCount];

        for (int i = 0; i < pairCount; i++) {
            Button qBtn = createButton(questions[i], i, true);
            Button aBtn = createButton(answers[i], i, false);
            questionButtons[i] = qBtn;
            answerButtons[i] = aBtn;

            mainPane.add(qBtn, 0, i);
            mainPane.add(aBtn, 1, i);
        }

        Scene scene = new Scene(mainPane, 1080, 720);
        stage.setTitle("Matching Type");
        stage.setScene(scene);
        stage.show();
    }

    private Button createButton(String text, int index, boolean isQuestion) {
        Button button = new Button(text);

        // Font styling
        button.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 16));

        // Styling and resizing
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-font-size: 16px;");
        button.setBackground(new Background(new BackgroundFill(Color.web("#002A45"), new CornerRadii(5), Insets.EMPTY)));

        button.setMaxWidth(Double.MAX_VALUE); // Stretch width
        button.setMinHeight(40);              // Consistent height

        button.setOnAction(e -> {
            if (isQuestion) {
                selectedQuestionIndex = index;
            } else {
                selectedAnswerIndex = index;
            }
            checkMatch();
        });

        return button;
    }

    private void checkMatch() {
        if (selectedQuestionIndex != -1 && selectedAnswerIndex != -1) {
            String question = questions[selectedQuestionIndex];
            String answer = answers[selectedAnswerIndex];

            if (isMatchingPair(question, answer)) {
                markAsMatched(questionButtons[selectedQuestionIndex], answerButtons[selectedAnswerIndex]);
                correctMatches++;
            } else {
                markAsIncorrect(questionButtons[selectedQuestionIndex], answerButtons[selectedAnswerIndex]);
            }

            if (correctMatches == pairCount) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Congratulations");
                alert.setHeaderText(null);
                alert.setContentText("Well done! You matched all pairs correctly!");
                alert.showAndWait();

                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to return to the homepage?");
                Optional<ButtonType> result = confirm.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    Platform.exit();
                } else {
                    Stage newStage = new Stage();
                    new Matching_Type(pairs).show(newStage);
                    ((Stage) mainPane.getScene().getWindow()).close();
                }
            }

            selectedQuestionIndex = -1;
            selectedAnswerIndex = -1;
        }
    }

    private void markAsMatched(Button qButton, Button aButton) {
        qButton.setStyle("-fx-background-color: gray; -fx-font-size: 16px;");
        aButton.setStyle("-fx-background-color: gray; -fx-font-size: 16px;");
        qButton.setDisable(true);
        aButton.setDisable(true);
    }

    private void markAsIncorrect(Button qButton, Button aButton) {
        qButton.setStyle("-fx-background-color: red; -fx-font-size: 16px;");
        aButton.setStyle("-fx-background-color: red; -fx-font-size: 16px;");

        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                qButton.setStyle("-fx-background-color: #002A45; -fx-font-size: 16px;");
                aButton.setStyle("-fx-background-color: #002A45; -fx-font-size: 16px;");
            });
        }).start();
    }

    private boolean isMatchingPair(String question, String answer) {
        for (String[] pair : pairs) {
            if (pair[0].equals(question) && pair[1].equals(answer)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
