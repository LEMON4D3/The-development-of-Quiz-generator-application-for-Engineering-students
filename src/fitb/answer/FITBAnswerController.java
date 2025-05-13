package fitb.answer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import util.quizCreateControllerExtend;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FITBAnswerController extends quizCreateControllerExtend {

    @FXML
    private Button backBtn, nextBtn;

    @FXML
    private Label hintT, questionT;

    @FXML
    private TextField answerTF;

    @FXML
    private TextArea questionTA;

    @Override
    public void initQuiz(Map<String, Object> quizInfo) {

        questionTA.setText((String) quizInfo.get("quiz question"));
        hintT.setText((String) quizInfo.get("quiz hint"));
        questionT.setText("Question " + (quizListScene.size() + 1));

    }

    public void backBtnFn(ActionEvent e) {

        addUserAnswer();
        backListFn(e);

    }

    private void addUserAnswer() {

        List<String> answerList = Arrays.asList(answerTF.getText());

        try {
            userAnswerList.set(quizIndex, answerList);
        } catch (Exception e) {
            userAnswerList.add(answerList);
        }

        System.out.println("User Answer: " + userAnswerList);

    }

    public void nextBtnFn(ActionEvent e) {

        addUserAnswer();
        nextListFn(e);

    }

}
