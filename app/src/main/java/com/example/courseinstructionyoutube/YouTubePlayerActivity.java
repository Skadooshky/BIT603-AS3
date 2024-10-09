package com.example.courseinstructionyoutube;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubePlayerActivity extends AppCompatActivity {

    SignInCredential credential; // Declaring SignInCredential Variable
    YouTubePlayerView youTubePlayerView;
    EditText videoUrlEt;
    Button playBtn;
    String videoId;
    final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
    final String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_player);

        // Reading credential object from the calling intent
        credential = getIntent().getParcelableExtra("CREDENTIAL");

        // Displaying user's info in UI
        TextView nameTv = findViewById(R.id.userNameTV);
        ImageView avatarView = findViewById(R.id.avatarImage);

        nameTv.setText(credential.getDisplayName());
        Picasso.with(this).load(credential.getProfilePictureUri().toString()).into(avatarView);

        // Initializing the GUI widgets for Video Player and user input
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        videoUrlEt = findViewById(R.id.ytVideoUrlEt);
        playBtn = findViewById(R.id.ytPlayVideoBtn);

        // Disable automatic initialization of YouTubePlayerView
        youTubePlayerView.setEnableAutomaticInitialization(false);

        // Setup the click event for the play button
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to play the video
                playVideoButtonClick();
            }
        });

        // YouTubePlayer is a lifecycle aware widget
        // Adding a lifecycle observer so the video only plays when it is visible to the user
        getLifecycle().addObserver(youTubePlayerView);

        // Initialize the YouTubePlayerView and load the default video to play
        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                videoId = "tBv-4ttoyyc";  // Example video ID
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
    }

    // Helper method to extract the video ID from a YouTube URL
    private String extractVideoIdFromUrl(String url) {
        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDoman(url);

        for(String regex : videoIdRegex){
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);
            if(matcher.find()){
                return matcher.group(1);
            }
        }
        return null;
    }

    public String youTubeLinkWithoutProtocolAndDoman(String url){
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);
        if(matcher.find()){
            return url.replace(matcher.group(), "");
        }
        return url;
    }

    public void playVideoButtonClick(){
        String urlStr = videoUrlEt.getText().toString();
        if(urlStr.isEmpty()){
            videoId = "KAbJnGLDxnE";
        }else{
            videoId = extractVideoIdFromUrl(urlStr);
        }

        if(videoId!=null){
            youTubePlayerView.getYouTubePlayerWhenReady(this::playVideo);
        }else{
            Toast.makeText(this, "Enter a valid YouTube video URL to play a video", Toast.LENGTH_LONG).show();
        }
    }

    private void playVideo(YouTubePlayer player){
        player.loadVideo(videoId, 0);
    }
}
