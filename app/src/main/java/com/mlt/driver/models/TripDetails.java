package com.mlt.driver.models;

import com.google.gson.annotations.SerializedName;
public class TripDetails {

    @SerializedName("provider_name")
    private String providerName;

    @SerializedName("provider_rating")
    private float providerRating;

    @SerializedName("trip_date")
    private String tripDate;

    @SerializedName("source")
    private String source;

    @SerializedName("destination")
    private String destination;

    @SerializedName("trip_id")
    private String tripId;

    @SerializedName("comments")
    private String comments;


    // Constructor
    public TripDetails(String providerName, float providerRating, String tripDate, String source, String destination, String comments, String tripId) {
        this.providerName = providerName;
        this.providerRating = providerRating;
        this.tripDate = tripDate;
        this.source = source;
        this.destination = destination;
        this.comments = comments;
        this.tripId = tripId;
    }

    // Getter methods
    public String getProviderName() {
        return providerName;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setProviderRating(float providerRating) {
        this.providerRating = providerRating;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getTripId() {
        return tripId;
    }

    public float getProviderRating() {
        return providerRating;
    }

    public String getDate() {
        return tripDate;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getComments() {
        return comments;
    }


}
