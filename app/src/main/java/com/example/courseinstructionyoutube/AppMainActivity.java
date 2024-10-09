package com.example.courseinstructionyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AppMainActivity extends AppCompatActivity {

    private Button watchYouTubeVideoBtn, listYouTubeChannelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);  // Layout for app features

        // Initialize buttons
        watchYouTubeVideoBtn = findViewById(R.id.watchYouTubeVideoBtn);
        listYouTubeChannelBtn = findViewById(R.id.listYouTubeChannelBtn);

        // Set click listener for Watch YouTube Video button
        watchYouTubeVideoBtn.setOnClickListener(v -> {
            // Navigate to YouTube Player Activity
            Intent intent = new Intent(AppMainActivity.this, YouTubePlayerActivity.class);
            startActivity(intent);
        });

        // Set click listener for List YouTube Channel Videos button
        listYouTubeChannelBtn.setOnClickListener(v -> {
            // Fetch YouTube Channel Info and Videos (Task 6 functionality)
            Intent intent = new Intent(AppMainActivity.this, YouTubeChannelListActivity.class);  // YouTube channel list activity
            startActivity(intent);
        });
    }
}
