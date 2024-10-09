package com.example.courseinstructionyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class YouTubePlayerActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView; // Player view to display YouTube video
    private EditText videoUrlEditText; // EditText for entering video URL
    private Button playButton; // Button to play the video from URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_player);

        // Initialize UI elements
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        videoUrlEditText = findViewById(R.id.ytVideoUrlEt);
        playButton = findViewById(R.id.play_button);

        // Disable automatic initialization of YouTubePlayerView
        youTubePlayerView.setEnableAutomaticInitialization(false);
        getLifecycle().addObserver(youTubePlayerView); // Ensures lifecycle awareness of the player

        // Get the video ID passed from the previous activity (if any)
        String videoId = getIntent().getStringExtra("VIDEO_ID");

        // Initialize the YouTubePlayerView
        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // If a video ID is provided, load that video
                if (videoId != null) {
                    youTubePlayer.loadVideo(videoId, 0);
                }

                // Set a click listener for the play button
                playButton.setOnClickListener(v -> {
                    String videoUrl = videoUrlEditText.getText().toString().trim();
                    if (isValidYouTubeUrl(videoUrl)) {
                        String videoId = extractVideoId(videoUrl); // Extract the video ID from URL
                        assert videoId != null;
                        youTubePlayer.loadVideo(videoId, 0); // Load the video in the player
                    } else {
                        Toast.makeText(YouTubePlayerActivity.this, "Invalid YouTube URL. Please enter a valid URL.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Set a listener on the "Back to List" button to go back to the video list
        Button backToListButton = findViewById(R.id.back_to_list_button);
        backToListButton.setOnClickListener(v -> {
            Intent intent = new Intent(YouTubePlayerActivity.this, YouTubeChannelListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); // Go back to the YouTube video list
            finish(); // Close the current activity
        });
    }

    // Validate if the URL is a valid YouTube link
    private boolean isValidYouTubeUrl(String url) {
        return !TextUtils.isEmpty(url) && (url.contains("youtube.com/watch?v=") || url.contains("youtu.be/"));
    }

    // Extract video ID from a YouTube URL
    private String extractVideoId(String url) {
        if (url.contains("youtube.com/watch?v=")) {
            return url.split("v=")[1].split("&")[0]; // Get the video ID from URL
        } else if (url.contains("youtu.be/")) {
            return url.split("be/")[1].split("\\?")[0];
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (youTubePlayerView != null) {
            youTubePlayerView.release(); // Release player resources
        }
    }
}
