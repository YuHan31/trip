// models/AttractionListResponse.java
package com.xr.traveltracker.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AttractionListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<Attraction> data;

    @SerializedName("pagination")
    private Pagination pagination;

    @SerializedName("message")
    private String message;

    public static class Pagination {
        @SerializedName("page")
        private int page;

        @SerializedName("limit")
        private int limit;

        @SerializedName("total")
        private int total;

        @SerializedName("pages")
        private int pages;

        // Getter 和 Setter
        public int getPage() { return page; }
        public int getLimit() { return limit; }
        public int getTotal() { return total; }
        public int getPages() { return pages; }
    }

    public boolean isSuccess() { return success; }
    public List<Attraction> getData() { return data; }
    public Pagination getPagination() { return pagination; }
    public String getMessage() { return message; }
}

