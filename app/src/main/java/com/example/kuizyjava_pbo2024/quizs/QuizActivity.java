package com.example.kuizyjava_pbo2024.quizs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kuizyjava_pbo2024.R;
import com.example.kuizyjava_pbo2024.quizs.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private TextView textRemainingQuestions, textQuestion;
    private Button buttonOption1, buttonOption2, buttonOption3, buttonOption4;
    private DatabaseReference mDatabase;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int totalQuestions;
    private int correctAnswers = 0;
    private int incorrectAnswers = 0;
    private String quizId;
    private String quizName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        textRemainingQuestions = findViewById(R.id.text_remaining_questions);
        textQuestion = findViewById(R.id.text_question);
        buttonOption1 = findViewById(R.id.button_option_1);
        buttonOption2 = findViewById(R.id.button_option_2);
        buttonOption3 = findViewById(R.id.button_option_3);
        buttonOption4 = findViewById(R.id.button_option_4);

        quizId = getIntent().getStringExtra("quizId");
        quizName = getIntent().getStringExtra("quizName");

        if (quizId == null || quizId.isEmpty() || quizName == null || quizName.isEmpty()) {
            Toast.makeText(this, "Quiz ID or Name is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Received quizId: " + quizId);

        mDatabase = FirebaseDatabase.getInstance("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("quiz/categories/matematika/quizzes").child(quizId).child("questions");

        loadQuestions();
    }

    private void loadQuestions() {
        questions = new ArrayList<>();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                        Question question = questionSnapshot.getValue(Question.class);
                        if (question != null) {
                            questions.add(question);
                        }
                    }
                    totalQuestions = questions.size();
                    displayQuestion();
                } else {
                    Toast.makeText(QuizActivity.this, "No questions found for this quiz", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, "Failed to load questions: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < totalQuestions) {
            Question question = questions.get(currentQuestionIndex);
            textRemainingQuestions.setText("Soal Tersisa: " + (totalQuestions - currentQuestionIndex));
            textQuestion.setText(question.getText());
            buttonOption1.setText(question.getOption1());
            buttonOption2.setText(question.getOption2());
            buttonOption3.setText(question.getOption3());
            buttonOption4.setText(question.getOption4());

            buttonOption1.setOnClickListener(v -> checkAnswer(question, buttonOption1.getText().toString()));
            buttonOption2.setOnClickListener(v -> checkAnswer(question, buttonOption2.getText().toString()));
            buttonOption3.setOnClickListener(v -> checkAnswer(question, buttonOption3.getText().toString()));
            buttonOption4.setOnClickListener(v -> checkAnswer(question, buttonOption4.getText().toString()));
        } else {
            showQuizResult();
        }
    }

    private void checkAnswer(Question question, String selectedOption) {
        if (question.getCorrectAnswer().equals(selectedOption)) {
            Toast.makeText(this, "Jawaban benar!", Toast.LENGTH_SHORT).show();
            correctAnswers++;
        } else {
            Toast.makeText(this, "Jawaban salah!", Toast.LENGTH_SHORT).show();
            incorrectAnswers++;
        }
        currentQuestionIndex++;
        displayQuestion();
    }

    private void showQuizResult() {
        Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
        intent.putExtra("quizId", quizId);
        intent.putExtra("quizName", quizName);
        intent.putExtra("correctAnswers", correctAnswers);
        intent.putExtra("incorrectAnswers", incorrectAnswers);
        startActivity(intent);
        finish();
    }
}
