package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public void setAdjustedTimestamp(LocalDateTime adjustedTimestamp) {
        this.adjustedTimestamp = adjustedTimestamp;
    }

    public void setAdjustmentType(PunchAdjustmentType adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public void adjustPunch(LocalDateTime adjustedTimestamp, PunchAdjustmentType adjustmentType) {
        this.adjustedTimestamp = adjustedTimestamp;
        this.adjustmentType = adjustmentType;
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
        s.append(eventType).append(": ").append(originalTimestamp.format(formatter).toUpperCase());

        return s.toString();
    }
}