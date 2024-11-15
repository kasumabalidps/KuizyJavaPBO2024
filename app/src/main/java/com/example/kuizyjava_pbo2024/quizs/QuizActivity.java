package com.example.kuizyjava_pbo2024.quizs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kuizyjava_pbo2024.R;
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
    private String category;

    /**
     * Initializes the Quiz activity and sets up the user interface.
     * 
     * This method is called when the activity is starting. It performs the following tasks:
     * - Sets up the activity layout
     * - Configures edge-to-edge content display based on the Android version
     * - Hides the action bar if present
     * - Initializes UI elements
     * - Retrieves quiz information from the intent
     * - Validates quiz data and finishes the activity if data is missing
     * - Sets up the Firebase database reference
     * - Initiates loading of quiz questions
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously being 
     *                           shut down, this Bundle contains the data it most recently supplied 
     *                           in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Enable edge-to-edge content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        // Hide the action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        textRemainingQuestions = findViewById(R.id.text_remaining_questions);
        textQuestion = findViewById(R.id.text_question);
        buttonOption1 = findViewById(R.id.button_option_1);
        buttonOption2 = findViewById(R.id.button_option_2);
        buttonOption3 = findViewById(R.id.button_option_3);
        buttonOption4 = findViewById(R.id.button_option_4);

        quizId = getIntent().getStringExtra("quizId");
        quizName = getIntent().getStringExtra("quizName");
        category = getIntent().getStringExtra("category");

        if (quizId == null || quizId.isEmpty() || quizName == null || quizName.isEmpty() || category == null || category.isEmpty()) {
            Toast.makeText(this, "Quiz ID, Name or Category is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Received quizId: " + quizId);

        mDatabase = FirebaseDatabase.getInstance("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("quiz/categories").child(category).child("quizzes").child(quizId).child("questions");

        loadQuestions();
    }

    /**
     * Loads quiz questions from the database and populates the questions list.
     * 
     * This method fetches question data from a Firebase Realtime Database using a
     * single value event listener. It populates the questions list with Question
     * objects, sets the total number of questions, and initiates the display of
     * the first question. If no questions are found, it shows a toast message and
     * finishes the activity.
     * 
     * @throws DatabaseException if there is an error while reading from the database
     */
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

    /**
     * Displays the current question and sets up answer buttons.
     * 
     * This method updates the UI with the current question's text and answer options.
     * It also sets up click listeners for each answer button to check the selected answer.
     * If there are no more questions, it shows the quiz result.
     * 
     * @throws IndexOutOfBoundsException if currentQuestionIndex is invalid
     */
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

    /**
     * Checks the user's selected answer against the correct answer for the current question.
     * 
     * This method compares the selected option with the correct answer for the given question.
     * It displays a toast message indicating whether the answer is correct or incorrect,
     * updates the score counters, increments the current question index, and displays the next question.
     * 
     * @param question The current Question object containing the correct answer
     * @param selectedOption The user's selected answer option as a String
     */
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
        intent.putExtra("category", category);
        startActivity(intent);
        finish();
    }
}
