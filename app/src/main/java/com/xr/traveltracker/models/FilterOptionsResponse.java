// models/FilterOptionsResponse.java
package com.xr.traveltracker.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FilterOptionsResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private FilterData data;

    @SerializedName("message")
    private String message;

    public static class FilterData {
        @SerializedName("types")
        private List<String> types;

        @SerializedName("cities")
        private List<String> cities;

        public List<String> getTypes() { return types; }
        public List<String> getCities() { return cities; }
    }

    public boolean isSuccess() { return success; }
    public FilterData getData() { return data; }
    public String getMessage() { return message; }
}