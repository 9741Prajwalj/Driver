package com.mlt.driver.models;

public class Ride {
    private String date;
    private String time;
    private String amount;

    public Ride(String date, String time, String amount) {
        this.date = date;
        this.time = time;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getAmount() {
        return amount;
    }
}
