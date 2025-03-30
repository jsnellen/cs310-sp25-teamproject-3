package edu.jsu.mcis.cs310.tas_sp25;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;



/**
 * <p>Represents a punch (clock-in or clock-out event) in the TAS system.</p>
 * <p>A Punch contains both the original and adjusted timestamps, and logic to adjust timestamps based on shift rules.</p>
 * 
 * @author Group
 */

public class Punch {
    
    /**
     * Unique identifier for the punch.
     */
    private int id;

    /**
     * Terminal ID where the punch was recorded.
     */
    private int terminalId;

    /**
     * Badge associated with the punch.
     */
    private Badge badge;

    /**
     * Original timestamp recorded when the punch occurred.
     */
    private LocalDateTime originalTimestamp;

    /**
     * Adjusted timestamp after applying shift rounding rules.
     */
    private LocalDateTime adjustedTimestamp;

    /**
     * The type of punch event (e.g., CLOCK IN, CLOCK OUT).
     */
    private EventType eventType;

    /**
     * The type of adjustment applied to the punch.
     */
    private PunchAdjustmentType adjustmentType;

    /**
     * Date/time format pattern.
     */
    private String dateTimeFormat = "EEE MM/dd/yyyy HH:mm:ss";

    /**
     * Formatter for timestamps using the specified pattern.
     */
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

    /**
     * Constructs a Punch for live clock-in/out.
     *
     * @param terminalId the terminal ID where the punch occurred
     * @param badge      the badge used for the punch
     * @param eventType  the event type (CLOCK IN / CLOCK OUT)
     */

    public Punch(int terminalId, Badge badge, EventType eventType) {
        this.terminalId = terminalId;
        this.badge = badge;
        this.eventType = eventType;
        this.originalTimestamp = LocalDateTime.now();
        this.adjustedTimestamp = null;
        this.adjustmentType = PunchAdjustmentType.NONE;
    }

    /**
     * Constructs a Punch from existing database data.
     *
     * @param id               the punch ID
     * @param terminalId       the terminal ID
     * @param badge            the badge used
     * @param originalTimestamp the recorded original timestamp
     * @param eventType        the punch event type
     */

     
    // Constructor for Punch loaded from database
    public Punch(int id, int terminalId, Badge badge, LocalDateTime originalTimestamp, EventType eventType) {
        this.id = id;
        this.terminalId = terminalId;
        this.badge = badge;
        this.originalTimestamp = originalTimestamp;
        this.eventType = eventType;
        this.adjustedTimestamp = null;
        this.adjustmentType = PunchAdjustmentType.NONE;
    }



    // Getter methods
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getTerminalid() { return terminalId; }
    public Badge getBadge() { return badge; }
    public LocalDateTime getOriginaltimestamp() { return originalTimestamp; }
    public LocalDateTime getAdjustedTimestamp() { return adjustedTimestamp; }
    public EventType getPunchtype() { return eventType; }
    public PunchAdjustmentType getAdjustmentType() { return adjustmentType; }
    
    public String getTimestampAsString(LocalDateTime timestamp){
        return timestamp.format(formatter).toUpperCase();
    }

    public void setAdjustedTimestamp(LocalDateTime adjustedTimestamp) {
        this.adjustedTimestamp = adjustedTimestamp;
    }

    public void setAdjustmentType(PunchAdjustmentType adjustmentType) {
        this.adjustmentType = adjustmentType;
    }
    //adjust method
    public void adjust(Shift shift) {
        if (shift == null || originalTimestamp == null) return;

        // Get shift details
        LocalDateTime shiftStart = LocalDateTime.of(originalTimestamp.toLocalDate(), shift.getStart());
        LocalDateTime shiftStop = LocalDateTime.of(originalTimestamp.toLocalDate(), shift.getStop());
        LocalDateTime lunchStart = LocalDateTime.of(originalTimestamp.toLocalDate(), shift.getLunchStart());
        LocalDateTime lunchStop = LocalDateTime.of(originalTimestamp.toLocalDate(), shift.getLunchStop());

        int roundInterval = shift.getRoundInterval();
        int gracePeriod = shift.getGracePeriod();
        int dockPenalty = shift.getDockPenalty();

        // Default adjustment type is None
        adjustmentType = PunchAdjustmentType.NONE;
        adjustedTimestamp = originalTimestamp.truncatedTo(ChronoUnit.MINUTES);
        
        // Check if the day is a weekend
        DayOfWeek dayOfWeek = originalTimestamp.getDayOfWeek();
        boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
        
        if (isWeekend){
            // Logic for weekend rounding
            // *** LINE ADDED BY JORDAN *** \\
            calculateRounding(roundInterval);
        }
        else{
            // Adjustment rules
            if (eventType == EventType.CLOCK_IN) {
                System.out.println("originalTimeStamp, shiftStart: " + originalTimestamp.toString() + shiftStart);
                if (originalTimestamp.equals(shiftStart)) {  // Exact match
                    adjustedTimestamp = shiftStart;
                    adjustmentType = PunchAdjustmentType.SHIFT_START;
                } else if (originalTimestamp.isBefore(shiftStart) && originalTimestamp.isAfter(shiftStart.minusMinutes(roundInterval))) {
                    adjustedTimestamp = shiftStart;
                    adjustmentType = PunchAdjustmentType.SHIFT_START;
                } else if (originalTimestamp.isAfter(shiftStart) && originalTimestamp.isBefore(shiftStart.plusMinutes(gracePeriod))) {
                    adjustedTimestamp = shiftStart;
                    adjustmentType = PunchAdjustmentType.SHIFT_START;
                } else if (originalTimestamp.isAfter(shiftStart.plusMinutes(gracePeriod)) && originalTimestamp.isBefore(shiftStart.plusMinutes(dockPenalty))) {
                    adjustedTimestamp = shiftStart.plusMinutes(dockPenalty);
                    adjustmentType = PunchAdjustmentType.SHIFT_DOCK;
                } else if (originalTimestamp.isAfter(lunchStart) && originalTimestamp.isBefore(lunchStop)) {
                    adjustedTimestamp = lunchStop;
                    adjustmentType = PunchAdjustmentType.LUNCH_STOP;
                }
                else if (originalTimestamp.isAfter(lunchStop)&&originalTimestamp.isBefore(shiftStop.minusMinutes(dockPenalty))){
                    adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;
                    // *** LINE ADDED BY JORDAN *** \\
                    calculateRounding(roundInterval);
                }
            } else if (eventType == EventType.CLOCK_OUT) {
                if (originalTimestamp.isAfter(shiftStop) && originalTimestamp.isBefore(shiftStop.plusMinutes(roundInterval))) {
                    adjustedTimestamp = shiftStop;
                    adjustmentType = PunchAdjustmentType.SHIFT_STOP;
                } else if (originalTimestamp.isBefore(shiftStop) && originalTimestamp.isAfter(shiftStop.minusMinutes(gracePeriod))) {
                    adjustedTimestamp = shiftStop;
                    adjustmentType = PunchAdjustmentType.SHIFT_STOP;
                } else if (originalTimestamp.isBefore(shiftStop.minusMinutes(gracePeriod)) && originalTimestamp.compareTo(shiftStop.minusMinutes(dockPenalty))>=0) {
                    adjustedTimestamp = shiftStop.minusMinutes(dockPenalty);
                    adjustmentType = PunchAdjustmentType.SHIFT_DOCK;
                } else if (originalTimestamp.isAfter(lunchStart) && originalTimestamp.isBefore(lunchStop)) {
                    adjustedTimestamp = lunchStart;
                    adjustmentType = PunchAdjustmentType.LUNCH_START;
                }
                else if (originalTimestamp.isAfter(lunchStop) && originalTimestamp.isBefore(shiftStop.minusMinutes(dockPenalty))){
                    adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;
                    // LINE ADDED BY JORDAN \\
                    calculateRounding(roundInterval);
                }
            }
        }
    }
    
