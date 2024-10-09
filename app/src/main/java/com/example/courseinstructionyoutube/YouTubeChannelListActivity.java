package com.example.courseinstructionyoutube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YouTubeChannelListActivity extends AppCompatActivity {

    private RecyclerView youtubeVideoRecyclerView;
    private YouTubeVideoAdapter adapter;
    private List<YouTubeVideosResponse.Item> videoList;
    private YouTubeApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_channel_list);

        youtubeVideoRecyclerView = findViewById(R.id.youtubeVideoRecyclerView);
        youtubeVideoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoList = new ArrayList<>();

        // Initialize the YouTube API service
        apiService = RetrofitClient.getYouTubeApiService();

        // Fetch videos from the YouTube Data API
        fetchVideosFromYouTubeApi();
    }

    private void fetchVideosFromYouTubeApi() {
        // Fetch the YouTube API key from strings.xml
        String apiKey = getString(R.string.youtube_api_key);

        // Call the YouTube API to fetch videos from the specified channel
        Call<YouTubeVideosResponse> call = apiService.getVideosList(
                "snippet",
                "UC7_YxT-KID8kRbqZo7MyscQ",
                10,  // Number of videos to fetch
                "date",  // Order by date
                apiKey  // Pass the YouTube API key
        );

        call.enqueue(new Callback<YouTubeVideosResponse>() {
            @Override
            public void onResponse(Call<YouTubeVideosResponse> call, Response<YouTubeVideosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Populate the videoList with data from the API response
                    videoList.addAll(response.body().items);
                    setupRecyclerView();
                    Log.d("YouTube API", "Videos fetched successfully");
                } else {
                    Log.e("YouTube API", "Failed to retrieve videos: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<YouTubeVideosResponse> call, Throwable t) {
                Log.e("YouTube API", "Error: " + t.getMessage());
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new YouTubeVideoAdapter(this, videoList, this::openYouTubePlayer);
        youtubeVideoRecyclerView.setAdapter(adapter);
    }

    private void openYouTubePlayer(String videoId) {
        // Open YouTubePlayerActivity with the selected video
        Intent intent = new Intent(YouTubeChannelListActivity.this, YouTubePlayerActivity.class);
        intent.putExtra("VIDEO_ID", videoId);
        startActivity(intent);
    }
}
