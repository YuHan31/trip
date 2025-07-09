package com.xr.traveltracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xr.traveltracker.R;
import com.xr.traveltracker.models.VideoItem;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context context;
    private List<VideoItem> videoItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(VideoItem item);
    }

    public VideoAdapter(Context context, List<VideoItem> videoItems, OnItemClickListener listener) {
        this.context = context;
        this.videoItems = videoItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem item = videoItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView duration;
        ImageView playButton;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.videoThumbnail);
            title = itemView.findViewById(R.id.videoTitle);
            duration = itemView.findViewById(R.id.videoDuration);
            playButton = itemView.findViewById(R.id.playButton);
        }

        void bind(final VideoItem item, final OnItemClickListener listener) {
            Glide.with(itemView.getContext())
                    .load(item.getThumbnailUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(thumbnail);

            title.setText(item.getTitle());
            duration.setText(item.getDuration());

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}