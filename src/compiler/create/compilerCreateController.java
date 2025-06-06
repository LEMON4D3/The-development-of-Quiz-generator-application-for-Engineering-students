package compiler.create;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import util.Util;
import util.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

public class compilerCreateController implements Initializable {

    @FXML
    private Button backBtn;

    @FXML
    private TextArea printTA;

    @FXML
    private TextArea questionTA;

    @FXML
    private Button saveExerciseBtn;

    @FXML
    private GridPane scannerContainer;

    @FXML
    private Tab scannerTab, printTab;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        new initTab();
        new initScannerTab();

    }

    public void backBtnFn(ActionEvent event) {

        new controller().changeScene(event, "/classHomepage/teacher/Homepage.fxml");

    }

    @FXML
    private TextField quizTitleTF;

    public void saveExerciseBtnFn(ActionEvent event) {

        Map<String, Object> scannerFinal = new HashMap<>();

        scannerFinal.put("quiz title", quizTitleTF.getText());

        String practiceType = (currentTab == currentTabStatus.PRINT) ? "print" : "scanner";
        scannerFinal.put("practice type", practiceType);

        if (currentTab == currentTabStatus.SCANNER) {
            List<Map<String, Object>> getScannerList = new ArrayList<>();

            int lastRowIndex = scannerContainer.getRowCount() - 1;
            for (int i = lastRowIndex; i > 0; i--) {

                Map<String, Object> tempScanner = new HashMap<>();
                Node node1 = scannerContainer.getChildren().get(i * 2);
                Node node2 = scannerContainer.getChildren().get(i * 2 + 1);

                tempScanner.put("input", ((TextField) node1).getText());
                tempScanner.put("output", ((TextField) node2).getText());

                getScannerList.addFirst(tempScanner);
            }

            List<String> inputList = new ArrayList<>();
            List<String> outputList = new ArrayList<>();

            for (Map<String, Object> scanner : getScannerList) {

                inputList.add((String) scanner.get("input"));
                outputList.add((String) scanner.get("output"));

            }

            scannerFinal.put("input", inputList);
            scannerFinal.put("output", outputList);
            scannerFinal.put("data type", inputCombo.getValue());
            scannerFinal.put("quiz question", questionTA.getText());

        } else if (currentTab == currentTabStatus.PRINT) {

            scannerFinal.put("quiz question", questionTA.getText());
            scannerFinal.put("output", printTA.getText());

        }

        if(userChoice == quizType.NORMAL || !pastName.equals(quizTitleTF.getText())) {



            try {

            /*
                TODO:
                1. Insert the question into the class database
                2. Create New table for the coding exercise
                3. Insert the value of scannerFinal into the table

                table layout:
                - quiz title text
                - quiz question text
                - practice type text
                - data type text
                - input text
                - output text

             */

                Connection classConnectionDB = Util.getClassConnectionDB();

                // Insert the question into the class database
                String insertClassQuestionString = "insert into classes (announcement, isQuiz) values (?, ?)";
                PreparedStatement insertClassQuestionStatement = classConnectionDB.prepareStatement(insertClassQuestionString);

                insertClassQuestionStatement.setString(1, scannerFinal.get("quiz title").toString());
                insertClassQuestionStatement.setInt(2, 2);

                insertClassQuestionStatement.executeUpdate();

                // Create New table for the coding exercise
                String createTableString = "create table if not exists `" + scannerFinal.get("quiz title").toString() + "` (" +
                        "`quiz title` text primary key, " +
                        "`quiz question` text, " +
                        "`practice type` text, " +
                        "`data type` text, " +
                        "input text, " +
                        "output text)";
                Statement createTableStatement = classConnectionDB.createStatement();
                createTableStatement.execute(createTableString);

                // Insert the value of scannerFinal into the table
                String insertQuestionString = "insert into `" + scannerFinal.get("quiz title").toString() + "` " +
                        "(`quiz title`, `quiz question`, `practice type`, output , `data type`, input)" +
                        "values (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertQuestionStatement = classConnectionDB.prepareStatement(insertQuestionString);

                insertQuestionStatement.setString(1, scannerFinal.get("quiz title").toString());
                insertQuestionStatement.setString(2, scannerFinal.get("quiz question").toString());
                insertQuestionStatement.setString(3, scannerFinal.get("practice type").toString());
                insertQuestionStatement.setString(4, scannerFinal.get("output").toString());

                String dataTypeFinal = (scannerFinal.get("data type") == null) ? "" : scannerFinal.get("data type").toString();
                String inputFinal = (scannerFinal.get("input") == null) ? "" : scannerFinal.get("input").toString();

                insertQuestionStatement.setString(5, dataTypeFinal);
                insertQuestionStatement.setString(6, inputFinal);

                insertQuestionStatement.executeUpdate();

                // Create New Table will serve as a list
                String createListTableString = "create table if not exists `" + scannerFinal.get("quiz title").toString() + "List` (" +
                        "username text primary key" +
                        ")";
                Statement createListTableStatement = classConnectionDB.createStatement();
                createListTableStatement.execute(createListTableString);

                System.out.println("\n\nQuiz Created Successfully!");
                System.out.println("Quiz Title: " + scannerFinal.get("quiz title").toString());
                System.out.println("Quiz Question: " + scannerFinal.get("quiz question").toString());
                System.out.println("Practice Type: " + scannerFinal.get("practice type").toString());
                System.out.println("Data Type: " + dataTypeFinal);
                System.out.println("Input: " + inputFinal);
                System.out.println("Output: " + scannerFinal.get("output").toString());

                new controller().changeScene(event, "/classHomepage/teacher/Homepage.fxml");

            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }   
        else {
            
            try {
                
                Connection classConnectionDB = Util.getClassConnectionDB();
                
                String quizTitle = quizTitleTF.getText();
                String updateClassQuestionString = "update classes set announcement = ? where announcement = '" + quizTitle + "'";
                PreparedStatement updateClassQuestionStatement = classConnectionDB.prepareStatement(updateClassQuestionString);
                updateClassQuestionStatement.setString(1, quizTitle);
                updateClassQuestionStatement.executeUpdate();
                
                String updateQuestionString = "update `" + quizTitle + "` set `quiz title` = ?, `quiz question` = ?, `practice type` = ?, output = ?, `data type` = ?, input = ? where `quiz title` = '" + pastName + "'";
                PreparedStatement updateQuestionStatement = classConnectionDB.prepareStatement(updateQuestionString);

                updateQuestionStatement.setString(1, scannerFinal.get("quiz title").toString());
                updateQuestionStatement.setString(2, scannerFinal.get("quiz question").toString());
                updateQuestionStatement.setString(3, scannerFinal.get("practice type").toString());
                updateQuestionStatement.setString(4, scannerFinal.get("output").toString());

                String dataTypeFinal = (scannerFinal.get("data type") == null) ? "" : scannerFinal.get("data type").toString();
                String inputFinal = (scannerFinal.get("input") == null) ? "" : scannerFinal.get("input").toString();

                updateQuestionStatement.setString(5, dataTypeFinal);
                updateQuestionStatement.setString(6, inputFinal);

                updateQuestionStatement.executeUpdate();

                new controller().changeScene(event, "/classHomepage/teacher/Homepage.fxml");
                
                
            } catch (Exception exception) { exception.printStackTrace(); }
            
            
        }

    }

    quizType userChoice = quizType.NORMAL;
    String pastName = "";
    
    public enum quizType{
        EDIT,
        NORMAL
    }
    
    public void setEditContainer(Map<String, Object> codeMap) {

        new editComponent(codeMap);
        userChoice = quizType.EDIT;
        
        pastName = codeMap.get("quiz title").toString();
        quizTitleTF.setText(pastName);
        

    }

    @FXML
    TabPane containerTab;

    class editComponent {

        editComponent(Map<String, Object> codeMap) {

            questionTA.setText(codeMap.get("quiz question").toString());

            String practiceType = codeMap.get("practice type").toString();
            if(practiceType.equals("print")) {

                printTA.setText(codeMap.get("output").toString());
                return;

            } else
                containerTab.getSelectionModel().select(scannerTab);


            String inputString = codeMap.get("input").toString();
            String outputString = codeMap.get("output").toString();

            if(inputString.equals("[]")) inputString = "";
            else inputString = inputString.substring(1, inputString.length() - 1);

            if(outputString.equals("[]")) outputString = "";
            else  outputString = outputString.substring(1, outputString.length() - 1);

            List<String> inputList = List.of(inputString.split(","));
            List<String> outputList = List.of(outputString.split(","));

            for(int i = 0; i < inputList.size() && i < outputList.size(); i++) {

                String input = inputList.get(i).trim();
                String output = outputList.get(i).trim();

                TextField inputTF = new TextField(input);
                TextField outputTF = new TextField(output);

                inputTF.setOnKeyReleased(var -> {
                    Thread thread = new Thread(() -> {
                        String pastString = inputTF.getText();

                        try {

                            String inputResult = checkText(inputTF.getText());

                            if(!pastString.equals(inputResult)) {


                                inputTF.setText("Please enter a valid " + inputCombo.getValue());
                                inputTF.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                                inputTF.setEditable(false);
                                Thread.sleep(500);

                                inputTF.setEditable(true);
                                inputTF.setText(inputResult);
                                inputTF.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");

                            }

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                    });

                    thread.start();

                });

                scannerContainer.add(inputTF, 0, i + 1);
                scannerContainer.add(outputTF, 1, i + 1);

            }

        }

    }

    /*
    tab initialization function
    */

    currentTabStatus currentTab = currentTabStatus.PRINT;

    enum currentTabStatus{
        PRINT,
        SCANNER
    }

    class initTab {

        initTab() {

            printTab.setOnSelectionChanged(event -> tabBtnFn(event, currentTabStatus.PRINT));
            scannerTab.setOnSelectionChanged(event -> tabBtnFn(event, currentTabStatus.SCANNER));

        }

        public void tabBtnFn(Event event, currentTabStatus tabStatus) { currentTab = tabStatus;  }

    }

    /*
        scanner tab initialization function
     */

    @FXML Button scannerAddBtn;

    @FXML
    ComboBox<String> inputCombo;

    List<Map<String, Object>> scannerList = new ArrayList<>();

    class initScannerTab {

        initScannerTab() {

            initComboBox();
            scannerAddBtn.setOnAction(this::scannerAddBtnFn);

        }

        private void initComboBox() {

            ObservableList<String> inputList = FXCollections.observableArrayList("int", "float", "double", "char", "String");

            inputCombo.setItems(inputList);

            inputCombo.setValue("int");
        }


        private void scannerAddBtnFn(ActionEvent event) {

            Map<String, Object> scannerMap = new HashMap<>();

            TextField inputTF = new TextField();
            TextField outputTF = new TextField();

            inputTF.setOnKeyReleased(var -> {
                Thread thread = new Thread(() -> {
                    String pastString = inputTF.getText();

                    try {

                        String inputResult = checkText(inputTF.getText());

                        if(!pastString.equals(inputResult)) {


                            inputTF.setText("Please enter a valid " + inputCombo.getValue());
                            inputTF.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                            inputTF.setEditable(false);
                            Thread.sleep(500);

                            inputTF.setEditable(true);
                            inputTF.setText(inputResult);
                            inputTF.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");

                        }

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                });

                thread.start();

            });

            scannerMap.put("input", inputTF);
            scannerMap.put("output", outputTF);

            Node node = scannerContainer.getChildren().getLast();

            Integer rowIndex = GridPane.getRowIndex(node);
            rowIndex = (rowIndex == null) ? 0 : rowIndex;

            scannerContainer.add(inputTF, 0, rowIndex + 1);
            scannerContainer.add(outputTF, 1, rowIndex + 1);

            scannerList.add(scannerMap);

        }


    }

    String checkText(String text) {

        String dataTypeString = inputCombo.getValue();

        try {

            switch (dataTypeString) {
                case "int":
                    Integer.parseInt(text);
                    return text;
                case "float":
                    Float.parseFloat(text);
                    return text;
                case "double":
                    Double.parseDouble(text);
                    return text;
                case "char":
                    if (text.length() > 1) return text;
                default:
                    return text;
            }
        } catch (Exception e) {

            switch(dataTypeString) {
                case "int":
                    return text.replaceAll("[^0-9-]", "");
                case "char":
                    return text.charAt(0) + "";
                default:
                    text = text.replaceAll("[^0-9.-]", "");
                    return text;
            }

        }

    }


}
