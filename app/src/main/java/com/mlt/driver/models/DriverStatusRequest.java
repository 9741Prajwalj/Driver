package com.mlt.driver.models;

public class DriverStatusRequest {
    private int userId;
    private int status;

    public int getUserId() {
        return userId;
    }

    public int getStatus() {
        return status;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DriverStatusRequest(int userId, int status) {
        this.userId = userId;
        this.status = status;
    }
}

