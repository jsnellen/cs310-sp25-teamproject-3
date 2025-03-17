package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.Punch;
import edu.jsu.mcis.cs310.tas_sp25.Shift;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import com.github.cliftonlabs.json_simple.*;

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
        String jsonString;
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
        // encode jsonData to jsonString and return jsonString
        jsonString = Jsoner.serialize(jsonData);
        return jsonString;
    }
    
    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift) {
        int totalMinutes = 0;
        boolean isClockedIn = false;
        Punch clockInPunch = null;

        for (Punch punch : dailypunchlist) {
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
        LocalDateTime startTime = start.getAdjustedTimestamp();
        LocalDateTime endTime = end.getAdjustedTimestamp();

        // Calculate duration in minutes
        return (int) Duration.between(startTime, endTime).toMinutes();
    }
}
