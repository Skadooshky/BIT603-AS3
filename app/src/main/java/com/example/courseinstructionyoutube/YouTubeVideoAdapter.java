package com.example.courseinstructionyoutube;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class YouTubeVideoAdapter extends RecyclerView.Adapter<YouTubeVideoAdapter.VideoViewHolder> {

    // List to store YouTube video data
    private final List<YouTubeVideosResponse.Item> videoList;
    private final OnVideoClickListener onVideoClickListener;  // Listener for video click events

    // Interface to handle video click events
    public interface OnVideoClickListener {
        void onVideoClick(String videoId);
    }

    // Constructor to initialize the adapter with the context, video list, and click listener
    public YouTubeVideoAdapter(Context context, List<YouTubeVideosResponse.Item> videoList, OnVideoClickListener onVideoClickListener) {
        this.videoList = videoList;
        this.onVideoClickListener = onVideoClickListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom layout for each video item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(view);
    }

    // Bind the data (title and thumbnail) to the view holder
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        YouTubeVideosResponse.Item video = videoList.get(position);  // Get the video item at the current position
        holder.videoTitle.setText(video.snippet.title);  // Set the video title

        // Load video thumbnail using Picasso, checking for available thumbnail sizes
        if (video.snippet != null && video.snippet.thumbnails != null) {
            if (video.snippet.thumbnails.high != null) {
                Picasso.get().load(video.snippet.thumbnails.high.url).into(holder.videoThumbnail);
            } else if (video.snippet.thumbnails.medium != null) {
                Picasso.get().load(video.snippet.thumbnails.medium.url).into(holder.videoThumbnail);
            } else if (video.snippet.thumbnails._default != null) {  // Use _default thumbnail if others are unavailable
                Picasso.get().load(video.snippet.thumbnails._default.url).into(holder.videoThumbnail);
            } else {
                // Set a default image if no thumbnail is available
                holder.videoThumbnail.setImageResource(R.drawable.oplogoc);
            }
        } else {
            // Log a message if no thumbnail is found
            assert video.snippet != null;
            Log.d("YouTubeVideoAdapter", "No thumbnails available for video: " + video.snippet.title);
            holder.videoThumbnail.setImageResource(R.drawable.oplogoc);  // Default image
        }

        // Set the click listener for the video item to open the video player
        holder.itemView.setOnClickListener(v -> onVideoClickListener.onVideoClick(video.id.videoId));
    }

    // Return the size of the video list
    @Override
    public int getItemCount() {
        return videoList.size();
    }

    // Provide a reference to the views for each data item
    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        final TextView videoTitle;  // TextView to display the video title
        final ImageView videoThumbnail;  // ImageView to display the video thumbnail

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
        }
    }
}
