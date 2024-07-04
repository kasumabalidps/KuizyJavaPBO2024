package com.example.kuizyjava_pbo2024;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LevelHandler {

    private static final int XP_PER_LEVEL = 500;

    public static int calculateLevel(int xp) {
        return (xp / XP_PER_LEVEL) + 1;
    }

    public static int calculateLevelProgress(int xp) {
        int currentLevelXp = xp % XP_PER_LEVEL;
        return (int) ((currentLevelXp / (double) XP_PER_LEVEL) * 100);
    }

    public static void updateLevelInFirebase(String userId, int xp) {
        int level = calculateLevel(xp);
        int remainingXP = xp % XP_PER_LEVEL;
        int levelProgress = calculateLevelProgress(xp);

        DatabaseReference userRef = FirebaseDatabase.getInstance("https://kuizy-pbo2024-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(userId);

        userRef.child("level").setValue(level).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userRef.child("xp").setValue(remainingXP).addOnCompleteListener(task1 -> {
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
        int maxWidth = 350;  // width in dp
        int progressWidth = (int) ((progress / 100.0) * maxWidth);

        ViewGroup.LayoutParams layoutParams = progressBar.getLayoutParams();
        layoutParams.width = progressWidth;
        progressBar.setLayoutParams(layoutParams);
    }
}
