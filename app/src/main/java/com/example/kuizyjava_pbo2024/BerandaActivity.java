package com.example.kuizyjava_pbo2024;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.example.kuizyjava_pbo2024.quizs.QuizCategoryActivity;
import com.example.kuizyjava_pbo2024.quizs.QuizHistoryActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BerandaActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;

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

        setContentView(R.layout.activity_beranda);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        ImageButton btnMulaiQuiz = findViewById(R.id.btnMulaiQuiz);
        ImageButton btnAktivitas = findViewById(R.id.btnAktivitas);
        ImageButton btnPeraturan = findViewById(R.id.btnPeraturan);
        ImageButton btnKeluar = findViewById(R.id.btnKeluar);
        ImageButton btnGantiPassword = findViewById(R.id.btnGantiPassword);

        btnMulaiQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BerandaActivity.this, QuizCategoryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnAktivitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BerandaActivity.this, QuizHistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGantiPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BerandaActivity.this, GantiPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnPeraturan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the current user ID from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String currentUserId = sharedPreferences.getString("currentUserId", "");

                if (currentUserId.isEmpty()) {
                    Toast.makeText(BerandaActivity.this, "ID pengguna tidak ditemukan. Silakan masuk kembali.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference userRef = FirebaseDatabase.getInstance("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("users").child(currentUserId);

                userRef.child("peraturan").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean hasAgreed = dataSnapshot.getValue(Boolean.class);
                        if (hasAgreed != null && hasAgreed) {
                            Toast.makeText(BerandaActivity.this, "Kamu sudah menyetujui peraturan sebelumnya.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(BerandaActivity.this, PeraturanActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(BerandaActivity.this, "Gagal memeriksa status persetujuan.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Redirect to LoginActivity
                Intent intent = new Intent(BerandaActivity.this, LoginActivity.class);
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

        // Display the user's data
        displayUserData();
    }

    private void displayUserData() {
        final TextView subText = findViewById(R.id.subText);
        final TextView textInCircle = findViewById(R.id.text_in_circle);
        final ImageView profileImageView = findViewById(R.id.imageButton);
        final ImageView progressBar = findViewById(R.id.imageViewProgress);

        String currentUserId = sharedPreferences.getString("currentUserId", null);
        if (currentUserId != null) {
            DatabaseReference userRef = mDatabase.child("users").child(currentUserId);

            userRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        Integer level = dataSnapshot.child("level").getValue(Integer.class);
                        Integer levelProgress = dataSnapshot.child("levelProgress").getValue(Integer.class);
                        String profileUrl = dataSnapshot.child("profile_url").getValue(String.class);

                        if (username != null) {
                            subText.setText("Halo, " + username);
                        }

                        if (level != null) {
                            textInCircle.setText(String.valueOf(level));
                        }

                        if (profileUrl != null && !profileUrl.isEmpty()) {
                            Glide.with(BerandaActivity.this)
                                    .load(profileUrl)
                                    .into(profileImageView);
                        } else {
                            profileImageView.setImageResource(R.drawable.default_profile);
                        }

                        if (levelProgress != null) {
                            // Update progress bar
                            LevelHandler.setProgressWidth(progressBar, levelProgress);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(BerandaActivity.this, "Gagal memuat data pengguna: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
