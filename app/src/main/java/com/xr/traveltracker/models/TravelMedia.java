package com.xr.traveltracker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelMedia implements Parcelable {
    private int mediaId;
    private int travelId;
    private String mediaType;
    private String mediaUrl;
    private String thumbnailUrl;
    private String caption;
    private int displayOrder;

    // 添加默认构造函数
    public TravelMedia() {
    }

    // 添加常用构造函数
    public TravelMedia(int mediaId, int travelId, String mediaType, String mediaUrl) {
        this.mediaId = mediaId;
        this.travelId = travelId;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
    }

    // 添加全参构造函数
    public TravelMedia(int mediaId, int travelId, String mediaType, String mediaUrl,
                       String thumbnailUrl, String caption, int displayOrder) {
        this.mediaId = mediaId;
        this.travelId = travelId;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.caption = caption;
        this.displayOrder = displayOrder;
    }

    // 保留原有的 Parcelable 构造函数
    protected TravelMedia(Parcel in) {
        mediaId = in.readInt();
        travelId = in.readInt();
        mediaType = in.readString();
        mediaUrl = in.readString();
        thumbnailUrl = in.readString();
        caption = in.readString();
        displayOrder = in.readInt();
    }

    public static final Creator<TravelMedia> CREATOR = new Creator<TravelMedia>() {
        @Override
        public TravelMedia createFromParcel(Parcel in) {
            return new TravelMedia(in);
        }

        @Override
        public TravelMedia[] newArray(int size) {
            return new TravelMedia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mediaId);
        dest.writeInt(travelId);
        dest.writeString(mediaType);
        dest.writeString(mediaUrl);
        dest.writeString(thumbnailUrl);
        dest.writeString(caption);
        dest.writeInt(displayOrder);
    }

    // Getter和Setter方法
    public int getMediaId() { return mediaId; }
    public void setMediaId(int mediaId) { this.mediaId = mediaId; }

    public int getTravelId() { return travelId; }
    public void setTravelId(int travelId) { this.travelId = travelId; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}