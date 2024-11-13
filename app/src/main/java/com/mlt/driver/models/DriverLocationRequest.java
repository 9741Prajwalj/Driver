package com.mlt.driver.models;

public class DriverLocationRequest {
    private int userId;
    private double latitude;
    private double longitude;

    public DriverLocationRequest(int userId, double latitude, double longitude) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

