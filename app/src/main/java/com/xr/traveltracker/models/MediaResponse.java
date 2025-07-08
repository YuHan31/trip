package com.xr.traveltracker.models;

import java.util.List;

public class MediaResponse {
    private String message; // 消息或状态信息
    private List<MediaResult> results; // 媒体结果列表

    // MediaResponse 的构造函数
    public MediaResponse(String message, List<MediaResult> results) {
        this.message = message;
        this.results = results;
    }

    // MediaResponse 的 Getter 和 Setter 方法
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MediaResult> getResults() {
        return results;
    }

    public void setResults(List<MediaResult> results) {
        this.results = results;
    }

    // MediaResult 内部类
    public static class MediaResult {
        private int mediaId; // 媒体 ID
        private String filename; // 文件名
        private String mediaType; // 媒体类型

        // MediaResult 的构造函数
        public MediaResult(int mediaId, String filename, String mediaType) {
            this.mediaId = mediaId;
            this.filename = filename;
            this.mediaType = mediaType;
        }

        // MediaResult 的 Getter 和 Setter 方法
        public int getMediaId() {
            return mediaId;
        }

        public void setMediaId(int mediaId) {
            this.mediaId = mediaId;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }
    }
}