package com.mlt.driver.models;

public class EarningsResponse {

    private int target;
    private int earnings;

    public EarningsResponse(int target, int earnings) {
        this.target = target;
        this.earnings = earnings;
    }

    // Getter for target
    public int getTarget() {
        return target;
    }

    // Setter for target
    public void setTarget(int target) {
        this.target = target;
    }

    // Getter for earnings
    public int getEarnings() {
        return earnings;
    }

    // Setter for earnings
    public void setEarnings(int earnings) {
        this.earnings = earnings;
    }
}
