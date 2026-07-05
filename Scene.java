package com.example.smsrelay.models;

import java.util.List;

public class Scene {
    private String name;
    private List<Integer> relays;
    private List<Boolean> states;

    public Scene(String name, List<Integer> relays, List<Boolean> states) {
        this.name = name; this.relays = relays; this.states = states;
    }

    public String getName() { return name; }
    public int getRelayCount() { return relays.size(); }
    public int getRelayAt(int index) { return relays.get(index); }
    public boolean getStateAt(int index) { return states.get(index); }
    public void setName(String name) { this.name = name; }
}