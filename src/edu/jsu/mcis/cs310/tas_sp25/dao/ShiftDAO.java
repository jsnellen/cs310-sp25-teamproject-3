package edu.jsu.mcis.cs310.tas_sp25.dao;

import com.mysql.cj.jdbc.result.ResultSetMetaData;
import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author magan
 */
public class ShiftDAO {
    
    private final DAOFactory daoFactory;

    // Constructor to initialize database connection
    public ShiftDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    private static final String QUERY_FIND_BY_ID = "SELECT * FROM shift WHERE id = ?";
    private static final String QUERY_FIND_BY_BADGE = "SELECT * FROM employee WHERE badgeid = ?";

    // Finds a Shift by Shift ID
    public Shift find(int shiftId) {
        Shift shift = null; 
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Create connection to Database and set up prepared statement
            Connection conn = daoFactory.getConnection();
            if (conn != null && conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_BY_ID);
                ps.setInt(1, shiftId);
                rs = ps.executeQuery();
                
                if (rs.next()) {
                    // Create HashMap to store shift data
                    HashMap<String, String> shiftData = new HashMap<>();
                    
                    // Get metadata about the ResultSet
                    ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Loop through all columns dynamically
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = rs.getString(i); // Get column value as string
                        shiftData.put(columnName, columnValue);
                    }
                    
                    // Calculate durations (handling potential NULL values)
                    int lunchDuration = calculateDuration(rs.getString("lunchstart"), rs.getString("lunchstop"));
                    int shiftDuration = calculateDuration(rs.getString("shiftstart"), rs.getString("shiftstop"));

                    shiftData.put("lunchduration", String.valueOf(lunchDuration));
                    shiftData.put("shiftduration", String.valueOf(shiftDuration));

                    // Validate shiftData entries
                    shift = new Shift(validateShiftData(shiftData));
                }
            }
        } 
        catch (SQLException e) {
            throw new DAOException("Error finding shift by ID: " + e.getMessage());
        } 
        finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new DAOException("Error closing resources: " + e.getMessage());
            }
        }

        return shift;
    }

    // Finds a Shift by Badge ID
    public Shift find(Badge badge) {
        Shift shift = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();
            if (conn != null && conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_BY_BADGE);
                ps.setString(1, badge.getId());
                rs = ps.executeQuery();

                if (rs.next()) {
                    int shiftId = rs.getInt("shiftid"); // Get the shift ID from employee_shifts table
                    shift = find(shiftId); // Use existing find() method to get full Shift details
                }
            }
        } 
        catch (SQLException e) {
            throw new DAOException("Error finding shift by badge ID: " + e.getMessage());
        } 
        finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new DAOException("Error closing resources: " + e.getMessage());
            }
        }

        return shift;
    }

    // Helper method to validate shift data
    private HashMap<String, String> validateShiftData(HashMap<String, String> shiftData) {
        HashMap<String, String> validatedShiftData = new HashMap<>();
        validatedShiftData.put("id", Objects.requireNonNull(shiftData.get("id"), "ID is missing"));
        validatedShiftData.put("description", Objects.requireNonNull(shiftData.get("description"), "Description is missing"));
        validatedShiftData.put("start_time", Objects.requireNonNullElse(shiftData.get("shiftstart"), "00:00")); // Updated key
        validatedShiftData.put("stop_time", Objects.requireNonNullElse(shiftData.get("shiftstop"), "00:00")); // Updated key
        validatedShiftData.put("lunch_start", Objects.requireNonNullElse(shiftData.get("lunchstart"), "00:00")); // Updated key
        validatedShiftData.put("lunch_stop", Objects.requireNonNullElse(shiftData.get("lunchstop"), "00:00")); // Updated key
        validatedShiftData.put("lunchduration", Objects.requireNonNullElse(shiftData.get("lunchduration"), "0"));
        validatedShiftData.put("shiftduration", Objects.requireNonNullElse(shiftData.get("shiftduration"), "0"));
        return validatedShiftData;
    }

    // Helper method to calculate duration in minutes
    private int calculateDuration(String start, String stop) {
        if (start == null || stop == null) {
            return 0; // Handle null values 
        }
        
        LocalTime startTime = LocalTime.parse(start);
        LocalTime stopTime = LocalTime.parse(stop);
        
        return (int) Duration.between(startTime, stopTime).toMinutes();
    }
}
