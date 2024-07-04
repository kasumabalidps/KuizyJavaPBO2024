package com.example.kuizyjava_pbo2024;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LevelHandler {

    private static final int XP_PER_LEVEL = 500;

    public static int calculateLevel(int totalXP) {
        int level = (totalXP / XP_PER_LEVEL) + 1;
        Log.d("LevelHandler", "Calculated level: " + level + " for total XP: " + totalXP);
        return level;
    }

    public static int calculateRemainingXP(int totalXP) {
        int remainingXP = totalXP % XP_PER_LEVEL;
        Log.d("LevelHandler", "Calculated remaining XP: " + remainingXP + " for total XP: " + totalXP);
        return remainingXP;
    }

    public static int calculateLevelProgress(int remainingXP) {
        int progress = (int) ((remainingXP / (double) XP_PER_LEVEL) * 100);
        Log.d("LevelHandler", "Calculated level progress: " + progress + "% for remaining XP: " + remainingXP);
        return progress;
    }

    public static void updateLevelInFirebase(String userId, int totalXP) {
        int level = calculateLevel(totalXP);
        int remainingXP = calculateRemainingXP(totalXP);
        int levelProgress = calculateLevelProgress(remainingXP);

        DatabaseReference userRef = FirebaseDatabase.getInstance("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(userId);

        Log.d("LevelHandler", "Updating user level: " + level + ", remaining XP: " + remainingXP + ", level progress: " + levelProgress);

        userRef.child("level").setValue(level).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userRef.child("xp").setValue(totalXP).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        userRef.child("levelProgress").setValue(levelProgress).addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                Log.d("LevelHandler", "Level, XP, and level progress updated successfully");
                            } else {
                                Log.e("LevelHandler", "Failed to update level progress", task2.getException());
                            }
                        });
                    } else {
                        Log.e("LevelHandler", "Failed to update XP", task1.getException());
                    }
                });
            } else {
                Log.e("LevelHandler", "Failed to update level", task.getException());
            }
        });
    }

    public static void setProgressWidth(ImageView progressBar, int progress) {
        int maxWidth = 362;  // width in dp
        int progressWidth = (int) ((progress / 100.0) * maxWidth);

        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.width = progressWidth;
        progressBar.setLayoutParams(layoutParams);
    }
}
