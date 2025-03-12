package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Punch {
    
    private int id = -1; // Default to -1 to indicate unassigned ID
    private final int terminalId;
    private final Badge badge;
    private final LocalDateTime originalTimestamp;
    private LocalDateTime adjustedTimestamp;
    private final EventType eventType;
    private PunchAdjustmentType adjustmentType;

    // Date time format and formatter
    private static final String DATE_TIME_FORMAT = "EEE MM/dd/yyyy HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    // Constructor for new Punch
    public Punch(int terminalId, Badge badge, EventType eventType) {
        this.terminalId = terminalId;
        this.badge = badge;
        this.eventType = eventType;
        this.originalTimestamp = LocalDateTime.now();
        this.adjustedTimestamp = null;
        this.adjustmentType = PunchAdjustmentType.NONE;
    }

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

    // Getter methods with corrected names
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getTerminalId() { return terminalId; }
    public Badge getBadge() { return badge; }
    public LocalDateTime getOriginalTimestamp() { return originalTimestamp; }
    public LocalDateTime getAdjustedTimestamp() { return adjustedTimestamp; }
    public EventType getEventType() { return eventType; }
    public PunchAdjustmentType getAdjustmentType() { return adjustmentType; }

    public void setAdjustedTimestamp(LocalDateTime adjustedTimestamp) {
        this.adjustedTimestamp = adjustedTimestamp;
    }

    public void setAdjustmentType(PunchAdjustmentType adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public void adjust(Shift shift) {
    if (shift == null || originalTimestamp == null) return;

    // Get shift details
    LocalDateTime shiftStart = originalTimestamp.with(shift.getStart());
    LocalDateTime shiftStop = originalTimestamp.with(shift.getStop());
    LocalDateTime lunchStart = originalTimestamp.with(shift.getLunchStart());
    LocalDateTime lunchStop = originalTimestamp.with(shift.getLunchStop());

    int roundInterval = shift.getRoundInterval();
    int gracePeriod = shift.getGracePeriod();
    int dockPenalty = shift.getDockPenalty();

    // Default to no adjustment
    adjustmentType = PunchAdjustmentType.NONE;
    adjustedTimestamp = originalTimestamp.truncatedTo(ChronoUnit.MINUTES);

    // 🛠 **Clock-In Adjustments**
    if (eventType == EventType.CLOCK_IN) {
        if (originalTimestamp.equals(shiftStart)) {  // 🔥 Exact match
            adjustedTimestamp = shiftStart;
            adjustmentType = PunchAdjustmentType.SHIFT_START;
        } 
        else if (originalTimestamp.isBefore(shiftStart) && originalTimestamp.isAfter(shiftStart.minusMinutes(roundInterval))) {
            adjustedTimestamp = shiftStart;
            adjustmentType = PunchAdjustmentType.SHIFT_START;
        } 
        else if (originalTimestamp.isAfter(shiftStart) && originalTimestamp.isBefore(shiftStart.plusMinutes(gracePeriod))) {
            adjustedTimestamp = shiftStart;
            adjustmentType = PunchAdjustmentType.SHIFT_START;
        } 
        else if (originalTimestamp.isAfter(shiftStart.plusMinutes(gracePeriod)) && originalTimestamp.isBefore(shiftStart.plusMinutes(dockPenalty))) {
            adjustedTimestamp = shiftStart.plusMinutes(dockPenalty);
            adjustmentType = PunchAdjustmentType.SHIFT_DOCK;
        } 
        else if (originalTimestamp.isAfter(lunchStart) && originalTimestamp.isBefore(lunchStop)) {
            adjustedTimestamp = lunchStop;
            adjustmentType = PunchAdjustmentType.LUNCH_STOP;
        }
    } 

    // 🛠 **Clock-Out Adjustments**
    else if (eventType == EventType.CLOCK_OUT) {
        if (originalTimestamp.isAfter(shiftStop) && originalTimestamp.isBefore(shiftStop.plusMinutes(roundInterval))) {
            adjustedTimestamp = shiftStop;
            adjustmentType = PunchAdjustmentType.SHIFT_STOP;
        } 
        else if (originalTimestamp.isBefore(shiftStop) && originalTimestamp.isAfter(shiftStop.minusMinutes(gracePeriod))) {
            adjustedTimestamp = shiftStop;
            adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;
        } 
        else if (originalTimestamp.isBefore(shiftStop.minusMinutes(gracePeriod)) && originalTimestamp.isAfter(shiftStop.minusMinutes(dockPenalty))) {
            adjustedTimestamp = shiftStop.minusMinutes(dockPenalty);
            adjustmentType = PunchAdjustmentType.SHIFT_DOCK;
        } 
        else if (originalTimestamp.isAfter(lunchStart) && originalTimestamp.isBefore(lunchStop)) {
            adjustedTimestamp = lunchStart;
            adjustmentType = PunchAdjustmentType.LUNCH_START;
        }
    }

    // 🔁 **Final Rounding Check**
    if (adjustmentType == PunchAdjustmentType.NONE) {
        long minutes = adjustedTimestamp.getMinute();
        long roundedMinutes = Math.round((double) minutes / roundInterval) * roundInterval;

        if (minutes != roundedMinutes) {  // Ensure we only round if needed
            if (roundedMinutes >= 60) {
                adjustedTimestamp = adjustedTimestamp.plusHours(1).withMinute(0);
            } else {
                adjustedTimestamp = adjustedTimestamp.withMinute((int) roundedMinutes).withSecond(0).withNano(0);
            }
            adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;
        }
    }

    // Debug output (optional)
    System.out.println("Adjusted Timestamp: " + adjustedTimestamp);
    System.out.println("Adjustment Type: " + adjustmentType);
}


    @Override
    public String toString() {
        return printOriginal();
    }
    
    // Print the original timestamp
    public String printOriginal() {
        return String.format("#%s %s: %s", badge.getId(), eventType, originalTimestamp.format(FORMATTER).toUpperCase());
    }

    // Print the adjusted timestamp safely
    public String printAdjusted() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(badge.getId()).append(' ');
        s.append(eventType).append(": ");
        
        if (adjustedTimestamp != null) {
            s.append(adjustedTimestamp.format(FORMATTER).toUpperCase());
            s.append(" (").append(adjustmentType).append(')');
        } else {
            s.append("N/A (No Adjustment)");
        }
        
        return s.toString();
    }
}
