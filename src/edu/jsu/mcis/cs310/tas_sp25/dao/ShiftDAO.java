package edu.jsu.mcis.cs310.tas_sp25.dao;

import com.mysql.cj.jdbc.result.ResultSetMetaData;
import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Objects;

/**
 * <p>Data Access Object (DAO) for retrieving {@link Shift} records from the TAS database.</p>
 * <p>Supports finding shift data by shift ID or by badge. Automatically calculates and validates
 * shift duration and lunch duration based on the times retrieved.</p>
 * 
 * <p>Interacts with the "shift" and "employee" tables.</p>
 * 
 * @author Group
 */
public class ShiftDAO {

    /**
     * Factory object for database connections and DAO access.
     */
    private final DAOFactory daoFactory;

    private static final String QUERY_FIND_BY_ID = "SELECT s.*, ds.* FROM shift s JOIN dailyschedule ds ON s.dailyscheduleid = ds.id WHERE s.id = ?";
    private static final String QUERY_FIND_BY_BADGE = "SELECT * FROM employee WHERE badgeid = ?";

    /**
     * Constructs a ShiftDAO using the provided DAOFactory.
     *
     * @param daoFactory the factory used to obtain database connections
     */
    public ShiftDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Finds a shift by its ID.
     *
     * @param shiftId the ID of the shift to retrieve
     * @return a {@link Shift} object populated with database data, or {@code null} if not found
     * @throws DAOException if a database error occurs
     */
    public Shift find(int shiftId) {
        Shift shift = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();
            if (conn != null && conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_BY_ID);
                ps.setInt(1, shiftId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    HashMap<String, String> shiftData = new HashMap<>();
                    ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = rs.getString(i);
                        shiftData.put(columnName, columnValue);
                    }

                    // Map fields to match Shift constructor expectations
                    shiftData.put("start_time", rs.getString("start"));
                    shiftData.put("stop_time", rs.getString("stop"));
                    shiftData.put("lunch_start", rs.getString("lunchstart"));
                    shiftData.put("lunch_stop", rs.getString("lunchstop"));
                    shiftData.put("lunchthreshold", rs.getString("lunchthreshold"));
                    shiftData.put("roundinterval", rs.getString("roundinterval"));
                    shiftData.put("graceperiod", rs.getString("graceperiod"));
                    shiftData.put("dockpenalty", rs.getString("dockpenalty"));

                    // Calculate durations (now handled in DailySchedule constructor)
                    shiftData.put("lunchduration", "0"); // Placeholder, calculated in DailySchedule
                    shiftData.put("shiftduration", "0");  // Placeholder, calculated in DailySchedule

                    shift = new Shift(validateShiftData(shiftData));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding shift by ID: " + e.getMessage());
        } finally {
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
     * Finds a shift using an employee's badge ID.
     *
     * @param badge the {@link Badge} used to identify the employee
     * @return the corresponding {@link Shift} object, or {@code null} if not found
     * @throws DAOException if a database error occurs
     */
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
                    int shiftId = rs.getInt("shiftid");
                    shift = find(shiftId);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding shift by badge ID: " + e.getMessage());
        } finally {
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
     * Validates and formats the raw shift data retrieved from the database.
     *
     * @param shiftData raw shift data from the ResultSet
     * @return a validated and cleaned HashMap with all necessary fields
     */
    private HashMap<String, String> validateShiftData(HashMap<String, String> shiftData) {
        HashMap<String, String> validatedShiftData = new HashMap<>();
        validatedShiftData.put("id", Objects.requireNonNull(shiftData.get("id"), "ID is missing"));
        validatedShiftData.put("description", Objects.requireNonNull(shiftData.get("description"), "Description is missing"));
        validatedShiftData.put("start_time", Objects.requireNonNullElse(shiftData.get("start_time"), "00:00"));
        validatedShiftData.put("stop_time", Objects.requireNonNullElse(shiftData.get("stop_time"), "00:00"));
        validatedShiftData.put("lunch_start", Objects.requireNonNullElse(shiftData.get("lunch_start"), "00:00"));
        validatedShiftData.put("lunch_stop", Objects.requireNonNullElse(shiftData.get("lunch_stop"), "00:00"));
        validatedShiftData.put("lunchduration", Objects.requireNonNullElse(shiftData.get("lunchduration"), "0"));
        validatedShiftData.put("shiftduration", Objects.requireNonNullElse(shiftData.get("shiftduration"), "0"));
        validatedShiftData.put("lunchthreshold", Objects.requireNonNullElse(shiftData.get("lunchthreshold"), "0"));
        validatedShiftData.put("roundinterval", Objects.requireNonNullElse(shiftData.get("roundinterval"), "0"));
        validatedShiftData.put("graceperiod", Objects.requireNonNullElse(shiftData.get("graceperiod"), "0"));
        validatedShiftData.put("dockpenalty", Objects.requireNonNullElse(shiftData.get("dockpenalty"), "0"));
        return validatedShiftData;
    }

    /**
     * Calculates the number of minutes between two time strings.
     * (Note: Now handled in DailySchedule constructor, but kept for backward compatibility)
     *
     * @param start the start time string (e.g., "08:00")
     * @param stop  the stop time string (e.g., "17:00")
     * @return the duration in minutes between the two times, or 0 if either is null
     */
    private int calculateDuration(String start, String stop) {
        if (start == null || stop == null) {
            return 0;
        }

        LocalTime startTime = LocalTime.parse(start);
        LocalTime stopTime = LocalTime.parse(stop);

        return (int) Duration.between(startTime, stopTime).toMinutes();
    }
}