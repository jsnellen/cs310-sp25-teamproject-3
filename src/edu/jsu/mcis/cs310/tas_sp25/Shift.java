package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Objects;

public class Shift {

    private final int id; 
    private final String description; 
    private final LocalTime start; 
    private final LocalTime stop; 
    private final LocalTime lunchStart; 
    private final LocalTime lunchStop; 
    private final int lunchDuration; 
    private final int shiftDuration;
    private final int roundInterval = 15;
    private final int gracePeriod = 5;
    private final int dockPenalty = 15;

    // Constructor to initialize the Shift object using a HashMap.
    public Shift(HashMap<String, String> shiftData) {
        this.id = Integer.parseInt(Objects.requireNonNull(shiftData.get("id"), "ID is missing"));
        this.description = Objects.requireNonNull(shiftData.get("description"), "Description is missing");
        this.start = LocalTime.parse(Objects.requireNonNull(shiftData.get("start_time"), "00:00"));
        this.stop = LocalTime.parse(Objects.requireNonNull(shiftData.get("stop_time"), "00:00"));
        this.lunchStart = LocalTime.parse(Objects.requireNonNull(shiftData.get("lunch_start"), "00:00"));
        this.lunchStop = LocalTime.parse(Objects.requireNonNull(shiftData.get("lunch_stop"), "00:00"));
        this.lunchDuration = Integer.parseInt(Objects.requireNonNull(shiftData.get("lunchduration"), "0"));
        this.shiftDuration = Integer.parseInt(Objects.requireNonNull(shiftData.get("shiftduration"), "0"));
    }

    // Getters for all fields
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getStop() {
        return stop;
    }

    public LocalTime getLunchStart() {
        return lunchStart;
    }

    public LocalTime getLunchStop() {
        return lunchStop;
    }

    public int getLunchDuration() {
        return lunchDuration;
    }

    public int getShiftDuration() {
        return shiftDuration;
    }
    
    public int getRoundInterval() {
        return roundInterval;
    }
    
    public int getGracePeriod() {
        return gracePeriod;
    }
    
    public int getDockPenalty() {
        return dockPenalty;
    }

    // toString to describe shift
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(description).append(": ").append(start).append(" - ").append(stop)
          .append(" (").append(shiftDuration).append(" minutes); Lunch: ")
          .append(lunchStart).append(" - ").append(lunchStop)
          .append(" (").append(lunchDuration).append(" minutes)");
        return sb.toString();
    }
}
