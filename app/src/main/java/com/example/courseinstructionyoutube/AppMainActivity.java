package com.example.courseinstructionyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AppMainActivity extends AppCompatActivity {

    private Button watchYouTubeVideoBtn, listYouTubeChannelBtn, signOutButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_main);

        // Initialize buttons
        watchYouTubeVideoBtn = findViewById(R.id.watchYouTubeVideoBtn);
        listYouTubeChannelBtn = findViewById(R.id.listYouTubeChannelBtn);
        signOutButton = findViewById(R.id.sign_out_button);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Set click listener for Watch YouTube Video button
        watchYouTubeVideoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AppMainActivity.this, YouTubePlayerActivity.class);
            startActivity(intent);
        });

        // Set click listener for List YouTube Channel Videos button
        listYouTubeChannelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AppMainActivity.this, YouTubeChannelListActivity.class);
            startActivity(intent);
        });

        // Set click listener for Sign Out button
        signOutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();  // Sign out from Firebase
            Intent intent = new Intent(AppMainActivity.this, MainActivity.class);  // Go back to login
            startActivity(intent);
            finish();  // Close the current activity
        });
    }
}
