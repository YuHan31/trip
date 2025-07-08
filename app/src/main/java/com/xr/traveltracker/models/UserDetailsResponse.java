package com.xr.traveltracker.models;

public class UserDetailsResponse {
    private String userId;
    private String username;
    private String email;

    // 构造函数
    public UserDetailsResponse() {
    }

    public UserDetailsResponse(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    // Getter和Setter方法
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}