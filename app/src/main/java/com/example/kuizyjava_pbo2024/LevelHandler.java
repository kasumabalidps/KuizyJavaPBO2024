package com.example.kuizyjava_pbo2024;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LevelHandler {

    // Metode untuk menghitung level berdasarkan XP
    public static int[] calculateLevelAndRemainingXP(int xp, int currentLevel) {
        int level = currentLevel;
        int xpRequired = level * 100;

        while (xp >= xpRequired) {
            xp -= xpRequired;
            level++;
            xpRequired = level * 100;
        }

        return new int[]{level, xp};
    }

    // Metode untuk menghitung persentase kemajuan menuju level berikutnya
    public static int calculateLevelProgress(int xp, int currentLevel) {
        int level = currentLevel;
        int xpRequired = level * 100;

        while (xp >= xpRequired) {
            xp -= xpRequired;
            level++;
            xpRequired = level * 100;
        }

        return (int) (((double) xp / xpRequired) * 100);
    }

    // Metode untuk memperbarui level di Firebase
    public static void updateLevelInFirebase(String userId, int xp, int currentLevel) {
        int[] levelAndXp = calculateLevelAndRemainingXP(xp, currentLevel);
        int level = levelAndXp[0];
        int remainingXP = levelAndXp[1];

        DatabaseReference userRef = FirebaseDatabase.getInstance("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(userId);

        userRef.child("level").setValue(level).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userRef.child("xp").setValue(remainingXP).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Log.d("LevelHandler", "Level and XP updated successfully");
                    } else {
                        Log.e("LevelHandler", "Failed to update XP", task1.getException());
                    }
                });
            } else {
                Log.e("LevelHandler", "Failed to update level", task.getException());
            }
        });
    }

    // Metode untuk mengatur lebar progress bar berdasarkan persentase kemajuan
    public static void setProgressWidth(ImageView progressBar, int progress) {
        int maxWidth = 362; // Max width in dp
        int progressWidth = (int) ((progress / 100.0) * maxWidth);

        ViewGroup.LayoutParams params = progressBar.getLayoutParams();
        params.width = (int) (progressWidth * progressBar.getContext().getResources().getDisplayMetrics().density);
        progressBar.setLayoutParams(params);
    }
}
