package com.example.kuizyjava_pbo2024.quizs.quizList;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.example.kuizyjava_pbo2024.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizListMatematikaActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private GridLayout gridLayout;
    private static final String TAG = "QuizListMatematika";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list_matematika); // Ensure correct layout

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

        gridLayout = findViewById(R.id.grid_layout);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("quiz/categories/matematika/quizzes");

        // Load quiz list from Firebase
        loadQuizList();
    }

    private void loadQuizList() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "DataSnapshot exists");
                    for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                        String quizName = quizSnapshot.child("name").getValue(String.class);
                        long questionCount = quizSnapshot.child("questions").getChildrenCount();

                        Log.d(TAG, "Quiz Name: " + quizName + ", Question Count: " + questionCount);

                        addQuizToGridLayout(quizName, questionCount);
                    }
                } else {
                    Log.d(TAG, "No data found in database");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(QuizListMatematikaActivity.this, "Failed to load quiz list: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addQuizToGridLayout(String quizName, long questionCount) {
        // Inflate the quiz_item_layout dynamically
        View quizItemView = getLayoutInflater().inflate(R.layout.quiz_item_layout, gridLayout, false);

        TextView titleTextView = quizItemView.findViewById(R.id.text_view_matematika);
        TextView descriptionTextView = quizItemView.findViewById(R.id.text_view_deskripsi);

        titleTextView.setText(quizName);
        descriptionTextView.setText("Matematika\n" + questionCount + " Soal");

        Log.d(TAG, "Adding quiz item to grid layout");

        gridLayout.addView(quizItemView);
    }
}
