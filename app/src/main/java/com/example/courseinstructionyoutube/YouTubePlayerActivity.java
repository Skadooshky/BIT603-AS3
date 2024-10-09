package com.example.courseinstructionyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class YouTubePlayerActivity extends AppCompatActivity {

    private YouTubePlayerView youTubePlayerView;
    private EditText videoUrlEditText;
    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_player);

        // Initialize YouTubePlayerView
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        videoUrlEditText = findViewById(R.id.ytVideoUrlEt);
        playButton = findViewById(R.id.play_button);

        // Disable automatic initialization
        youTubePlayerView.setEnableAutomaticInitialization(false);
        getLifecycle().addObserver(youTubePlayerView);

        // Get the video ID passed from the list activity
        String videoId = getIntent().getStringExtra("VIDEO_ID");

        // Initialize YouTubePlayerView and load the selected video
        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                if (videoId != null) {
                    // Load video if a video ID is passed
                    youTubePlayer.loadVideo(videoId, 0);
                }

                playButton.setOnClickListener(v -> {
                    String videoUrl = videoUrlEditText.getText().toString().trim();
                    if (isValidYouTubeUrl(videoUrl)) {
                        String videoId = extractVideoId(videoUrl);
                        youTubePlayer.loadVideo(videoId, 0);
                    } else {
                        Toast.makeText(YouTubePlayerActivity.this, "Invalid YouTube URL. Please enter a valid URL.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button backToListButton = findViewById(R.id.back_to_list_button);
        backToListButton.setOnClickListener(v -> {
            Intent intent = new Intent(YouTubePlayerActivity.this, YouTubeChannelListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // This ensures that the current activity is finished
        });


    }

    private boolean isValidYouTubeUrl(String url) {
        return !TextUtils.isEmpty(url) && (url.contains("youtube.com/watch?v=") || url.contains("youtu.be/"));
    }

    private String extractVideoId(String url) {
        if (url.contains("youtube.com/watch?v=")) {
            return url.split("v=")[1].split("&")[0];
        } else if (url.contains("youtu.be/")) {
            return url.split("be/")[1].split("\\?")[0];
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (youTubePlayerView != null) {
            youTubePlayerView.release();
        }
    }

}
