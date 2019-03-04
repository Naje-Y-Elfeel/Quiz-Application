package com.example.naje.quizapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extraScore";
    public static final long COUNT_DOWN_IN_MILLIS = 30000;

    public static final String KEY_SCORE = "keyScore";
    public static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    public static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    public static final String KEY_ANSWERED = "keyAnswered";
    public static final String KEY_QUESTION_LIST = "keyQuestionList";

    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCountDown;
    private TextView textViewQuestion;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean is_answered;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textViewQuestion = findViewById(R.id.question);
        textViewScore = findViewById(R.id.score_view);
        textViewQuestionCount = findViewById(R.id.question_no);
        textViewCountDown = findViewById(R.id.count_down);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.option1);
        rb2 = findViewById(R.id.option2);
        rb3 = findViewById(R.id.option3);
        buttonConfirmNext = findViewById(R.id.button_confirm);

        // saves the color of rb1, and textViewCountDown as the default color
        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCd = textViewCountDown.getTextColors();

        if(savedInstanceState == null) {
            // Here the database is created with this as context
            QuizDpHelper quizDpHelper = new QuizDpHelper(this);

            //Retrieve all the questions and put them in list
            questionList = quizDpHelper.getAllQuestions();

            // The number of questions
            questionCountTotal = questionList.size();

            // put questions in random order
            Collections.shuffle(questionList);

            showNextQuestion();

        }
        else{
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            questionCountTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionList.get(questionCounter - 1);
            score =savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            is_answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            if(!is_answered){
                startCountDown();
            }else{
                updateCountDownText();
                showSolution();
            }
        }

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_answered) {
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, "Please Select one answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });
    }

    /*
     A method is used to show the questions, it first sets the the radioButtons
     colors to the default color and unCheck the radioGroup, then it check if the current question is the last question of not,
     after that it gets the Question from the list and sets the question and the different options, increment questionCounter because it
      starts with zero, shows it in the textViewQuestionCount,is_answered to false because we didn't
      answer yet, change the text of the buttonConfirmNext,
      and if there was new question it will ends the Quiz.
    */
    private void showNextQuestion() {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

        if(questionCounter < questionCountTotal){
            currentQuestion = questionList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            questionCounter++;
            textViewQuestionCount.setText("Question: "+ questionCounter + "/ " + questionCountTotal);
            is_answered = false;
            buttonConfirmNext.setText("Confirm");

            timeLeftInMillis = COUNT_DOWN_IN_MILLIS;
            startCountDown();

        }else{
            finishQuiz();
        }
    }

    private void startCountDown(){
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText(){
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textViewCountDown.setText(timeFormatted);

        if(timeLeftInMillis < 10000){
            textViewCountDown.setTextColor(Color.RED);
        }else{
            textViewCountDown.setTextColor(textColorDefaultCd);
        }
    }

    /*
    * This method is used to check the answer of the selected option, it first sets is_answered to true
    * and then gets the id of the selected radioButton and convert it into integer, and increment
    * with one because it starts with zero
    * Lastly if the answer_number is equal to the right answer then increments the score and show it
    * in the textViewScore, if it was not correct it will show the right answer
    * */
    private void checkAnswer() {
        is_answered = true;

        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answer_number = rbGroup.indexOfChild(rbSelected) + 1;

        if(answer_number == currentQuestion.getAnswerNumber()){
            score++;
            textViewScore.setText("Score :"+score);
            Toast.makeText(this, "Good", Toast.LENGTH_SHORT).show();
        }

        showSolution();
    }

    /*
    * This method will first sets all the radioButtons to red and then get the right answer and sets
    * its color to green and show it, lastly it will check if this question was the last question or not
    * */
    private void showSolution() {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);

        switch (currentQuestion.getAnswerNumber()){
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 1 is Correct");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 2 is Correct");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 3 is Correct");
                break;
        }

        if(questionCounter < questionCountTotal){
            buttonConfirmNext.setText("Next");
        }else{
            buttonConfirmNext.setText("Finish");
        }
    }

    // A method to finish the quiz
    private void finishQuiz() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        
        if( backPressedTime + 2000 > System.currentTimeMillis() ){
            finishQuiz();
        } else{
            Toast.makeText(this, "Press Back Again To Finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!= null){
            countDownTimer.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis);
        outState.putBoolean(KEY_ANSWERED, is_answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}
