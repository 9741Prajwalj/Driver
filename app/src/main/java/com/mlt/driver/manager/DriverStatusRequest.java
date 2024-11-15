package com.mlt.driver.manager;

public class DriverStatusRequest {
    private int driverId;
    private int status;

    public DriverStatusRequest(int driverId, int status) {
        this.driverId = driverId;
        this.status = status;
    }

    public int getDriverId() {
        return driverId;
    }

    public int getStatus() {
        return status;
    }
}
