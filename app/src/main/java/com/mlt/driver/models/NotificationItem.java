package com.mlt.driver.models;

public class NotificationItem {
    private String title;
    private String message;
    private String date;

    // Constructor
    public NotificationItem(String title, String message) {
        this.title = title;
        this.message = message;
        this.date = date;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    // Setters (if needed)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
