package com.example.kuizyjava_pbo2024.quizs.quizList;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageButton;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kuizyjava_pbo2024.R;
import com.example.kuizyjava_pbo2024.quizs.QuizActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizListMatematikaActivity extends AppCompatActivity {

    private static final String TAG = "QuizListMatematika";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list_matematika);

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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("quiz/categories/matematika/quizzes");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                        String quizId = quizSnapshot.getKey();
                        String quizName = quizSnapshot.child("name").getValue(String.class);
                        long questionCount = quizSnapshot.child("questions").getChildrenCount();

                        if (quizId != null && quizName != null) {
                            Log.d(TAG, "Quiz Name: " + quizName + ", Question Count: " + questionCount);
                            addQuizToGridLayout(quizId, quizName, questionCount);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void addQuizToGridLayout(String quizId, String quizName, long questionCount) {
        GridLayout gridLayout = findViewById(R.id.grid_layout);

        View quizItem = getLayoutInflater().inflate(R.layout.quiz_item_layout, gridLayout, false);
        TextView textQuizName = quizItem.findViewById(R.id.text_view_matematika);
        TextView textQuizDescription = quizItem.findViewById(R.id.text_view_deskripsi);
        ImageButton btnMulaiQuizMatematikaPertanyaan = quizItem.findViewById(R.id.btnMulaiQuizMatematikaPertanyaan);

        textQuizName.setText(quizName);
        textQuizDescription.setText(questionCount + " Soal");

        btnMulaiQuizMatematikaPertanyaan.setOnClickListener(v -> startQuizActivity(quizId, quizName));

        gridLayout.addView(quizItem);
    }

    private void startQuizActivity(String quizId, String quizName) {
        Log.d(TAG, "Starting QuizActivity with quizId: " + quizId);
        Intent intent = new Intent(QuizListMatematikaActivity.this, QuizActivity.class);
        intent.putExtra("quizId", quizId);
        intent.putExtra("quizName", quizName);
        startActivity(intent);
    }
}
