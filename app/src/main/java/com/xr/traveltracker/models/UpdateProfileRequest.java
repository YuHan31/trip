package com.xr.traveltracker.models;

public class UpdateProfileRequest {
    private String userId;
    private String email;
    private String password;

    // 构造函数
    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    // Getter和Setter方法
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}