package com.mlt.driver.models;

public class UpcomingRide {

    private int bookingId;
    private String bookDate;
    private String bookTime;
    private String sourceAddress;
    private String destAddress;
    private String journeyDate;
    private String journeyTime;
    private String rideStatus;
    private String pickupLat;
    private String pickupLong;

    // Constructor for upcoming ride
    public UpcomingRide(int bookingId, String bookDate, String bookTime, String sourceAddress,
                        String destAddress, String journeyDate, String journeyTime, String rideStatus) {
        this.bookingId = bookingId;
        this.bookDate = bookDate;
        this.bookTime = bookTime;
        this.sourceAddress = sourceAddress;
        this.destAddress = destAddress;
        this.journeyDate = journeyDate;
        this.journeyTime = journeyTime;
        this.rideStatus = rideStatus;
    }

    // Getter methods (optional)
    public int getBookingId() {
        return bookingId;
    }

    public String getBookDate() {
        return bookDate;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public String getBookTime() {
        return bookTime;
    }

    public void setBookTime(String bookTime) {
        this.bookTime = bookTime;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(String journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getJourneyTime() {
        return journeyTime;
    }

    public void setJourneyTime(String journeyTime) {
        this.journeyTime = journeyTime;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public String getPickupLat() {
        return pickupLat;
    }

    public String getPickupLong() {
        return pickupLong;
    }
}