    // *** METHOD ADDED BY JORDAN *** \\
    // Method to calculate rounding for IntervalRound
    private void calculateRounding(int roundInterval){
        int minutes = originalTimestamp.getMinute();
        int seconds = originalTimestamp.getSecond();
        int minutesPastInterval = minutes % roundInterval;
        int timeTillNextInterval = roundInterval - minutesPastInterval;
        double halfInterval = roundInterval / 2;
        int halfPossibleSeconds = 30;

        // Check for exact interval match
        if ( minutesPastInterval == 0){ 
            // determine if seconds are off
            if (seconds == 0){
                adjustmentType = PunchAdjustmentType.NONE;
            }else{
                adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;
            }
        }
        else {
            adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;

            // determine where in the interval the minutes falls
            if (minutesPastInterval == Math.floor(halfInterval)){
                // determine if we should round up or down
                if ( seconds < halfPossibleSeconds ){ 
                    // calc round down
                    adjustedTimestamp = adjustedTimestamp.minusMinutes(minutesPastInterval);
                } else{
                    // calc round up
                    adjustedTimestamp = adjustedTimestamp.plusMinutes(timeTillNextInterval);
                }
            }
            else if (minutesPastInterval < halfInterval){
                // calc round down
                adjustedTimestamp = adjustedTimestamp.minusMinutes(minutesPastInterval);
            } else {
                // calc round up
                adjustedTimestamp = adjustedTimestamp.plusMinutes(timeTillNextInterval);
            }
        }
    }
    
    // toString method-formats and returns a string that includes the badge ID, event type, day of the week in uppercase, and the original timestamp.
    //***FS-The tests are failing due to output formatting. EX: expected:<[#08D01475 CLOCK IN: TUE] 09/18/2018 11:59:33> but was:<[Tue] 09/18/2018 11:59:33>
    @Override
    public String toString(){
        return printOriginal();
    }

    // Method to print original timestamp
   public String printOriginal() {
       StringBuilder s = new StringBuilder();
            s.append("#").append(badge.getId()).append(" ").append(eventType.toString());
            s.append(": ").append(getTimestampAsString(originalTimestamp));
        return s.toString();
    }

    // Method to print adjusted timestamp
    public String printAdjusted() {
        if (adjustedTimestamp != null) {
            StringBuilder s = new StringBuilder();
            s.append("#").append(badge.getId()).append(" ").append(eventType.toString());
            s.append(": ").append(getTimestampAsString(adjustedTimestamp));
            s.append(" (").append(adjustmentType).append(")");
            return s.toString();
        } else {
            return "No adjustment";
        }
    }

    /*Leaving these here in case needed in the future --FS       
            if (adjustmentType == PunchAdjustmentType.NONE) {
                long minutes = adjustedTimestamp.getMinute();
                long lowerBound = (long) (Math.floor((double) minutes / roundInterval) * roundInterval);
                long upperBound = (long) (Math.ceil((double) minutes / roundInterval) * roundInterval);

                // Determine which bound is closer
                long roundedMinutes = (minutes - lowerBound < upperBound - minutes) ? lowerBound : upperBound;

                    if (minutes != roundedMinutes) {  // Ensure we only round if needed
                        if (roundedMinutes >= 60) {
                            adjustedTimestamp = adjustedTimestamp.plusHours(1).withMinute(0);
                        } else {
                         adjustedTimestamp = adjustedTimestamp.withMinute((int) roundedMinutes).withSecond(0).withNano(0);
                        }
            adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;
                    }
            }
    */
    
}
