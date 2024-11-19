package com.mlt.driver.models;

public class Booking {
    private int bookingId; // Change to int
    private String bookingDate;
    private String sourceAddress;
    private String destAddress;
    private String journeyDate;
    private String journeyTime;
    private String rideStatus;
    private String totalKms;
    private String amount;

    public Booking(int bookingId, String bookingDate, String sourceAddress, String destAddress, String journeyDate, String journeyTime, String rideStatus, String totalKms, String amount) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.sourceAddress = sourceAddress;
        this.destAddress = destAddress;
        this.journeyDate = journeyDate;
        this.journeyTime = journeyTime;
        this.rideStatus = rideStatus;
        this.totalKms = totalKms;
        this.amount = amount;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
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

    public String getTotalKms() {
        return totalKms;
    }

    public void setTotalKms(String totalKms) {
        this.totalKms = totalKms;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
