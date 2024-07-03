package com.example.kuizyjava_pbo2024.quizs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kuizyjava_pbo2024.BerandaActivity;
import com.example.kuizyjava_pbo2024.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuizHistoryActivity extends AppCompatActivity {

    private static final String TAG = "QuizHistoryActivity";
    private GridLayout gridLayout;
    private TextView totalPointsTextView;
    private TextView totalSoalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizHistoryActivity.this, BerandaActivity.class);
                startActivity(intent);
                finish();
            }
        });

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

        gridLayout = findViewById(R.id.grid_layout);
        totalPointsTextView = findViewById(R.id.text_view_point);
        totalSoalTextView = findViewById(R.id.text_view_soal);

        loadQuizHistory();
    }

    private void loadQuizHistory() {
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
                    int totalPoints = snapshot.child("point").getValue(Integer.class) != null ? snapshot.child("point").getValue(Integer.class) : 0;
                    int totalSoal = snapshot.child("total_soal").getValue(Integer.class) != null ? snapshot.child("total_soal").getValue(Integer.class) : 0;

                    totalPointsTextView.setText("Total Point : " + totalPoints);
                    totalSoalTextView.setText("Total Soal : " + totalSoal);

                    DataSnapshot quizHistorySnapshot = snapshot.child("quiz_history");
                    List<QuizHistoryItem> historyItems = new ArrayList<>();

                    for (DataSnapshot historySnapshot : quizHistorySnapshot.getChildren()) {
                        String quizName = historySnapshot.child("name").getValue(String.class);
                        String tanggal = historySnapshot.child("tanggal").getValue(String.class);
                        int pointDitambahkan = historySnapshot.child("point_ditambahkan").getValue(Integer.class) != null ? historySnapshot.child("point_ditambahkan").getValue(Integer.class) : 0;

                        if (quizName != null && tanggal != null) {
                            historyItems.add(new QuizHistoryItem(quizName, pointDitambahkan, tanggal, historySnapshot.getKey()));
                        }
                    }

                    // Sort the history items by the key in descending order
                    Collections.sort(historyItems, new Comparator<QuizHistoryItem>() {
                        @Override
                        public int compare(QuizHistoryItem o1, QuizHistoryItem o2) {
                            return Integer.parseInt(o2.getKey()) - Integer.parseInt(o1.getKey());
                        }
                    });

                    // Add the sorted items to the GridLayout
                    for (QuizHistoryItem item : historyItems) {
                        addHistoryItemToGridLayout(item.getQuizName(), item.getPointDitambahkan(), item.getTanggal());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        });
    }

    private void addHistoryItemToGridLayout(String quizName, int pointDitambahkan, String tanggal) {
        View historyItem = getLayoutInflater().inflate(R.layout.quiz_history_item, gridLayout, false);
        TextView textQuiz = historyItem.findViewById(R.id.text_quiz);

        String historyText = "Quiz : " + quizName + "\nPoint Terkumpul : " + pointDitambahkan + "\nTanggal : " + tanggal;
        textQuiz.setText(historyText);

        gridLayout.addView(historyItem);
    }

    private static class QuizHistoryItem {
        private String quizName;
        private int pointDitambahkan;
        private String tanggal;
        private String key;

        public QuizHistoryItem(String quizName, int pointDitambahkan, String tanggal, String key) {
            this.quizName = quizName;
            this.pointDitambahkan = pointDitambahkan;
            this.tanggal = tanggal;
            this.key = key;
        }

        public String getQuizName() {
            return quizName;
        }

        public int getPointDitambahkan() {
            return pointDitambahkan;
        }

        public String getTanggal() {
            return tanggal;
        }

        public String getKey() {
            return key;
        }
    }
}
