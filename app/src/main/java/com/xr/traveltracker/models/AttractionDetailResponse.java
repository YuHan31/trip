// models/AttractionDetailResponse.java
package com.xr.traveltracker.models;

import com.google.gson.annotations.SerializedName;

public class AttractionDetailResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Attraction data;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() { return success; }
    public Attraction getData() { return data; }
    public String getMessage() { return message; }
}
