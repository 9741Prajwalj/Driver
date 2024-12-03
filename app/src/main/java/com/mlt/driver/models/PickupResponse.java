package com.mlt.driver.models;

public class PickupResponse {
    private String status;
    private Data data;

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String pickupAddress;

        public String getPickupAddress() {
            return pickupAddress;
        }
    }
}


