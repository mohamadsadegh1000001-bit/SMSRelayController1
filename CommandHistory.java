package com.example.smsrelay.models;

public class CommandHistory {
    private String time, command, response, status;

    public CommandHistory(String time, String command, String response, String status) {
        this.time = time; this.command = command; this.response = response; this.status = status;
    }

    public String getTime() { return time; }
    public String getCommand() { return command; }
    public String getResponse() { return response; }
    public String getStatus() { return status; }
}