package com.mlt.driver.models;
/**
 * Created by Prajwal J.
 */
public class CompletedRide {

    private int bookingId;
    private String bookDate;
    private String journeyDate;
    private String sourceAddress;
    private String destAddress;
    private String rideStatus;
    private String totalKms;
    private int amount;
    private String sourceTime;
    private String destTime;

    public CompletedRide(int bookingId, String bookDate, String journeyDate, String sourceAddress,
                         String destAddress, String rideStatus, String sourceTime, String destTime,
                         String totalKms, int amount) {
        this.bookingId = bookingId;
        this.bookDate = bookDate;
        this.journeyDate = journeyDate;
        this.sourceAddress = sourceAddress;
        this.destAddress = destAddress;
        this.rideStatus = rideStatus;
        this.sourceTime = sourceTime;
        this.destTime = destTime;
        this.totalKms = totalKms;
        this.amount = amount;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getBookDate() { return bookDate; }
    public void setBookDate(String bookDate) { this.bookDate = bookDate; }

    public String getJourneyDate() { return journeyDate; }
    public void setJourneyDate(String journeyDate) { this.journeyDate = journeyDate; }

    public String getSourceAddress() { return sourceAddress; }
    public void setSourceAddress(String sourceAddress) { this.sourceAddress = sourceAddress; }

    public String getDestAddress() { return destAddress; }
    public void setDestAddress(String destAddress) { this.destAddress = destAddress; }

    public String getRideStatus() { return rideStatus; }
    public void setRideStatus(String rideStatus) { this.rideStatus = rideStatus; }

    public String getSourceTime() { return sourceTime; }
    public void setSourceTime(String sourceTime) { this.sourceTime = sourceTime; }

    public String getDestTime() { return destTime; }
    public void setDestTime(String destTime) { this.destTime = destTime; }

    public String getTotalKms() { return totalKms; }
    public void setTotalKms(String totalKms) { this.totalKms = totalKms; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    // Methods to match the UI structure
    public String getBookingDateTime() {
        return getBookDate() + " | " + getJourneyDate();
    }

    public String getJourneyTime() {
        return getSourceTime() + " | " + getDestTime();
    }

    public String getStatusText() {
        return "Status: " + getRideStatus();
    }

    public String getTotalKmText() {
        return "Total Distance: " + getTotalKms() + " km";
    }

    public String getAmountText() {
        return "Amount: ₹" + getAmount();
    }

    public String getTotalKm() {
        return totalKms;
    }

    public String getJourneyDateTime() {
        return getJourneyDate() + " | " + getJourneyTime();
    }

    public String getStatus() {
        return rideStatus;
    }
}
