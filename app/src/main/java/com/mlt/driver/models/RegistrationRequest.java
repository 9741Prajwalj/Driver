package com.mlt.driver.models;

public class RegistrationRequest {
    private String phone;
    private String user_name;
    private String emailid;
    private String password;

    public RegistrationRequest(String phone, String user_name, String emailid, String password) {
        this.phone = phone;
        this.user_name = user_name;
        this.emailid = emailid;
        this.password = password;
    }
    // Getters and Setters (if needed)

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
