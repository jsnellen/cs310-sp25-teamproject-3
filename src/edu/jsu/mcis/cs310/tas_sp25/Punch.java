package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Punch {
    
    private int id;
    private int terminalId;
    private Badge badge;
    private LocalDateTime originalTimestamp;
    private LocalDateTime adjustedTimestamp;
    private EventType eventType;
    private PunchAdjustmentType adjustmentType;
    
    // date time format and formatter
    String dateTimeFormat = "EEE MM/dd/yyy HH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

    public Punch(int terminalId, Badge badge, EventType eventType) {
        this.terminalId = terminalId;
        this.badge = badge;
        this.eventType = eventType;
        this.originalTimestamp = LocalDateTime.now();
        this.adjustedTimestamp = null;
        this.adjustmentType = null;
    }

    public Punch(int id, int terminalId, Badge badge, LocalDateTime originalTimestamp, EventType eventType) {
        this.id = id;
        this.terminalId = terminalId;
        this.badge = badge;
        this.originalTimestamp = originalTimestamp;
        this.eventType = eventType;
        this.adjustedTimestamp = null;
        this.adjustmentType = null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getTerminalid() { return terminalId; }
    public Badge getBadge() { return badge; }
    public LocalDateTime getOriginaltimestamp() { return originalTimestamp; }
    public LocalDateTime getAdjustedTimestamp() { return adjustedTimestamp; }
    public EventType getPunchtype() { return eventType; }
    public PunchAdjustmentType getAdjustmentType() { return adjustmentType; }
    
    public String getTimestampAsString(LocalDateTime timestamp){
        return timestamp.format(formatter);
    }

    public void setAdjustedTimestamp(LocalDateTime adjustedTimestamp) {
        this.adjustedTimestamp = adjustedTimestamp;
    }

    public void setAdjustmentType(PunchAdjustmentType adjustmentType) {
        this.adjustmentType = adjustmentType;
    }
    
    public void adjust(Shift shift) {
        if (shift == null || originalTimestamp == null) return;

        LocalDateTime shiftStart = originalTimestamp.with(shift.getStart());
        LocalDateTime shiftStop = originalTimestamp.with(shift.getStop());
        LocalDateTime lunchStart = originalTimestamp.with(shift.getLunchStart());
        LocalDateTime lunchStop = originalTimestamp.with(shift.getLunchStop());
        
        int roundInterval = shift.getRoundInterval();
        int gracePeriod = shift.getGracePeriod();
        int dockPenalty = shift.getDockPenalty();

        // Default adjustment type is None
        adjustmentType = PunchAdjustmentType.NONE;
        adjustedTimestamp = originalTimestamp.truncatedTo(ChronoUnit.MINUTES);

        // Adjustment rules
        if (eventType == EventType.CLOCK_IN) {
            if (originalTimestamp.isBefore(shiftStart) && originalTimestamp.isAfter(shiftStart.minusMinutes(roundInterval))) {
                adjustedTimestamp = shiftStart;
                adjustmentType = PunchAdjustmentType.SHIFT_START;
            } else if (originalTimestamp.isAfter(shiftStart) && originalTimestamp.isBefore(shiftStart.plusMinutes(gracePeriod))) {
                adjustedTimestamp = shiftStart;
                adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;
            } else if (originalTimestamp.isAfter(shiftStart.plusMinutes(gracePeriod)) && originalTimestamp.isBefore(shiftStart.plusMinutes(dockPenalty))) {
                adjustedTimestamp = shiftStart.plusMinutes(dockPenalty);
                adjustmentType = PunchAdjustmentType.SHIFT_DOCK;
            } else if (originalTimestamp.isAfter(lunchStart) && originalTimestamp.isBefore(lunchStop)) {
                adjustedTimestamp = lunchStop;
                adjustmentType = PunchAdjustmentType.LUNCH_STOP;
            }
        } 
        else if (eventType == EventType.CLOCK_OUT) {
            if (originalTimestamp.isAfter(shiftStop) && originalTimestamp.isBefore(shiftStop.plusMinutes(roundInterval))) {
                adjustedTimestamp = shiftStop;
                adjustmentType = PunchAdjustmentType.SHIFT_STOP;
            } else if (originalTimestamp.isBefore(shiftStop) && originalTimestamp.isAfter(shiftStop.minusMinutes(gracePeriod))) {
                adjustedTimestamp = shiftStop;
                adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;
            } else if (originalTimestamp.isBefore(shiftStop.minusMinutes(gracePeriod)) && originalTimestamp.isAfter(shiftStop.minusMinutes(dockPenalty))) {
                adjustedTimestamp = shiftStop.minusMinutes(dockPenalty);
                adjustmentType = PunchAdjustmentType.SHIFT_DOCK;
            } else if (originalTimestamp.isAfter(lunchStart) && originalTimestamp.isBefore(lunchStop)) {
                adjustedTimestamp = lunchStart;
                adjustmentType = PunchAdjustmentType.LUNCH_START;
            }
        }

        // Apply rounding if no specific adjustment rule was applied
        if (adjustmentType == PunchAdjustmentType.NONE) {
            long minutes = adjustedTimestamp.getMinute();
            long roundedMinutes = Math.round((double) minutes / roundInterval) * roundInterval;
            adjustedTimestamp = adjustedTimestamp.withMinute((int) roundedMinutes).withSecond(0).withNano(0);
            adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;
        }
    }

    @Override
    public String toString() {
        return printOriginal();
    }
    
    // Test String to visualize an output -> "#D2C39273 CLOCK IN: WED 09/05/2018 07:00:07"
    public String printOriginal() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(badge.getId()).append(' ');
        s.append(eventType).append(": ").append(originalTimestamp.format(formatter).toUpperCase());

        return s.toString();
    }
    public String printAdjusted() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(badge.getId()).append(' ');
        s.append(eventType).append(": ").append(adjustedTimestamp.format(formatter).toUpperCase());

        return s.toString();
    }
}
