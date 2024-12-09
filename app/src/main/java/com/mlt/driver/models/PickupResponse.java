package com.mlt.driver.models;

/**
 * Created by Prajwal J.
 */

public class PickupResponse {
    private int success;
    private String message;
    private Data data;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private int booking_id;
        private String pickup_address;
        private String pickup_lat;
        private String pickup_long;

        public int getBookingId() {
            return booking_id;
        }

        public String getPickupAddress() {
            return pickup_address;
        }

        public String getPickupLat() {
            return pickup_lat;
        }

        public String getPickupLong() {
            return pickup_long;
        }
    }
}



