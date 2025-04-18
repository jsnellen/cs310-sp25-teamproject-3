package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Objects;

/**
 * <p>Represents a work shift, including start/stop times, lunch periods, and policy rules such as grace and dock penalties.</p>
 * <p>Shift details are loaded from a HashMap, typically coming from a database.</p>
 * 
 * @author Group
 */
public class Shift {

    /**
     * Unique identifier for the shift.
     */
    private final int id;

    /**
     * Description or name of the shift.
     */
    private final String description;
    
    /**
     * Default daily schedule for this shift.
     */
    private final DailySchedule defaultschedule;

    /**
     * Constructs a Shift object from a HashMap containing shift data.
     *
     * @param shiftData a HashMap of shift field names and values
     */
    public Shift(HashMap<String, String> shiftData) {
        this.id = Integer.parseInt(Objects.requireNonNull(shiftData.get("id"), "ID is missing"));
        this.description = Objects.requireNonNull(shiftData.get("description"), "Description is missing");
        
        this.defaultschedule = new DailySchedule(
            LocalTime.parse(Objects.requireNonNull(shiftData.get("start_time"), "00:00")),
            LocalTime.parse(Objects.requireNonNull(shiftData.get("stop_time"), "00:00")),
            LocalTime.parse(Objects.requireNonNull(shiftData.get("lunch_start"), "00:00")),
            LocalTime.parse(Objects.requireNonNull(shiftData.get("lunch_stop"), "00:00")),
            Integer.parseInt(Objects.requireNonNull(shiftData.get("lunchthreshold"), "0")),
            Integer.parseInt(Objects.requireNonNull(shiftData.get("roundinterval"), "0")),
            Integer.parseInt(Objects.requireNonNull(shiftData.get("graceperiod"), "0")),
            Integer.parseInt(Objects.requireNonNull(shiftData.get("dockpenalty"), "0"))
        );
    }

    /**
     * Gets the shift ID.
     *
     * @return the shift ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the shift description.
     *
     * @return the shift description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the default daily schedule for this shift.
     *
     * @return the default daily schedule
     */
    public DailySchedule getDefaultschedule() {
        return defaultschedule;
    }

    /**
     * Gets the shift start time.
     *
     * @return the start time
     */
    public LocalTime getStart() {
        return defaultschedule.getStart();
    }

    /**
     * Gets the shift stop time.
     *
     * @return the stop time
     */
    public LocalTime getStop() {
        return defaultschedule.getStop();
    }

    /**
     * Gets the lunch start time.
     *
     * @return the lunch start time
     */
    public LocalTime getLunchStart() {
        return defaultschedule.getLunchStart();
    }

    /**
     * Gets the lunch stop time.
     *
     * @return the lunch stop time
     */
    public LocalTime getLunchStop() {
        return defaultschedule.getLunchStop();
    }

    /**
     * Gets the lunch duration in minutes.
     *
     * @return the lunch duration
     */
    public int getLunchDuration() {
        return defaultschedule.getLunchDuration();
    }

    /**
     * Gets the lunch threshold.
     *
     * @return the lunch threshold
     */
    public int getLunchThreshold() {
        return defaultschedule.getLunchThreshold();
    }

    /**
     * Gets the total shift duration in minutes.
     *
     * @return the shift duration
     */
    public int getShiftDuration() {
        return defaultschedule.getShiftDuration();
    }

    /**
     * Gets the rounding interval in minutes.
     *
     * @return the rounding interval
     */
    public int getRoundInterval() {
        return defaultschedule.getRoundInterval();
    }

    /**
     * Gets the grace period in minutes.
     *
     * @return the grace period
     */
    public int getGracePeriod() {
        return defaultschedule.getGracePeriod();
    }

    /**
     * Gets the dock penalty in minutes.
     *
     * @return the dock penalty
     */
    public int getDockPenalty() {
        return defaultschedule.getDockPenalty();
    }

    /**
     * Returns a formatted string describing the shift.
     *
     * @return a formatted string of shift and lunch times/durations
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(description).append(": ").append(getStart()).append(" - ").append(getStop())
          .append(" (").append(getShiftDuration()).append(" minutes); Lunch: ")
          .append(getLunchStart()).append(" - ").append(getLunchStop())
          .append(" (").append(getLunchDuration()).append(" minutes)");
        return sb.toString();
    }
}