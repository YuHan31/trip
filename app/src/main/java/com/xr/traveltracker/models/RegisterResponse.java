package com.xr.traveltracker.models;

public class RegisterResponse {
    private String message;
    private String token;

    // 构造函数
    public RegisterResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    // Getter 和 Setter 方法
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}