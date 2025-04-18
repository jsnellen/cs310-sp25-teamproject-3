package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalTime;
import java.time.Duration;

/**
 * Represents the daily schedule rules including shift times, lunch periods, and rounding rules.
 */
public class DailySchedule {
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
     * The amount of time needed to work before deducting lunch from time worked.
     */
    private final int lunchThreshold;
    
    /**
     * The interval (in minutes) for rounding punch times.
     */
    private final int roundInterval;

    /**
     * The grace period (in minutes) allowed for clocking in/out before being considered late.
     */
    private final int gracePeriod;

    /**
     * The dock penalty (in minutes) for clocking in/out beyond the grace period.
     */
    private final int dockPenalty;
    
    /**
     * Duration of lunch in minutes.
     */
    private final int lunchDuration;
    
    /**
     * Duration of shift in minutes (excluding lunch).
     */
    private final int shiftDuration;
    
    /**
     * Constructs a DailySchedule with all required parameters.
     *
     * @param start the shift start time
     * @param stop the shift stop time
     * @param lunchStart the lunch start time
     * @param lunchStop the lunch stop time
     * @param lunchThreshold the lunch threshold in minutes
     * @param roundInterval the rounding interval in minutes
     * @param gracePeriod the grace period in minutes
     * @param dockPenalty the dock penalty in minutes
     */
    public DailySchedule(LocalTime start, LocalTime stop, LocalTime lunchStart, LocalTime lunchStop,
                        int lunchThreshold, int roundInterval, int gracePeriod, int dockPenalty) {
        this.start = start;
        this.stop = stop;
        this.lunchStart = lunchStart;
        this.lunchStop = lunchStop;
        this.lunchThreshold = lunchThreshold;
        this.roundInterval = roundInterval;
        this.gracePeriod = gracePeriod;
        this.dockPenalty = dockPenalty;
        this.lunchDuration = (int)Duration.between(lunchStart, lunchStop).toMinutes();
        this.shiftDuration = (int)Duration.between(start, stop).toMinutes();
    }

    /**
     * Gets the shift start time.
     * @return the start time
     */
    public LocalTime getStart() { return start; }
    
    /**
     * Gets the shift stop time.
     * @return the stop time
     */
    public LocalTime getStop() { return stop; }
    
    /**
     * Gets the lunch start time.
     * @return the lunch start time
     */
    public LocalTime getLunchStart() { return lunchStart; }
    
    /**
     * Gets the lunch stop time.
     * @return the lunch stop time
     */
    public LocalTime getLunchStop() { return lunchStop; }
    
    /**
     * Gets the lunch threshold.
     * @return the lunch threshold in minutes
     */
    public int getLunchThreshold() { return lunchThreshold; }
    
    /**
     * Gets the rounding interval.
     * @return the rounding interval in minutes
     */
    public int getRoundInterval() { return roundInterval; }
    
    /**
     * Gets the grace period.
     * @return the grace period in minutes
     */
    public int getGracePeriod() { return gracePeriod; }
    
    /**
     * Gets the dock penalty.
     * @return the dock penalty in minutes
     */
    public int getDockPenalty() { return dockPenalty; }
    
    /**
     * Gets the lunch duration.
     * @return the lunch duration in minutes
     */
    public int getLunchDuration() { return lunchDuration; }
    
    /**
     * Gets the shift duration (excluding lunch).
     * @return the shift duration in minutes
     */
    public int getShiftDuration() { return shiftDuration; }
}