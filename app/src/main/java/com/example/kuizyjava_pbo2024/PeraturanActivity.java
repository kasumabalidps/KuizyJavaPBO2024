package com.example.kuizyjava_pbo2024;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PeraturanActivity extends AppCompatActivity {

    private CheckBox checkBox;
    private Button btnSetuju;

    /**
     * Initializes the activity, sets up the UI, and configures user interactions.
     * 
     * This method is called when the activity is first created. It performs the following tasks:
     * - Sets the content view to the activity_peraturan layout
     * - Enables edge-to-edge content display
     * - Hides the system UI (status and navigation bars)
     * - Hides the action bar if present
     * - Sets up window insets listener for proper layout adjustment
     * - Initializes UI elements (checkbox and button)
     * - Sets up click listener for the agreement button
     * 
     * @param savedInstanceState If the activity is being re-initialized after previously being 
     *                           shut down, this Bundle contains the data it most recently 
     *                           supplied in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peraturan);

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkBox = findViewById(R.id.checkBox);
        btnSetuju = findViewById(R.id.btnSetuju);

        btnSetuju.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                saveUserAgreementToFirebase(true);
            } else {
                Toast.makeText(PeraturanActivity.this, "Harap centang kotak persetujuan terlebih dahulu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Saves the user's agreement status to Firebase Realtime Database.
     * 
     * This method retrieves the current user ID from SharedPreferences,
     * then updates the user's agreement status in the Firebase database.
     * If successful, it displays a success message and navigates to the BerandaActivity.
     * If unsuccessful, it displays an error message.
     * 
     * @param agreed A boolean indicating whether the user has agreed to the terms
     * @return void This method does not return a value
     * 
     * @throws RuntimeException if the user ID is not found in SharedPreferences
     */
    private void saveUserAgreementToFirebase(boolean agreed) {
        // Retrieve the current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String currentUserId = sharedPreferences.getString("currentUserId", "");

        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "ID pengguna tidak ditemukan. Silakan masuk kembali.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(currentUserId);

        userRef.child("peraturan").setValue(agreed).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PeraturanActivity.this, "Persetujuan berhasil disimpan.", Toast.LENGTH_SHORT).show();
                // Navigate to BerandaActivity
                Intent intent = new Intent(PeraturanActivity.this, BerandaActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(PeraturanActivity.this, "Gagal menyimpan persetujuan. Silakan coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
