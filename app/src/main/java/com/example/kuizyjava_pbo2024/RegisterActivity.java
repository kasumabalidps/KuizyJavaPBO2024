package com.example.kuizyjava_pbo2024;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        Button btnRegister = findViewById(R.id.btnRegister);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        setOnEditorActionListeners(etUsername);
        setOnEditorActionListeners(etEmail);
        setOnEditorActionListeners(etPassword);
        setOnEditorActionListeners(etConfirmPassword);

        // Firebase Database Reference
        FirebaseDatabase.getInstance().getReferenceFromUrl("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/");
        database = FirebaseDatabase.getInstance().getReference("users");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Mohon lengkapi form", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Password tidak sama!!", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(email, username, password);
                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void registerUser(String email, String username, String password) {
        DatabaseReference usersChildName = database.child(username);

        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("username", username);
        userData.put("password", password);
        userData.put("level", 0);
        userData.put("xp", 0);
        userData.put("point", 0);
        userData.put("profile_url", "");

        Map<String, Object> quizHistory1 = new HashMap<>();
        quizHistory1.put("quiz_name", "");
        quizHistory1.put("quiz_history_time", "");

        Map<String, Object> quizHistory2 = new HashMap<>();
        quizHistory2.put("quiz_name", "");
        quizHistory2.put("quiz_history_time", "");

        Map<String, Object> quizHistory = new HashMap<>();
        quizHistory.put("1", quizHistory1);
        quizHistory.put("2", quizHistory2);

        userData.put("quiz_history", quizHistory);

        usersChildName.setValue(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Berhasil mendaftar", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Gagal mendaftar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setOnEditorActionListeners(EditText editText) {
        editText.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                hideKeyboard(v);
                return true;
            }
            return false;
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onLoginClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
