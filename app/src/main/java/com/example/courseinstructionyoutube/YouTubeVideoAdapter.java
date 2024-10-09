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

    private final List<YouTubeVideosResponse.Item> videoList;
    private final OnVideoClickListener onVideoClickListener;
    private final Context context;

    // Interface for video click events
    public interface OnVideoClickListener {
        void onVideoClick(String videoId);
    }

    public YouTubeVideoAdapter(Context context, List<YouTubeVideosResponse.Item> videoList, OnVideoClickListener onVideoClickListener) {
        this.context = context;
        this.videoList = videoList;
        this.onVideoClickListener = onVideoClickListener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        YouTubeVideosResponse.Item video = videoList.get(position);
        holder.videoTitle.setText(video.snippet.title);

        // Check for null values before accessing fields and attempt to load different thumbnail sizes
        if (video.snippet != null && video.snippet.thumbnails != null) {
            if (video.snippet.thumbnails.high != null) {
                Picasso.with(context).load(video.snippet.thumbnails.high.url).into(holder.videoThumbnail);
            } else if (video.snippet.thumbnails.medium != null) {
                Picasso.with(context).load(video.snippet.thumbnails.medium.url).into(holder.videoThumbnail);
            } else if (video.snippet.thumbnails._default != null) {  // Use _default here
                Picasso.with(context).load(video.snippet.thumbnails._default.url).into(holder.videoThumbnail);
            } else {
                holder.videoThumbnail.setImageResource(R.drawable.oplogoc);  // Default image
            }
        } else {
            Log.d("YouTubeVideoAdapter", "No thumbnails available for video: " + video.snippet.title);
            holder.videoThumbnail.setImageResource(R.drawable.oplogoc);  // Default image
        }

        holder.itemView.setOnClickListener(v -> onVideoClickListener.onVideoClick(video.id.videoId));
    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        public TextView videoTitle;
        public ImageView videoThumbnail;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
        }
    }
}
