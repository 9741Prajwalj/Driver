package com.mlt.driver.models;

public class PickupRequest {
    private String api_token;
    private int booking_id;

    public PickupRequest(String api_token, int booking_id) {
        this.api_token = api_token;
        this.booking_id = booking_id;
    }
}

