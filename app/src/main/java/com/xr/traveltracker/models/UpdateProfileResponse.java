package com.xr.traveltracker.models;

public class UpdateProfileResponse {
    private boolean success;
    private String message;

    // 构造函数
    public UpdateProfileResponse() {
    }

    public UpdateProfileResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getter和Setter方法
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}