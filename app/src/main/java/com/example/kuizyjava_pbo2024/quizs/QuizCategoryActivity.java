package com.example.kuizyjava_pbo2024.quizs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.kuizyjava_pbo2024.BerandaActivity;
import com.example.kuizyjava_pbo2024.R;
import com.example.kuizyjava_pbo2024.quizs.quizList.QuizListIPAActivity;
import com.example.kuizyjava_pbo2024.quizs.quizList.QuizListIPSActivity;
import com.example.kuizyjava_pbo2024.quizs.quizList.QuizListMatematikaActivity;
import com.example.kuizyjava_pbo2024.quizs.quizList.QuizListPPKNActivity;

public class QuizCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setContentView(R.layout.activity_category_quiz);

        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnMulaiQuizMatematika = findViewById(R.id.btnMulaiQuizMatematika);
        ImageButton btnMulaiQuizIPS = findViewById(R.id.btnMulaiQuizIPS);
        ImageButton btnMulaiQuizIPA = findViewById(R.id.btnMulaiQuizIPA);
        ImageButton btnMulaiQuizPPKN = findViewById(R.id.btnMulaiQuizPPKN);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizCategoryActivity.this, BerandaActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnMulaiQuizMatematika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizCategoryActivity.this, QuizListMatematikaActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnMulaiQuizIPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizCategoryActivity.this, QuizListIPAActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnMulaiQuizIPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizCategoryActivity.this, QuizListIPSActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnMulaiQuizPPKN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizCategoryActivity.this, QuizListPPKNActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Hide the action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Ensure no padding is applied to the root layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            return insets;
        });
    }
}
