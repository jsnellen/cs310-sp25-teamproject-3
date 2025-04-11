package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import com.github.cliftonlabs.json_simple.*;
import edu.jsu.mcis.cs310.tas_sp25.Punch;
import edu.jsu.mcis.cs310.tas_sp25.Shift;
import edu.jsu.mcis.cs310.tas_sp25.EventType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * Utility class for DAOs.  This is a final, non-constructable class containing
 * common DAO logic and other repeated and/or standardized code, refactored into
 * individual static methods.
 * 
 */
public final class DAOUtility {
    
    // Utility Method to get a list of punches and convert them to a JSON string
    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist){
        // set up variables to store data
        JsonArray jsonData = new JsonArray();
        
        // for each punch in dailypunchlist
        for (Punch punch : dailypunchlist){
            // copy data from dailypunchlist into punchData
            JsonObject punchData = new JsonObject();
            punchData.put("id", String.valueOf(punch.getId()) );
            punchData.put("badgeid", punch.getBadge().getId());
            punchData.put("terminalid", String.valueOf(punch.getTerminalid()) );
            punchData.put("punchtype", punch.getPunchtype().toString() );
            punchData.put("adjustmenttype", punch.getAdjustmentType().toString() );
            punchData.put("originaltimestamp", punch.getTimestampAsString(punch.getOriginaltimestamp()));
            punchData.put("adjustedtimestamp", punch.getTimestampAsString(punch.getAdjustedTimestamp()));
            
            // add punchData to jsonData
            jsonData.add(punchData);
        }
        return Jsoner.serialize(jsonData);
    }
    
    // Utility Method to encode information accumlated by an emloyee during one pay period into a JSONString
 public static String getPunchListPlusTotalsAsJSON(ArrayList<Punch> punchlist, Shift shift) {
        JsonObject jsonData = new JsonObject();
        
        int totalMinutes = calculateTotalMinutes(punchlist, shift);
        BigDecimal absenteeism = calculateAbsenteeism(punchlist, shift);
        String punchListString = getPunchListAsJSON(punchlist);
        
        // Format absenteeism 
        String absenteeismString = String.format("%.2f%%", absenteeism);
        
        jsonData.put("absenteeism", absenteeismString);
        jsonData.put("totalminutes", totalMinutes);
        try {
            jsonData.put("punchlist", Jsoner.deserialize(punchListString));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return Jsoner.serialize(jsonData);
    }
    
    public static int calculateTotalMinutes(ArrayList<Punch> punchlist, Shift shift) {
        int totalMinutes = 0;
        boolean isClockedIn = false;
        Punch clockInPunch = null;

        // Sort punches by timestamp to ensure correct order
        punchlist.sort(Comparator.comparing(Punch::getAdjustedTimestamp));

        for (Punch punch : punchlist) {
            switch (punch.getPunchtype()) {
                case CLOCK_IN:
                    if (!isClockedIn) {
                        clockInPunch = punch;
                        isClockedIn = true;
                    }
                    break;
                case CLOCK_OUT:
                    if (isClockedIn) {
                        totalMinutes += calculateDuration(clockInPunch, punch, shift);
                        isClockedIn = false;
                    }
                    break;
                case TIME_OUT:
                    // Skip "time out" punches
                    isClockedIn = false;
                    break;
            }
        }
        
        return totalMinutes;
    }

    private static int calculateDuration(Punch start, Punch end, Shift shift) {
        int time = (int) Duration.between(
            start.getAdjustedTimestamp(), 
            end.getAdjustedTimestamp()
        ).toMinutes();
        
        // Check if the day is a weekend
        DayOfWeek dayOfWeek = start.getAdjustedTimestamp().getDayOfWeek();
        boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
        if (!isWeekend){
            // Deduct lunch break if applicable
            if (time > shift.getLunchThreshold()) {
                time -= shift.getLunchDuration();
            }
        }
        return time;
    }

    public static BigDecimal calculateAbsenteeism(ArrayList<Punch> punchlist, Shift shift) {
        int totalWorked = calculateTotalMinutes(punchlist, shift);
    
        // Get unique weekdays (Monday-Friday)
        Set<LocalDate> weekDays = new HashSet<>();
        for (Punch p : punchlist) {
            LocalDate date = p.getAdjustedTimestamp().toLocalDate();
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                weekDays.add(date);
            }
        }
    
        // Calculate scheduled minutes (weekdays only)
        int dailyScheduled = shift.getShiftDuration();
        if (dailyScheduled > shift.getLunchThreshold()) {
            dailyScheduled -= shift.getLunchDuration();
        }
        int totalScheduled = /*weekDays.size()*/ 5 * dailyScheduled;
    
        // Debug output
        System.out.println("[DEBUG] Weekdays worked: " + weekDays.size());
        System.out.println("[DEBUG] Minutes per scheduled weekday: " + dailyScheduled);
        System.out.println("[DEBUG] Total Worked: " + totalWorked);
        System.out.println("[DEBUG] Total Scheduled: " + totalScheduled);
    
        if (totalScheduled == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    
        return new BigDecimal(totalScheduled - totalWorked)
            .divide(new BigDecimal(totalScheduled), 6, RoundingMode.HALF_UP)
            .multiply(new BigDecimal(100))
            .setScale(2, RoundingMode.HALF_UP);
    }
}