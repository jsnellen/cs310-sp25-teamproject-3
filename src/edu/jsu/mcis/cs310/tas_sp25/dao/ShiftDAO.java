package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;

/**
 *
 * @author magan
 */
public class ShiftDAO {
    
    private final Connection connection;
    private final DAOFactory daoFactory;

    // Constructor to initialize database connection
    public ShiftDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.connection = daoFactory.getConnection();
    }
    
    private static final String QUERY_FIND_BY_ID = "SELECT * FROM shift WHERE id = ?";
    private static final String QUERY_FIND_BY_BADGE = "SELECT shift_id FROM employee_shifts WHERE badge_id = ?";

    
    // Finds a Shift by Shift ID
     
    public Shift find(int shiftId) {
        Shift shift = null; 
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (connection != null && connection.isValid(0)) {
                ps = connection.prepareStatement(QUERY_FIND_BY_ID);
                ps.setInt(1, shiftId);
                rs = ps.executeQuery();
                
                if (rs.next()) {
                    // Create HashMap to store shift data
                    HashMap<String, String> shiftData = new HashMap<>();
                    
                    shiftData.put("id", String.valueOf(rs.getInt("id")));
                    shiftData.put("start_time", rs.getString("shiftstart"));
                    shiftData.put("stop_time", rs.getString("shiftstop"));
                    shiftData.put("lunch_start", rs.getString("lunchstart"));
                    shiftData.put("lunch_stop", rs.getString("lunchstop"));
                    
                    // Calculate durations (handling potential NULL values)
                    int lunchDuration = calculateDuration(rs.getString("lunchstart"), rs.getString("lunchstop"));
                    int shiftDuration = calculateDuration(rs.getString("shiftstart"), rs.getString("shiftstop"));

                    shiftData.put("lunchduration", String.valueOf(lunchDuration));
                    shiftData.put("shiftduration", String.valueOf(shiftDuration));

                    shift = new Shift(shiftData);
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

    /**
     * Finds a Shift by Badge ID
     */
    public Shift find(String badgeId) {
        Shift shift = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (connection != null && connection.isValid(0)) {
                ps = connection.prepareStatement(QUERY_FIND_BY_BADGE);
                ps.setString(1, badgeId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    int shiftId = rs.getInt("shift_id"); // Get the shift ID from employee_shifts table
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
