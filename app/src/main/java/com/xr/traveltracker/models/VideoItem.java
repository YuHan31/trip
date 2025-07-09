package com.xr.traveltracker.models;

public class VideoItem {
    private String title;
    private String duration;
    private String thumbnailUrl;
    private String videoUrl;

    public VideoItem(String title, String duration, String thumbnailUrl, String videoUrl) {
        this.title = title;
        this.duration = duration;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDuration() { return duration; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getVideoUrl() { return videoUrl; }
}