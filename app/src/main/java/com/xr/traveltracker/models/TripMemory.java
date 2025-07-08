package com.xr.traveltracker.models;

public class TripMemory {
    private String title;
    private String date;
    private int durationDays;
    private int imageRes;
    private String description;

    public TripMemory(String title, String date, int durationDays, int imageRes, String description) {
        this.title = title;
        this.date = date;
        this.durationDays = durationDays;
        this.imageRes = imageRes;
        this.description = description;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public int getDurationDays() { return durationDays; }
    public int getImageRes() { return imageRes; }
    public String getDescription() { return description; }
}