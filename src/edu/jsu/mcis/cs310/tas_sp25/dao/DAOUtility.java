package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import com.github.cliftonlabs.json_simple.*;
import edu.jsu.mcis.cs310.tas_sp25.*;

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
            punchData.put("originaltimestamp", punch.printOriginal());
            punchData.put("adjustedtimestamp", punch.printAdjusted());
            
            // add punchData to jsonData
            jsonData.add(punchData);
        }
        // encode jsonData to jsonString and return jsonString
        jsonString = Jsoner.serialize(jsonData);
        return jsonString;
    }
}