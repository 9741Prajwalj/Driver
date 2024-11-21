package com.mlt.driver.models;

public class CancelledRide {
    private int bookingId;
    private String bookDate;
    private String bookTime;
    private String sourceAddress;
    private String destAddress;
    private String journeyDate;
    private String journeyTime;
    private String rideStatus;

    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookDate() { return bookDate; }
    public void setBookDate(String bookDate) { this.bookDate = bookDate; }

    public String getBookTime() { return bookTime; }
    public void setBookTime(String bookTime) { this.bookTime = bookTime; }

    public String getSourceAddress() { return sourceAddress; }
    public void setSourceAddress(String sourceAddress) { this.sourceAddress = sourceAddress; }

    public String getDestAddress() { return destAddress; }
    public void setDestAddress(String destAddress) { this.destAddress = destAddress; }

    public String getJourneyDate() { return journeyDate; }
    public void setJourneyDate(String journeyDate) { this.journeyDate = journeyDate; }

    public String getJourneyTime() { return journeyTime; }
    public void setJourneyTime(String journeyTime) { this.journeyTime = journeyTime; }

    public String getRideStatus() { return rideStatus; }
    public void setRideStatus(String rideStatus) { this.rideStatus = rideStatus; }
}
