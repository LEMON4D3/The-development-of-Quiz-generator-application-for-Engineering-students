package compiler.answer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import util.Util;
import util.controller;
import util.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class compilerAnswerController implements Initializable {

    @FXML
    private Tab resultTab;

    @FXML
    private Tab outputTab;

    @FXML private GridPane mainPane;

    Map<String, Object> questionMap = new HashMap<>();
    Stage mainStage = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        new initRequireComponent();
        new initCompiler();

        Platform.runLater(() -> mainStage = new controller().getStage((Node) mainPane.getChildren().getFirst()));

    }

    public void backBtnFn(ActionEvent event) {

        new controller().changeScene(event, "/classHomepage/student/Homepage.fxml");

    }

    /*
        Test Button
    */

    @FXML
    TabPane tabPane;

    @FXML
    Button testBtn;

    public void testBtnFn(ActionEvent event) throws Exception {


       /*
       else {

            expectedOutput = questionMap.get("output").toString().replace("[", "").replace("]", "");

            List<String> expectedOutputList = List.of(expectedOutput.split(","));


        }
        */

        boolean isPrint = (questionMap.get("practice type").toString().equals("print")) ? true : false;
        new checkUser(isPrint);

    }

    class checkUser {

        String userJavaPath = "application/classes/" + user.currentClass + "/java/" + user.currentQuiz + "/";
        String userJava = userJavaPath + user.currentUser + ".java";
        String code = "";

        File file = null;

        checkUser(boolean isPrint) throws Exception {

            createFile();
            compileCode();

            if(isPrint) printFn();
            else scannerFn();

            tabPane.getSelectionModel().select(resultTab);

        }

        void createFile() throws Exception{

            File filePath = new File(userJavaPath);
            if (!filePath.exists() && !filePath.isDirectory())
                filePath.mkdirs();

            file = new File(userJava);
            file.createNewFile();

            code = codeCA.getText();
            FileWriter writer = new FileWriter(file);
            writer.write(code);
            writer.close();

        }

        void compileCode() throws Exception{

            // Compile the code
            ProcessBuilder processBuilder = new ProcessBuilder("javac", file.getAbsolutePath());
            processBuilder.directory(new File(userJavaPath));
            Process process = processBuilder.start();

            StringBuilder errorOutput = new StringBuilder();
            try (BufferedReader reader = new BufferedReader( new InputStreamReader(process.getErrorStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    errorOutput.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {

                resultTA.setText(errorOutput.toString());
                return;

            }

        }

        void printFn() throws Exception {

            // Run the compiled code
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", file.getAbsolutePath());
            runProcessBuilder.directory(new File(userJavaPath));
            Process runProcess = runProcessBuilder.start();

            // Read the output
            StringBuilder output = new StringBuilder();
            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(runProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int runExitCode = runProcess.waitFor();
            if (runExitCode == 0) {

                resultTA.setText(output.toString());

            }

            // Input the code into the user.currentQuiz + "List" Table
            List<Map<String, Object>> classList = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz + "List", false);
            String prepareStatementString = "";
            for(Map<String, Object> username : classList) {
                if(username.get("username").equals(user.currentUser))
                    prepareStatementString = "update `" + user.currentQuiz + "List` set code = ?, output = ? where username = ?";
            }

            // Check if the output is correct
            String expectedOutput = "";

            expectedOutput = (String) questionMap.get("output");
            for(int i = output.toString().length() - 1; i > 0; i--) {

                if(output.toString().charAt(i) == '\n') output.replace(i, i + 1, "");

            }

            String outputFinal = output.toString();
            if(expectedOutput.contains(outputFinal)) passPopUp();
            else  {

                resultTA.setText("Incorrect" + "\nExpected Output: " + expectedOutput + "\nYour Output: " + outputFinal);
                return;

            }

            if(prepareStatementString.isEmpty()) {

                prepareStatementString = "insert into `" + user.currentQuiz + "List` (username) values (?, ?, ?)";
                PreparedStatement insertStatement = Util.getClassConnectionDB().prepareStatement(prepareStatementString);
                insertStatement.setString(1, user.currentUser);
                insertStatement.setString(2, code);
                insertStatement.setString(3, output.toString());
                insertStatement.executeUpdate();
                return;

            }

            PreparedStatement updateStatement = Util.getClassConnectionDB().prepareStatement(prepareStatementString);
            updateStatement.setString(1, code);
            updateStatement.setString(2, output.toString());
            updateStatement.executeUpdate();



        }

        void  scannerFn() throws Exception{

            // Get the input
            List<Map<String, Object>> classList = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz, false);
            Map<String, Object> classListMap = classList.get(0);

            String dataType = classListMap.get("data type").toString();

            String input = classListMap.get("input").toString().substring(1, classList.get(0).get("input").toString().length() - 1);
            String expectedOutput = classListMap.get("output").toString().substring(1, classList.get(0).get("output").toString().length() - 1);

            List<Object> inputList = new ArrayList<>();
            if(dataType.equals("int")) {
                for(String inputString : input.split(",")) {
                    inputList.add(Integer.parseInt(inputString.replaceAll("[^0-9]", "")));
                }
            } else if(dataType.equals("double")) {
                for(String inputString : input.split(",")) {
                    inputList.add(Double.parseDouble(inputString.replaceAll("[^0-9].", "")));
                }
            } else if(dataType.equals("String")) {
                for(String inputString : input.split(",")) {
                    inputList.add(inputString);
                }
            }

            List<String> expectedOutputList = List.of(expectedOutput.split(","));

            // Run the compiled code

            StringBuilder totalOutput = new StringBuilder();
            for(int i = 0; i < expectedOutputList.size() && i < inputList.size(); i++) {

                Object inputString = inputList.get(i);
                String expectedString = expectedOutputList.get(i);

                ProcessBuilder runProcessBuilder = new ProcessBuilder("java", file.getAbsolutePath());
                runProcessBuilder.directory(new File(userJavaPath));
                Process runProcess = runProcessBuilder.start();

                // Send input to the process
                try (var writer = new java.io.OutputStreamWriter(runProcess.getOutputStream())) {
                    writer.write(inputString + "");
                    writer.flush();
                }

                // Read the output
                StringBuilder output = new StringBuilder();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(runProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                // Also check for errors
                StringBuilder errorOutput = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(runProcess.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                }

                int runExitCode = runProcess.waitFor();
                if (runExitCode != 0) {
                    resultTA.setText("Error: " + errorOutput.toString());
                    return;
                }

                totalOutput.append("Input: ").append(inputString).append("\n");
                totalOutput.append("Output: ").append(output).append("\n\n");

                for(int a = output.toString().length() - 1; a > 0; a--) {

                    if(output.toString().charAt(a) == '\n') output.replace(a, a + 1, "");

                }

                System.out.println("Input: " + inputString);
                System.out.println("Output: " + output);

                if(!expectedString.contains(output.toString())) {
                    resultTA.setStyle("-fx-text-fill: red;");
                    resultTA.setText("Incorrect" + "\nExpected Output: " + expectedString + "\nYour Output: " + output.toString());
                    return;
                }



            }

            passPopUp();

        }

        void passPopUp() {

            Stage miniStage = new Stage();
            miniStage.initModality(Modality.APPLICATION_MODAL);
            miniStage.initOwner(mainStage);

            VBox miniPane = new VBox();
            miniPane.setAlignment(Pos.CENTER);

            Label passedT = new Label("Passed!");
            passedT.setStyle("-fx-text-fill: green; -fx-font-size: 20px;");

            Button okBtn = new Button("OK");
            okBtn.setOnAction(event -> {

                miniStage.close();
                insertName();

            });

            miniPane.getChildren().addAll(passedT, okBtn);
            miniStage.setScene(new Scene(miniPane, 300, 200));
            miniStage.showAndWait();

            testBtn.setDisable(true);
            testBtn.setText("Passed");

            resultTA.setText("Passed!");
            resultTA.setStyle("-fx-text-fill: green;");

        }

        void insertName() {

            try {

                Connection classConnection = Util.getClassConnectionDB();

                String insertName = "insert into `" + user.currentQuiz + "List` (username) values (?)";
                PreparedStatement insertStatement = classConnection.prepareStatement(insertName);
                insertStatement.setString(1, user.currentUser);
                insertStatement.executeUpdate();

            } catch (Exception exception) { exception.printStackTrace(); }

        }

    }

    /*
        Initialization Section
    */

    @FXML
    private Label quizTitleT;

    @FXML
    private TextArea questionTA;

    @FXML
    private TextArea expectedResultTA;

    @FXML
    HBox upperPane;

    class initRequireComponent {

        initRequireComponent() {

            getQuestionMap();
            init();

        }

        public void init() {

            quizTitleT.setText((String) questionMap.get("quiz title"));
            questionTA.setText((String) questionMap.get("quiz question"));
            expectedResultTA.setText((String) questionMap.get("output"));

            String practiceType = questionMap.get("practice type").toString();
            if(practiceType.equals("print")) printFn();
            else scannerFn();

        }

        private void printFn() {

            Label printLabel = new Label("Practice Type: " + questionMap.get("practice type"));
            printLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            upperPane.getChildren().add(printLabel);

        }

        private void scannerFn() {

            Label scannerLabel = new Label("Practice Type: " + questionMap.get("practice type"));
            scannerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            upperPane.getChildren().add(scannerLabel);

            Label dataTypeLabel = new Label("Data Type: " + questionMap.get("data type"));
            dataTypeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            upperPane.getChildren().add(dataTypeLabel);

        }

        public void getQuestionMap() {

            try {

                List<Map<String, Object>> classList = Util.getQuizOrAnnouncementClassListDB(user.currentQuiz, false);
                questionMap = classList.getFirst();

            } catch (Exception exception) { exception.printStackTrace(); }

        }

    }

    /*
        Compiler Section
    */

    @FXML
    private TextArea resultTA;

    @FXML
    private VBox codeContainer;

    CodeArea codeCA = new CodeArea();

    class initCompiler {

        String initCode = "import java.util.Scanner;\n\n" +
                "public class " + user.currentUser + " {\n" +
                "\tpublic static void main(String[] args) {\n" +
                "\t\tScanner sc = new Scanner(System.in);\n" +
                "\t\t// Your code here\n" +
                "\t}\n" +
                "}";

        initCompiler() {

            resultTA.setText("Compile Result");
            resultTA.setText("Compile Result");

            codeCA.setParagraphGraphicFactory(LineNumberFactory.get(codeCA));
            codeCA.replaceText(0, 0, initCode);
            codeCA.getStylesheets().add(getClass().getResource("/compiler/answer/CodeAreaStyle.css").toExternalForm());
            codeCA.setStyle("-fx-font-family: 'monospace'; -fx-font-size: 12px;");
            codeCA.setPrefHeight(400);
            VBox.setVgrow(codeCA, Priority.ALWAYS);

            VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(codeCA);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            codeContainer.getChildren().clear();
            codeContainer.getChildren().add(scrollPane);

            codeCA.textProperty().addListener((obs, oldText, newText) -> {
                codeCA.setStyleSpans(0, computeHighlighting(newText));
            });

            codeCA.setStyleSpans(0, computeHighlighting(codeCA.getText()));

            codeCA.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {

                if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {

                    int caretPos = codeCA.getCaretPosition();
                    int currentParagraph = codeCA.getCurrentParagraph();
                    if (currentParagraph > 0) {
                        String prevLine = codeCA.getParagraph(currentParagraph - 1).getSegments().getFirst().toString();
                        StringBuilder leadingTabs = new StringBuilder();
                        for (char c : prevLine.toCharArray()) {
                            if (c == '\t') leadingTabs.append('\t');
                            else break;
                        }

                        Platform.runLater(() -> {
                            codeCA.insertText(codeCA.getCaretPosition(), leadingTabs.toString());
                        });
                    }
                }
            });

        }

        private static final String[] KEYWORDS = {
                "abstract", "assert", "break", "byte",
                "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else",
                "enum", "extends", "final", "finally", "float",
                "for", "if", "goto", "implements", "import",
                "instanceof", "int", "interface", "long", "native",
                "new", "package", "private", "protected", "public",
                "return", "short", "static", "strictfp", "super",
                "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while"
        };

        private static final String[] DATATYPE = {
                "String", "int", "double", "float", "char",
                "boolean", "byte", "short", "long",
                "Scanner"
        };

        private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
        private static final String DATATYPE_PATTERN = "\\b(" + String.join("|", DATATYPE) + ")\\b";
        private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
        private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

        private static final Pattern PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                        + "|(?<DATATYPE>" + DATATYPE_PATTERN + ")"
                        + "|(?<STRING>" + STRING_PATTERN + ")"
                        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        );

        private StyleSpans<Collection<String>> computeHighlighting(String text) {
            StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

            Matcher matcher = PATTERN.matcher(text);
            int lastKwEnd = 0;

            while(matcher.find()) {
                String styleClass =
                        matcher.group("KEYWORD") != null ? "keyword" :
                                matcher.group("STRING") != null ? "string" :
                                        matcher.group("COMMENT") != null ? "comment" :
                                                matcher.group("DATATYPE") != null ? "datatype" :
                                                null;
                assert styleClass != null;

                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();
            }
            spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);

            return spansBuilder.create();
        }
    }

}
