package com.example.kuizyjava_pbo2024.quizs;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class QuizResultActivity extends AppCompatActivity {

    private static final String TAG = "QuizResultActivity";
    private TextView quizNameTextView, correctAnswersTextView, wrongAnswersTextView, totalPointsTextView, totalXPTextView, dateTextView;
    private String quizId;
    private String quizName;
    private int correctAnswers;
    private int incorrectAnswers;
    private int totalPoints;
    private int totalXP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

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

        // Initialize views
        quizNameTextView = findViewById(R.id.quiz_name);
        correctAnswersTextView = findViewById(R.id.correct_answers);
        wrongAnswersTextView = findViewById(R.id.wrong_answers);
        totalPointsTextView = findViewById(R.id.total_points);
        totalXPTextView = findViewById(R.id.total_xp);
        dateTextView = findViewById(R.id.date);

        // Retrieve result data from intent
        Intent intent = getIntent();
        quizId = intent.getStringExtra("quizId");
        quizName = intent.getStringExtra("quizName");
        correctAnswers = intent.getIntExtra("correctAnswers", 0);
        incorrectAnswers = intent.getIntExtra("incorrectAnswers", 0);

        // Calculate total points and XP
        totalPoints = correctAnswers * 10; // Example calculation
        totalXP = correctAnswers * 20; // Example calculation

        // Display result data
        quizNameTextView.setText("Pilihan Quiz: " + quizName);
        correctAnswersTextView.setText("Benar: " + correctAnswers);
        wrongAnswersTextView.setText("Salah: " + incorrectAnswers);
        totalPointsTextView.setText("Point Terkumpul: " + totalPoints);
        totalXPTextView.setText("XP Point: " + totalXP);
        String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
        dateTextView.setText("Tanggal: " + currentDate);

        // Save result data to Firebase
        saveResultToFirebase();

        findViewById(R.id.button_mainlagi).setOnClickListener(v -> finish());
    }

    private void saveResultToFirebase() {
        // Retrieve the current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("currentUserId", "");

        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(currentUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int currentTotalSoal = snapshot.child("total_soal").getValue(Integer.class) != null ? snapshot.child("total_soal").getValue(Integer.class) : 0;
                    int currentPoints = snapshot.child("point").getValue(Integer.class) != null ? snapshot.child("point").getValue(Integer.class) : 0;
                    int currentXP = snapshot.child("xp").getValue(Integer.class) != null ? snapshot.child("xp").getValue(Integer.class) : 0;

                    int newTotalSoal = currentTotalSoal + (correctAnswers + incorrectAnswers);
                    int newPoints = currentPoints + totalPoints;
                    int newXP = currentXP + totalXP;

                    userRef.child("total_soal").setValue(newTotalSoal);
                    userRef.child("point").setValue(newPoints);
                    userRef.child("xp").setValue(newXP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }
}
