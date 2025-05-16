package compiler.create;

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

    @FXML
    private TextField quizTitleTF;

    public void saveExerciseBtnFn(ActionEvent event) {

        Map<String, Object> scannerFinal = new HashMap<>();

        scannerFinal.put("quiz title", quizTitleTF.getText());

        String practiceType = (currentTab == currentTabStatus.PRINT) ? "print" : "scanner";
        scannerFinal.put("practice type", practiceType);

        if(currentTab == currentTabStatus.SCANNER) {
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

            System.out.println("quiz question: " + scannerFinal.get("quiz question"));
            System.out.println("practice type: " + scannerFinal.get("practice type"));
            System.out.println("data type: " + scannerFinal.get("data type"));
            System.out.println("input: " + scannerFinal.get("input"));
            System.out.println("output: " + scannerFinal.get("output"));

        } else if(currentTab == currentTabStatus.PRINT) {

            scannerFinal.put("quiz question", questionTA.getText());
            scannerFinal.put("output", printTA.getText());

            System.out.println("quiz question: " + scannerFinal.get("quiz question"));
            System.out.println("practice type: " + scannerFinal.get("practice type"));
            System.out.println("output: " + scannerFinal.get("output"));

        }

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

        } catch (Exception exception) { exception.printStackTrace(); }


    }

    class dataLayout{

        enum practiceType{
            PRINT,
            SCANNER
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

}
