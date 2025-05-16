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

import java.net.URL;
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
    ComboBox<String> inputCombo, outputCombo;

    List<Map<String, Object>> scannerList = new ArrayList<>();

    class initScannerTab {

        initScannerTab() {

            initComboBox();
            scannerAddBtn.setOnAction(this::scannerAddBtnFn);

        }

        private void initComboBox() {

            ObservableList<String> inputList = FXCollections.observableArrayList("int", "float", "double", "char", "String");
            ObservableList<String> outputList = FXCollections.observableArrayList("int", "float", "double", "char", "String");

            inputCombo.setItems(inputList);
            outputCombo.setItems(outputList);

            inputCombo.setValue("int");
            outputCombo.setValue("int");
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
