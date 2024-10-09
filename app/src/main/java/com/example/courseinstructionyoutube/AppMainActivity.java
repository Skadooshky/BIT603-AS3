package com.example.courseinstructionyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AppMainActivity extends AppCompatActivity {

    // Firebase Authentication instance to manage user sign-in and sign-out
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for the main activity
        setContentView(R.layout.activity_app_main);

        // Initialize buttons for the different features
        // Declare buttons for different actions in the main app
        Button watchYouTubeVideoBtn = findViewById(R.id.watchYouTubeVideoBtn);
        Button listYouTubeChannelBtn = findViewById(R.id.listYouTubeChannelBtn);
        Button signOutButton = findViewById(R.id.sign_out_button);

        // Initialize Firebase Authentication to manage user session
        firebaseAuth = FirebaseAuth.getInstance();

        // Set click listener for "Watch YouTube Video" button
        // When clicked, it navigates to the YouTubePlayerActivity
        watchYouTubeVideoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AppMainActivity.this, YouTubePlayerActivity.class);
            startActivity(intent);  // Start YouTube Player Activity
        });

        // Set click listener for "List YouTube Channel Videos" button
        // Navigates to YouTubeChannelListActivity to show the channel videos
        listYouTubeChannelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AppMainActivity.this, YouTubeChannelListActivity.class);
            startActivity(intent);  // Start YouTube Channel List Activity
        });

        // Set click listener for "Sign Out" button
        // Signs the user out of Firebase and returns to the login screen (MainActivity)
        signOutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();  // Sign out the user
            Intent intent = new Intent(AppMainActivity.this, MainActivity.class);
            startActivity(intent);  // Navigate back to the login screen
            finish();  // Close the current activity
        });
    }
}
