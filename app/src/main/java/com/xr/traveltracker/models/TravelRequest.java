package com.xr.traveltracker.models;

public class TravelRequest {
    private String destination;     // 目的地
    private String start_date;      // 开始日期
    private String end_date;        // 结束日期
    private String description;     // 描述
    private double budget;          // 预算

    // 构造函数
    public TravelRequest(String destination, String start_date, String end_date, String description, double budget) {
        this.destination = destination;
        this.start_date = start_date;
        this.end_date = end_date;
        this.description = description;
        this.budget = budget;
    }

    // Getter 和 Setter 方法
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}