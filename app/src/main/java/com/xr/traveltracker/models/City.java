package com.xr.traveltracker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {
    private String name;
    private String province;
    private double latitude;
    private double longitude;
    private boolean visited;

    public City(String name, String province, double latitude, double longitude) {
        this.name = name;
        this.province = province;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visited = false;
    }

    protected City(Parcel in) {
        name = in.readString();
        province = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        visited = in.readByte() != 0;
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(province);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeByte((byte) (visited ? 1 : 0));
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}