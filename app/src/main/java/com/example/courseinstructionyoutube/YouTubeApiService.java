package com.example.courseinstructionyoutube;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Define Retrofit interface for YouTube API calls
public interface YouTubeApiService {

    // Fetch information about a YouTube channel
    @GET("channels")
    Call<YouTubeChannelResponse> getChannelInfo(
            @Query("part") String part,
            @Query("id") String channelId,
            @Query("key") String apiKey
    );

    // Fetch a list of videos for a specific channel
    @GET("search")
    Call<YouTubeVideosResponse> getVideosList(
            @Query("part") String part,
            @Query("channelId") String channelId,
            @Query("maxResults") int maxResults,
            @Query("order") String order,
            @Query("key") String apiKey
    );
}
