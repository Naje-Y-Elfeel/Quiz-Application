package com.example.naje.quizapplication;

/*
* This Class is used to provide constants to the rest parts of the application
* It uses final so that it can not be subclassed
* It also uses private constructor so that it can not be instantiated
* It imports the BaseColumns class witch define the variable ID_ to be as the id column of the database
* */

import android.provider.BaseColumns;

public final class QuizContract {

    private QuizContract() {
    }

    public static class QuestionTable implements BaseColumns {

        public static final String TABLE_NAME = "quiz_questions";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION1 = "option1";
        public static final String COLUMN_OPTION2 = "option2";
        public static final String COLUMN_OPTION3 = "option3";
        public static final String COLUMN_ANSWER_NUMBER = "answer_number";
    }
}
