package com.xr.traveltracker.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class TravelRecord implements Parcelable {
    private int travelId;
    private int userId;
    private String destination;
    private Date startDate;
    private Date endDate;
    private String description;
    private String budget;

    // 构造函数
    public TravelRecord(int travelId, int userId, String destination,
                        Date startDate, Date endDate, String description,
                        String budget) {
        this.travelId = travelId;
        this.userId = userId;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.budget = budget;
    }

    // Parcelable 构造函数
    protected TravelRecord(Parcel in) {
        travelId = in.readInt();
        userId = in.readInt();
        destination = in.readString();
        long startDateVal = in.readLong();
        startDate = startDateVal == -1 ? null : new Date(startDateVal);
        long endDateVal = in.readLong();
        endDate = endDateVal == -1 ? null : new Date(endDateVal);
        description = in.readString();
        budget = in.readString();
    }

    public static final Creator<TravelRecord> CREATOR = new Creator<TravelRecord>() {
        @Override
        public TravelRecord createFromParcel(Parcel in) {
            return new TravelRecord(in);
        }

        @Override
        public TravelRecord[] newArray(int size) {
            return new TravelRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(travelId);
        dest.writeInt(userId);
        dest.writeString(destination);
        dest.writeLong(startDate != null ? startDate.getTime() : -1);
        dest.writeLong(endDate != null ? endDate.getTime() : -1);
        dest.writeString(description);
        dest.writeString(budget);
    }

    // Getter和Setter方法
    public int getTravelId() { return travelId; }
    public void setTravelId(int travelId) { this.travelId = travelId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }
}