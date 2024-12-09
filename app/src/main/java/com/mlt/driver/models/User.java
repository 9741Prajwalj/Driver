package com.mlt.driver.models;

/**
 * Created by Prajwal J.
 */

public class User {
    private String Mobile;
    private String userName;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String mobileNo;
    private String address;


    public User(String mobile, String userName, String email, String password) {
        this.Mobile = mobile;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Getters and Setters (if needed)
}

