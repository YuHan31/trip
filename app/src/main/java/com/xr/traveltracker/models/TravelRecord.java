package com.xr.traveltracker.models;

public class TravelRecord {
    private int travelId;
    private String destination;
    private String startDate;
    private String endDate;
    private String description;
    private double budget;

    // getter和setter方法
    public int getTravelId() {
        return travelId;
    }

    public String getDestination() {
        return destination;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public double getBudget() {
        return budget;
    }
}
