package com.example.smsrelay.models;

public class Relay {
    private int number;
    private String name;
    private boolean status;

    public Relay(int number, String name, boolean status) {
        this.number = number; this.name = name; this.status = status;
    }

    public int getNumber() { return number; }
    public String getName() { return name; }
    public boolean getStatus() { return status; }
    public void setName(String name) { this.name = name; }
    public void setStatus(boolean status) { this.status = status; }
}