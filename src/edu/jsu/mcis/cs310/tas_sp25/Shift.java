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
     * The start time of the shift.
     */
    private final LocalTime start;

    /**
     * The stop time of the shift.
     */
    private final LocalTime stop;

    /**
     * The start time of the lunch break.
     */
    private final LocalTime lunchStart;

    /**
     * The stop time of the lunch break.
     */
    private final LocalTime lunchStop;

    /**
     * The duration of lunch in minutes.
     */
    private final int lunchDuration;
    
    private final int lunchThreshold = 360;

    /**
     * The total shift duration in minutes.
     */
    private final int shiftDuration;

    /**
     * The interval (in minutes) for rounding punch times.
     */
    private final int roundInterval = 15;

    /**
     * The grace period (in minutes) allowed for clocking in/out before being considered late.
     */
    private final int gracePeriod = 5;

    /**
     * The dock penalty (in minutes) for clocking in/out beyond the grace period.
     */
    private final int dockPenalty = 15;

    /**
     * Constructs a Shift object from a HashMap containing shift data.
     *
     * @param shiftData a HashMap of shift field names and values
     */
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
     * Gets the shift start time.
     *
     * @return the start time
     */
    public LocalTime getStart() {
        return start;
    }

    /**
     * Gets the shift stop time.
     *
     * @return the stop time
     */
    public LocalTime getStop() {
        return stop;
    }

    /**
     * Gets the lunch start time.
     *
     * @return the lunch start time
     */
    public LocalTime getLunchStart() {
        return lunchStart;
    }

    /**
     * Gets the lunch stop time.
     *
     * @return the lunch stop time
     */
    public LocalTime getLunchStop() {
        return lunchStop;
    }

    /**
     * Gets the lunch duration in minutes.
     *
     * @return the lunch duration
     */
    public int getLunchDuration() {
        return lunchDuration;
    }

    public int getLunchThreshold() {
        return lunchThreshold;
    }
    /**
     * Gets the total shift duration in minutes.
     *
     * @return the shift duration
     */
    public int getShiftDuration() {
        return shiftDuration;
    }

    /**
     * Gets the rounding interval in minutes.
     *
     * @return the rounding interval
     */
    public int getRoundInterval() {
        return roundInterval;
    }

    /**
     * Gets the grace period in minutes.
     *
     * @return the grace period
     */
    public int getGracePeriod() {
        return gracePeriod;
    }

    /**
     * Gets the dock penalty in minutes.
     *
     * @return the dock penalty
     */
    public int getDockPenalty() {
        return dockPenalty;
    }

    /**
     * Returns a formatted string describing the shift.
     *
     * @return a formatted string of shift and lunch times/durations
     */
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
