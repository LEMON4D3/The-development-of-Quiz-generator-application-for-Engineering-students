package tof.answer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import util.quizCreateControllerExtend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TofAnswerController extends quizCreateControllerExtend {

    @FXML
    private Button backBtn, nextBtn, falseBtn, trueBtn;

    @FXML
    private TextArea questionTA;

    String isAnswer = null;

    @Override
    public void initQuiz(Map<String, Object> quizInfo) {
        questionTA.setText(quizInfo.get("quiz question").toString());
    }

    public void backBtnFn(ActionEvent e) {

        addUserAnswer();
        backListFn(e);

    }

    private void addUserAnswer() {

        List<String> answerList = new ArrayList<>();
        answerList.add(isAnswer);

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

    public void falseBtnFn(ActionEvent e) {

        isAnswer = "False";

        trueBtn.getStyleClass().removeAll("choiceBox-activated");
        falseBtn.getStyleClass().add("choiceBox-activate");

    }

    public void trueBtnFn(ActionEvent e) {

        isAnswer = "True";

        falseBtn.getStyleClass().removeAll("choiceBox-activated");
        trueBtn.getStyleClass().add("choiceBox-activate");

    }


}