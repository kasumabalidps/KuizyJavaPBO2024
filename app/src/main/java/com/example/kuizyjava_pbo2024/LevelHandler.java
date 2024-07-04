package com.example.kuizyjava_pbo2024;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LevelHandler {
    public static int calculateLevelProgress(int xp, int level) {
        int requiredXp = 100 * level;
        int previousRequiredXp = 100 * (level - 1);

        int currentLevelXp = xp - previousRequiredXp;
        int nextLevelXp = requiredXp - previousRequiredXp;

        return (int) ((currentLevelXp / (double) nextLevelXp) * 100);
    }

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

    public static void setProgressWidth(ImageView progressBar, int progress) {
        int maxWidth = 362;
        int progressWidth = (int) ((progress / 100.0) * maxWidth);

        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.width = progressWidth;
        progressBar.setLayoutParams(layoutParams);
    }
}

