package com.xr.traveltracker.models;

public class Trip {
    private String date;
    private String title;
    private String location;
    private int imageRes;

    public Trip(String date, String title, String location, int imageRes) {
        this.date = date;
        this.title = title;
        this.location = location;
        this.imageRes = imageRes;
    }

    // Getters
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public int getImageRes() { return imageRes; }
}
