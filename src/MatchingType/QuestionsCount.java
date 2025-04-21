package Matching_Type;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.util.ArrayList;

public class QuestionsCount {

    public interface PairInputListener {
        void onPairsSubmitted(ArrayList<String[]> pairs);
    }

    public static void launchForm(PairInputListener listener) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Enter Pairs");

            VBox root = new VBox(10);
            root.setPadding(new Insets(15));
            root.setStyle("-fx-background-color: #003153;");

            // Top Panel
            HBox topPanel = new HBox(10);
            topPanel.setPadding(new Insets(10));
            topPanel.setStyle("-fx-background-color: #F0DC82; -fx-font-family: 'Arial Black'; -fx-font-size: 13px; -fx-font-weight: bold; ");
            Label countLabel = new Label("Number of Pairs:");
            TextField countField = new TextField();
            Button generateButton = new Button("Generate");
            topPanel.getChildren().addAll(countLabel, countField, generateButton);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            VBox formPanel = new VBox(10);
            formPanel.setStyle("-fx-background-color: #003153;");
            formPanel.setPadding(new Insets(10));
            formPanel.setFillWidth(true);
            scrollPane.setContent(formPanel);

            root.getChildren().addAll(topPanel, scrollPane);

            ArrayList<TextField[]> pairFields = new ArrayList<>();

            generateButton.setOnAction(e -> {
                formPanel.getChildren().clear();
                pairFields.clear();

                int count;
                try {
                    count = Integer.parseInt(countField.getText().trim());
                    if (count <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    showAlert("Please enter a valid number.");
                    return;
                }

                // Headers
                GridPane headerRow = new GridPane();
                headerRow.setHgap(10);
                Label qLabel = new Label("Question");
                qLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                Label aLabel = new Label("Answer");
                aLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                headerRow.add(qLabel, 0, 0);
                headerRow.add(aLabel, 32, 0);
                formPanel.getChildren().add(headerRow);

                // Input Fields
                for (int i = 0; i < count; i++) {
                    HBox row = new HBox(10);
                    row.setPadding(new Insets(5, 0, 5, 0));

                    TextField qField = new TextField();
                    TextField aField = new TextField();

                    qField.setPadding(new Insets(8));
                    aField.setPadding(new Insets(8));

                    qField.setMaxWidth(Double.MAX_VALUE);
                    aField.setMaxWidth(Double.MAX_VALUE);

                    HBox.setHgrow(qField, Priority.ALWAYS);
                    HBox.setHgrow(aField, Priority.ALWAYS);

                    pairFields.add(new TextField[]{qField, aField});
                    row.getChildren().addAll(qField, aField);
                    formPanel.getChildren().add(row);
                }

                Button startButton = new Button("Start Game");
                startButton.setStyle("-fx-background-color: #FFD300;");
                startButton.setOnAction(evt -> {
                    ArrayList<String[]> pairs = new ArrayList<>();
                    for (TextField[] fields : pairFields) {
                        String q = fields[0].getText().trim();
                        String a = fields[1].getText().trim();
                        if (q.isEmpty() || a.isEmpty()) {
                            showAlert("Please fill in all fields.");
                            return;
                        }
                        pairs.add(new String[]{q, a});
                    }

                    if (listener != null) {
                        listener.onPairsSubmitted(pairs);
                    }
                    stage.close();
                });

                formPanel.getChildren().add(startButton);
            });

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();
        });
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
