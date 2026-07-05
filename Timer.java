package com.example.smsrelay.models;

public class Timer {
    private int relay; private int minutes; private boolean action; private boolean active;

    public Timer(int relay, int minutes, boolean action, boolean active) {
        this.relay = relay; this.minutes = minutes; this.action = action; this.active = active;
    }

    public int getRelay() { return relay; }
    public int getMinutes() { return minutes; }
    public boolean isAction() { return action; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getActionText() { return action ? "روشن" : "خاموش"; }
}