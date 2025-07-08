package com.xr.traveltracker.models;

public class TravelResponse {
    private String message; // 消息或状态信息
    private int travelId; // 旅行的唯一标识符

    // 构造函数
    public TravelResponse(String message, int travelId) {
        this.message = message;
        this.travelId = travelId;
    }

    // Getter 和 Setter 方法
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTravelId() {
        return travelId;
    }

    public void setTravelId(int travelId) {
        this.travelId = travelId;
    }
}