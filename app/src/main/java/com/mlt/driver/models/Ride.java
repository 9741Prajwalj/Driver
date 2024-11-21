package com.mlt.driver.models;

public class Ride {
    private int bookingId;
    private String bookDate;
    private String bookTime;
    private String sourceAddress;
    private String destinationAddress;
    private String journeyDate;
    private String journeyTime;
    private String rideStatus;

    // Constructor
    public Ride(int bookingId, String sourceAddress, String destinationAddress, String journeyDate, String journeyTime, String rideStatus) {
        this.bookingId = bookingId;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.journeyDate = journeyDate;
        this.journeyTime = journeyTime;
        this.rideStatus = rideStatus;
    }

    // Getters
    public int getBookingId() {
        return bookingId;
    }
    public String getBookDate() { return bookDate; }
    public void setBookDate(String bookDate) { this.bookDate = bookDate; }

    public String getBookTime() { return bookTime; }
    public void setBookTime(String bookTime) { this.bookTime = bookTime; }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void setJourneyDate(String journeyDate) {
        this.journeyDate = journeyDate;
    }

    public void setJourneyTime(String journeyTime) {
        this.journeyTime = journeyTime;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public String getJourneyTime() {
        return journeyTime;
    }

    public String getRideStatus() {
        return rideStatus;
    }
}

