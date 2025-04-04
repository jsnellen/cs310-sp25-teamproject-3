package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public static String getPunchListPlusTotalsAsJSON(ArrayList<Punch> punchlist, Shift shift){
        // Set up JsonObjects to store punchlist and totals
        JsonObject jsonData = new JsonObject();
        
        // Get info to be stored
        int totalMinutes = calculateTotalMinutes(punchlist, shift);
        BigDecimal absenteeism = calculateAbsenteeism(punchlist, shift);
        String punchListString = getPunchListAsJSON(punchlist);
        
        // place info into JsonObject
        jsonData.put("absenteeism", absenteeism);
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
                        totalMinutes += calculateDuration(clockInPunch, punch);
                        isClockedIn = false;
                    }
                    break;
                case TIME_OUT:
                // Skip "time out" punches
                    isClockedIn = false;
                    break;
            }
        }

    // Deduct lunch break if applicable
        if (totalMinutes > shift.getShiftDuration() && shift.getLunchDuration() > 0) {
            totalMinutes -= shift.getLunchDuration();
        }

        return totalMinutes;
    }

    private static int calculateDuration(Punch start, Punch end) {
        return (int) Duration.between(
            start.getAdjustedTimestamp(), 
            end.getAdjustedTimestamp()
        ).toMinutes();
    }

    public static BigDecimal calculateAbsenteeism(ArrayList<Punch> punchlist, Shift shift) {
        // Calculate total minutes worked
        int totalWorked = calculateTotalMinutes(punchlist, shift);
        
        // Get all work days (excluding weekends)
        List<LocalDate> workDays = punchlist.stream()
                .map(p -> p.getAdjustedTimestamp().toLocalDate())
                .distinct()
                // *** commented by Jordan ***
                //.filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY)
                //.filter(d -> d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .collect(Collectors.toList());
        
        // Calculate scheduled minutes per day (minus lunch if applicable)
        int scheduledPerDay = shift.getShiftDuration();
        /* // *** Commented by Jordan ***
        if (scheduledPerDay > 360) { // 6 hour threshold
            scheduledPerDay -= shift.getLunchDuration();
        }
        */
        
        // Calculate total scheduled minutes for all work days
        int totalScheduled = workDays.size() * scheduledPerDay;
        
        // Calculate absenteeism percentage
        if (totalScheduled == 0) {
            return BigDecimal.ZERO; // Avoid division by zero
        }
        
        BigDecimal absenteeism = new BigDecimal(totalWorked - totalScheduled)
                .divide(new BigDecimal(totalScheduled), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        
        return absenteeism;
    }
}