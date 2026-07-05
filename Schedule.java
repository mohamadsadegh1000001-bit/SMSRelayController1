package com.example.smsrelay.models;

public class Schedule {
    private String name; private int relay; private String time; private String action; private String repeat; private String dayOfWeek;

    public Schedule(String name, int relay, String time, String action, String repeat, String dayOfWeek) {
        this.name = name; this.relay = relay; this.time = time; this.action = action; this.repeat = repeat; this.dayOfWeek = dayOfWeek;
    }

    public String getName() { return name; }
    public int getRelay() { return relay; }
    public String getTime() { return time; }
    public String getAction() { return action; }
    public String getRepeat() { return repeat; }
    public String getDayOfWeek() { return dayOfWeek; }
}