package com.xr.traveltracker.models;

import com.google.gson.annotations.SerializedName;

public class Attraction {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("city")
    private String city;

    @SerializedName("description")
    private String description;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("ticket_price")
    private double ticketPrice;

    @SerializedName("opening_hours")
    private String openingHours;

    @SerializedName("address")
    private String address;

    // 构造函数
    public Attraction() {}

    public Attraction(int id, String name, String type, String city, String description,
                      String imageUrl, double ticketPrice, String openingHours, String address) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.city = city;
        this.description = description;
        this.imageUrl = imageUrl;
        this.ticketPrice = ticketPrice;
        this.openingHours = openingHours;
        this.address = address;
    }

    // Getter 和 Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}