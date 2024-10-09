package com.example.courseinstructionyoutube;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class YouTubePlayerActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_player);

        // Initialize YouTubePlayerView
        youTubePlayerView = findViewById(R.id.youtube_player_view);

        // Disable automatic initialization
        youTubePlayerView.setEnableAutomaticInitialization(false);

        // Add lifecycle observer for YouTubePlayerView
        getLifecycle().addObserver(youTubePlayerView);

        // Get the video ID from the Intent
        videoId = getIntent().getStringExtra("VIDEO_ID");

        // Initialize the YouTubePlayerView and load the video
        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                if (videoId != null && !videoId.isEmpty()) {
                    // Load the video using the passed video ID
                    youTubePlayer.loadVideo(videoId, 0);
                } else {
                    // Load a default video if no video ID is provided
                    youTubePlayer.loadVideo("tBv-4ttoyyc", 0); // Example video ID
                    Toast.makeText(YouTubePlayerActivity.this, "No video ID provided, playing default video", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (youTubePlayerView != null) {
            youTubePlayerView.release();
        }
    }
}
