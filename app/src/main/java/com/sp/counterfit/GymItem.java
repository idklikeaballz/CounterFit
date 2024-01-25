package com.sp.counterfit;

public class GymItem {
    private String name;
    private String address;
    private int imageResourceId; // Drawable resource ID for the image
    private double latitude;
    private double longitude;

    // Constructor, getters, and setters
    public GymItem(String name, String address, int imageResourceId, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.imageResourceId = imageResourceId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
