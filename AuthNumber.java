package com.example.smsrelay.models;

public class AuthNumber {
    private String number; private boolean active;

    public AuthNumber(String number, boolean active) {
        this.number = number; this.active = active;
    }

    public String getNumber() { return number; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}