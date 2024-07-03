package com.example.kuizyjava_pbo2024.quizs;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kuizyjava_pbo2024.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuizResultActivity extends AppCompatActivity {

    private TextView textQuizName, textCorrectAnswers, textIncorrectAnswers, textTotalPoints, textXpPoints, textDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_result);

        textQuizName = findViewById(R.id.textQuizName);
        textCorrectAnswers = findViewById(R.id.textCorrectAnswers);
        textIncorrectAnswers = findViewById(R.id.textIncorrectAnswers);
        textTotalPoints = findViewById(R.id.textTotalPoints);
        textXpPoints = findViewById(R.id.textXpPoints);
        textDate = findViewById(R.id.textDate);

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

        // Ensure no padding is applied to the root layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get data from Intent
        String quizId = getIntent().getStringExtra("quizId");
        String quizName = getIntent().getStringExtra("quizName");
        int correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
        int incorrectAnswers = getIntent().getIntExtra("incorrectAnswers", 0);
        int totalPoints = correctAnswers * 10; // Example calculation
        int xpPoints = correctAnswers * 20; // Example calculation
        String date = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(new Date());

        // Display data
        textQuizName.setText("Pilihan Quiz: " + quizName);
        textCorrectAnswers.setText("Benar: " + correctAnswers);
        textIncorrectAnswers.setText("Salah: " + incorrectAnswers);
        textTotalPoints.setText("Point Terkumpul: " + totalPoints);
        textXpPoints.setText("XP Point: " + xpPoints);
        textDate.setText("Tanggal: " + date);
    }
}
