package com.mlt.driver.models;

public class DriverData {

    private String username;
    private String userId;
    private String phoneNumber;

    // Constructor
    public DriverData(String username, String userId, String phoneNumber) {
        this.username = username;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Setter methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
