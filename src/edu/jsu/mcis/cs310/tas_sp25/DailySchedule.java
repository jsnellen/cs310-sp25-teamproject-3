package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalTime;


public class DailySchedule {
    // class variables 
    /**
     * The start time of the shift.
     */
    private final LocalTime shiftStart;
    
    /**
     * The stop time of the shift.
     */
    private final LocalTime shiftStop;
    
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
    
    
    public DailySchedule(LocalTime start, LocalTime stop, LocalTime lStart, LocalTime lStop, int lThresh, int interval, int grace, int dock){
        shiftStart = start;
        shiftStop = stop;
        lunchStart = lStart;
        lunchStop = lStop;
        lunchThreshold = lThresh;
        roundInterval = interval;
        gracePeriod = grace;
        dockPenalty = dock;
    }
    
    
    
}
