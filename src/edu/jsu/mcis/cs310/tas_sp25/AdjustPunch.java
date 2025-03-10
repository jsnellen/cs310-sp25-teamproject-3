package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Punch {
    
    private int id;
    private int terminalId;
    private Badge badge;
    private LocalDateTime originalTimestamp;
    private LocalDateTime adjustedTimestamp;
    private EventType eventType;
    private PunchAdjustmentType adjustmentType;

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
    public int getTerminalId() { return terminalId; }
    public Badge getBadge() { return badge; }
    public LocalDateTime getOriginalTimestamp() { return originalTimestamp; }
    public LocalDateTime getAdjustedTimestamp() { return adjustedTimestamp; }
    public EventType getEventType() { return eventType; }
    public PunchAdjustmentType getAdjustmentType() { return adjustmentType; }

    public void adjust(Shift shift) {
        if (shift == null || originalTimestamp == null) return;

        LocalDateTime shiftStart = originalTimestamp.with(shift.getShiftStart());
        LocalDateTime shiftStop = originalTimestamp.with(shift.getShiftStop());
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
                adjustmentType = PunchAdjustmentType.GRACE_PERIOD;
            } else if (originalTimestamp.isAfter(shiftStart.plusMinutes(gracePeriod)) && originalTimestamp.isBefore(shiftStart.plusMinutes(dockPenalty))) {
                adjustedTimestamp = shiftStart.plusMinutes(dockPenalty);
                adjustmentType = PunchAdjustmentType.DOCK;
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
                adjustmentType = PunchAdjustmentType.GRACE_PERIOD;
            } else if (originalTimestamp.isBefore(shiftStop.minusMinutes(gracePeriod)) && originalTimestamp.isAfter(shiftStop.minusMinutes(dockPenalty))) {
                adjustedTimestamp = shiftStop.minusMinutes(dockPenalty);
                adjustmentType = PunchAdjustmentType.DOCK;
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
        return "Punch: {ID: " + id +
                ", Terminal: " + terminalId +
                ", Badge: " + badge +
                ", Original Timestamp: " + originalTimestamp +
                ", Adjusted Timestamp: " + adjustedTimestamp +
                ", Event Type: " + eventType +
                ", Adjustment Type: " + adjustmentType +
                "}";
    }

    public String printOriginal() {
        return "#" + badge.getId() + " " + originalTimestamp + " " + eventType;
    }

    public String printAdjusted() {
        return "#" + badge.getId() + " " + adjustedTimestamp + " " + eventType + " (" + adjustmentType + ")";
    }
}