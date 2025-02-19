package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;

public class PunchDAO {
    private final DAOFactory daoFactory;
    
    // Query Statements
    private static final String QUERY_LIST = "SELECT * FROM event WHERE badgeid = ? AND DATE(timestamp) = ? ORDER BY timestamp";
    private static final String QUERY_FIND = "SELECT * FROM event WHERE id = ?";
    private static final String QUERY_GETEVENT = "SELECT * FROM eventtype WHERE id = ?";
    
    // constructor for PunchDAO
    PunchDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    // Method for finding a specific punch
    public Punch find(int id) {
        Punch punch = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        BadgeDAO bd1 = daoFactory.getBadgeDAO();
                
        try {
            // create connection to Database and set up prepared statement
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND);
                ps.setInt(1, id);
                
                // Execute the query and determine if it returned results
                boolean hasresults = ps.execute();
                if (hasresults) {
                    // get result set and build a punch object to hold the data stored in the retrieved record
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        int numID = rs.getInt("id");
                        int terminalID = rs.getInt("terminalid");
                        Badge badge = bd1.find(rs.getString("badgeid"));
                        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                        EventType eventtype = getEventType(rs.getInt("eventtypeid"));
                        punch = new Punch(numID, terminalID, badge, timestamp, eventtype);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return punch;
    }
    
    // Method for listing several punches made by one person
    public ArrayList list(Badge b, LocalDate time) {
        Punch punch = null;
        ArrayList<Punch> results = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        // convert the provided LocalDate to a Timestamp object for the database
        LocalDateTime convert = time.atStartOfDay();
        Timestamp ts = Timestamp.valueOf(convert);
      
        try {
            // create connection to Database and set up prepared statement
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_LIST);
                ps.setString(1, b.getId());
                ps.setTimestamp(2, ts);
                
                // execute query and determine if it gets results
                boolean hasresults = ps.execute();
                if (hasresults) {
                    rs = ps.getResultSet();
                    // loop through result set and create punch objects to hold data obtained from database records
                    while (rs.next()) {
                        int numID = rs.getInt("id");
                        int terminalID = rs.getInt("terminalid");
                        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                        EventType eventtype = getEventType(rs.getInt("eventtypeid"));
                        punch = new Punch(numID, terminalID, b, timestamp, eventtype);
                        // add the created punch to the results list
                        results.add(punch);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return results;
    }
    
    // method for getting the eventtype based on an eventtypeid
    public EventType getEventType(int eventTypeID){
        PreparedStatement ps = null;
        ResultSet rs = null;
        EventType event = null;
        
        try{
            // create connection to Database and set up prepared statement
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)){
                ps = conn.prepareStatement(QUERY_GETEVENT);
                ps.setInt(1, eventTypeID);
                // Execute the query and determine if it returned results
                boolean hasresults = ps.execute();
                if (hasresults) {
                    rs = ps.getResultSet();
                    // check the returned record to see which event was referenced
                    while (rs.next()) {
                        switch(rs.getString("description")){
                            case "Clock-Out Punch":
                                // event = EventType.valueOf("CLOCK OUT");
                                event = EventType.CLOCK_OUT;
                                break;
                            case "Clock-In Punch":
                                // event = EventType.valueOf("CLOCK IN");
                                event = EventType.CLOCK_IN;
                                break;
                            case "Clock Time Out":
                                // event = EventType.valueOf("TIME OUT");
                                event = EventType.TIME_OUT;
                                break;
                            default:
                                event = null;
                        }
                    }
                }
            }
        }catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return event;
    }
    
    /*
    public Punch create(Punch punch) {
        int generatedId = 0;
        String query = "INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, punch.getTerminalId());
            stmt.setString(2, punch.getBadge().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(punch.getOriginalTimestamp()));
            stmt.setInt(4, punch.getEventType().ordinal());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                generatedId = rs.getInt(1);
                punch.setId(generatedId);  // Update the Punch object with the new ID
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return punch;  // Return the full Punch object, not just the ID
    }
*/
}