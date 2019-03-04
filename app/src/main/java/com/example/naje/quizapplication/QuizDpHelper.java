package com.example.naje.quizapplication;

/*
* This class is used as a bridge with the database, it performs operations of creating, filling,
* and retrieving data from the database.
*/


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.naje.quizapplication.QuizContract.*;

import java.util.ArrayList;

public class QuizDpHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QuizApplicationDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;


    // Here is the constructor to create the database with given parameters

    public QuizDpHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.db = sqLiteDatabase;

        final String SQL_CREATE_QUESTION_TABLE = "CREATE TABLE " +
                QuestionTable.TABLE_NAME + " ( " +
                QuestionTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionTable.COLUMN_QUESTION + " TEXT, " +
                QuestionTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionTable.COLUMN_ANSWER_NUMBER + " INTEGER ) ";

        db.execSQL(SQL_CREATE_QUESTION_TABLE);
        fillQuestionTable();
    }

    /*
     A Method that saves data in form of objects of Question Class and pass these objects to
     addQuestion() method to be added to the database
    */
    private void fillQuestionTable() {
        Question q1 = new Question("A is correct", "A", "B", "C", 1);
        addQuestion(q1);
        Question q2 = new Question("B is correct", "A", "B", "C", 2);
        addQuestion(q2);
        Question q3 = new Question("C is correct", "A", "B", "C", 3);
        addQuestion(q3);
        Question q4 = new Question("A is correct again", "A", "B", "C", 1);
        addQuestion(q4);
        Question q5 = new Question("B is correct again", "A", "B", "C", 2);
        addQuestion(q5);
    }

    // A method to fill the database columns with data passed as objects of the Question Class
    private void addQuestion(Question question) {
        ContentValues content = new ContentValues();
        content.put(QuestionTable.COLUMN_QUESTION, question.getQuestion());
        content.put(QuestionTable.COLUMN_OPTION1, question.getOption1());
        content.put(QuestionTable.COLUMN_OPTION2, question.getOption2());
        content.put(QuestionTable.COLUMN_OPTION3, question.getOption3());
        content.put(QuestionTable.COLUMN_ANSWER_NUMBER, question.getAnswerNumber());

        db.insert(QuestionTable.TABLE_NAME, null, content);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + QuestionTable.TABLE_NAME);
        onCreate(db);
    }


    // A method to retrieve all the data in form of List
    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();

        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuestionTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do{
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(QuestionTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionTable.COLUMN_OPTION3)));
                question.setAnswerNumber(c.getInt(c.getColumnIndex(QuestionTable.COLUMN_ANSWER_NUMBER)));

                questionList.add(question);

            } while (c.moveToNext()) ;
        }
        c.close();
        return questionList;
    }
}