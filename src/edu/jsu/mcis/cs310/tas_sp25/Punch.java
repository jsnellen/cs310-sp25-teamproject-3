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
 
    // Date time format and formatter
    String dateTimeFormat = "EEE MM/dd/yyyy HH:mm:ss";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

    // Constructor for new punch
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
        return timestamp.format(formatter);
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
    } else if (eventType == EventType.CLOCK_OUT) {
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

    // Separate handling for lunchStart and lunchStop
    if (adjustmentType == PunchAdjustmentType.NONE) {
        if (originalTimestamp.isAfter(lunchStart) && originalTimestamp.isBefore(lunchStop)) {
            adjustedTimestamp = lunchStop;
            adjustmentType = PunchAdjustmentType.LUNCH_STOP;
        } else {
            // Final rounding check
            int minutes = adjustedTimestamp.getMinute();
            int roundedMinutes = ((minutes + roundInterval / 2) / roundInterval) * roundInterval;

            if (minutes != roundedMinutes) { // Round only if necessary
                if (roundedMinutes >= 60) {
                    adjustedTimestamp = adjustedTimestamp.plusHours(1).withMinute(0);
                } else {
                    adjustedTimestamp = adjustedTimestamp.withMinute(roundedMinutes).withSecond(0).withNano(0);
                }
                adjustmentType = PunchAdjustmentType.INTERVAL_ROUND;
            }
        }
    }
}
// toString method-formats and returns a string that includes the badge ID, event type, day of the week in uppercase, and the original timestamp.
//***FS-The tests are failing due to output formatting. EX: expected:<[#08D01475 CLOCK IN: TUE] 09/18/2018 11:59:33> but was:<[Tue] 09/18/2018 11:59:33>
    @Override
public String toString() {
    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
    String dayOfWeek = originalTimestamp.format(dayFormatter).toUpperCase();
    DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    String formattedTimestamp = originalTimestamp.format(timestampFormatter);
    return "#" + badge.getId() + " " + eventType.toString() + ": " + dayOfWeek + " " + formattedTimestamp;
}

    // Method to print original timestamp
   public String printOriginal() {
    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
    String dayOfWeek = originalTimestamp.format(dayFormatter).toUpperCase();
    DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    String formattedTimestamp = originalTimestamp.format(timestampFormatter);
    return "#" + badge.getId() + " " + eventType.toString() + ": " + dayOfWeek + " " + formattedTimestamp;
}

    // Method to print adjusted timestamp
    
public String printAdjusted() {
    if (adjustedTimestamp != null) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");
        String dayOfWeek = adjustedTimestamp.format(dayFormatter).toUpperCase();
        DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        String formattedTimestamp = adjustedTimestamp.format(timestampFormatter);
        return "#" + badge.getId() + " " + eventType.toString() + ": " + dayOfWeek + " " + formattedTimestamp + " (" + adjustmentType + ")";
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
/*       
    @Override
    public String toString(){
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
        if (adjustedTimestamp == null) {
            return printOriginal() + " (NO ADJUSTMENT)";
        }

        StringBuilder s = new StringBuilder();
        s.append('#').append(badge.getId()).append(' ');
        s.append(eventType).append(": ").append(adjustedTimestamp.format(formatter).toUpperCase());

        // Append adjustment type, including " (None)" explicitly when no adjustment was made
        s.append(" (").append(adjustmentType).append(")");

        return s.toString();
    }



    }
    }
}
}
*/
}