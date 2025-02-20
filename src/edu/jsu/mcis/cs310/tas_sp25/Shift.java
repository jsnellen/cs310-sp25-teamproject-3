package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalTime;
import java.util.HashMap;

public class Shift {

    private final int id; 
    private final String description; 
    private final LocalTime start; 
    private final LocalTime stop; 
    private final LocalTime lunchStart; 
    private final LocalTime lunchStop; 
    private final int lunchDuration; 
    private final int shiftDuration; 

    
     // Constructor to initialize the Shift object using a HashMap.
     
    public Shift(HashMap<String, String> shiftData) {
        this.id = Integer.parseInt(shiftData.get("id"));
        this.description = shiftData.get("description"); 
        this.start = LocalTime.parse(shiftData.get("start_time"));
        this.stop = LocalTime.parse(shiftData.get("stop_time"));
        this.lunchStart = LocalTime.parse(shiftData.get("lunch_start"));
        this.lunchStop = LocalTime.parse(shiftData.get("lunch_stop"));
        this.lunchDuration = Integer.parseInt(shiftData.get("lunchduration"));
        this.shiftDuration = Integer.parseInt(shiftData.get("shiftduration"));
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

    // toString to describe shift
    @Override
    public String toString() {
        return description + ": " + start + " - " + stop + " (" + shiftDuration + " minutes); Lunch: " +
                lunchStart + " - " + lunchStop + " (" + lunchDuration + " minutes)";
    }
}