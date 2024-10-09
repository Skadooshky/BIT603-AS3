package com.example.courseinstructionyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YouTubeChannelListActivity extends AppCompatActivity {

    // RecyclerView to display the list of videos
    private RecyclerView youtubeVideoRecyclerView;
    private YouTubeVideoAdapter adapter;  // Adapter to manage the video list
    private List<YouTubeVideosResponse.Item> videoList;  // List to hold video data
    private YouTubeApiService apiService;  // Service to interact with YouTube API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_channel_list);

        // Set up RecyclerView and its layout manager
        youtubeVideoRecyclerView = findViewById(R.id.youtubeVideoRecyclerView);
        youtubeVideoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add dividers between list items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));  // Set custom drawable as divider
        youtubeVideoRecyclerView.addItemDecoration(dividerItemDecoration);

        // Initialize the video list
        videoList = new ArrayList<>();

        // Initialize the YouTube API service
        apiService = RetrofitClient.getYouTubeApiService();

        // Fetch the videos and channel details
        fetchVideosFromYouTubeApi();
        fetchChannelDetails();  // Fetches channel name to display in the banner
    }

    // Method to fetch the list of videos from the YouTube API
    private void fetchVideosFromYouTubeApi() {
        // Retrieve YouTube API key from the resources
        String apiKey = getString(R.string.youtube_api_key);

        // API call to get video list from the specified channel
        Call<YouTubeVideosResponse> call = apiService.getVideosList(
                "snippet",
                "UC7_YxT-KID8kRbqZo7MyscQ",  // Channel ID
                50,  // Number of videos to fetch
                "date",  // Order videos by date
                apiKey  // Pass API key
        );

        // Handle API response asynchronously
        call.enqueue(new Callback<YouTubeVideosResponse>() {
            @Override
            public void onResponse(Call<YouTubeVideosResponse> call, Response<YouTubeVideosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // If the response is successful, add videos to the list
                    videoList.addAll(response.body().items);
                    setupRecyclerView();  // Set up RecyclerView to display the videos
                    Log.d("YouTube API", "Videos fetched successfully");
                } else {
                    // Log an error if the response was not successful
                    Log.e("YouTube API", "Failed to retrieve videos: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<YouTubeVideosResponse> call, Throwable t) {
                // Log any errors that occur during the API call
                Log.e("YouTube API", "Error: " + t.getMessage());
            }
        });
    }

    // Method to set up RecyclerView with the video list
    private void setupRecyclerView() {
        adapter = new YouTubeVideoAdapter(this, videoList, this::openYouTubePlayer);
        youtubeVideoRecyclerView.setAdapter(adapter);
    }

    // Method to handle video selection and open the YouTubePlayerActivity
    private void openYouTubePlayer(String videoId) {
        Intent intent = new Intent(YouTubeChannelListActivity.this, YouTubePlayerActivity.class);
        intent.putExtra("VIDEO_ID", videoId);  // Pass the selected video ID to the player activity
        startActivity(intent);  // Start the YouTube player activity
    }

    // Method to fetch channel details like the name using the channel ID
    private void fetchChannelDetails() {
        String apiKey = getString(R.string.youtube_api_key);  // Fetch API key from resources

        // API call to get channel details
        Call<YouTubeChannelResponse> call = apiService.getChannelDetails(
                "snippet",
                "UC7_YxT-KID8kRbqZo7MyscQ",  // Channel ID
                apiKey
        );

        // Handle API response asynchronously
        call.enqueue(new Callback<YouTubeChannelResponse>() {
            @Override
            public void onResponse(Call<YouTubeChannelResponse> call, Response<YouTubeChannelResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extract and display the channel name in the UI
                    String channelName = response.body().items.get(0).snippet.title;
                    displayChannelName(channelName);  // Method to display channel name in the banner
                } else {
                    // Log an error if fetching channel details fails
                    Log.e("YouTube API", "Failed to retrieve channel details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<YouTubeChannelResponse> call, Throwable t) {
                // Log any errors during the API call
                Log.e("YouTube API", "Error: " + t.getMessage());
            }
        });
    }

    // Method to update the channel name banner
    private void displayChannelName(String channelName) {
        TextView channelNameBanner = findViewById(R.id.channel_name_banner);  // Get the banner TextView
        channelNameBanner.setText(channelName);  // Set the channel name in the banner
    }
}
