package compiler.answer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import util.Util;
import util.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class compilerAnswerController implements Initializable {

    @FXML
    private Tab resultTab;

    @FXML
    private Tab outputTab;

    Map<String, Object> questionMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        user.currentUser = "xien";
        user.isTeacher = true;
        user.currentClass = "Assembly Language";
        user.currentQuiz = "AightBro";

        new initRequireComponent();
        new initCompiler();

    }

    /*
        Test Button
    */

    @FXML
    TabPane tabPane;

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

            code = codeTA.getText();
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

            if(prepareStatementString.isEmpty()) {

                prepareStatementString = "insert into `" + user.currentQuiz + "List` (username, code, output) values (?, ?, ?)";
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

            // Check if the output is correct
            String expectedOutput = "";

            expectedOutput = (String) questionMap.get("output");
            for(int i = output.toString().length() - 1; i > 0; i--) {

                if(output.toString().charAt(i) == '\n') output.replace(i, i + 1, "");

            }

            String outputFinal = output.toString();
            if(expectedOutput.equals(outputFinal)) resultTA.setText("Passed!");
            else  resultTA.setText("Incorrect" + "\nExpected Output: " + expectedOutput + "\nYour Output: " + output.toString());

        }

        void scannerFn() throws Exception{

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


    class initRequireComponent {

        initRequireComponent() {

            getQuestionMap();
            init();

        }

        public void init() {

            quizTitleT.setText((String) questionMap.get("quiz title"));
            questionTA.setText((String) questionMap.get("quiz question"));
            expectedResultTA.setText((String) questionMap.get("output"));

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
    private TextArea codeTA;

    class initCompiler {

        String tempCode = "public class " + user.currentUser + " {\n" +
                "\tpublic static void main(String[] args) {\n" +
                "\t\tSystem.out.println(\"Hello World\");\n" +
                "\t}\n" +
                "}";

        initCompiler() {

            resultTA.setText("Compile Result");
            codeTA.setText(tempCode);

        }



    }

}
