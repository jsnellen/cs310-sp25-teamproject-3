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
    private static final String QUERY_CREATE = "INSERT INTO event(terminalid, badgeid, timestamp, eventtypeid) VALUES (?, ?, ?, ?)";

    // Constructor for PunchDAO
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
            // Create connection to Database and set up prepared statement
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND);
                ps.setInt(1, id);

                // Execute the query and determine if it returned results
                boolean hasresults = ps.execute();
                if (hasresults) {
                    // Get result set and build a punch object to hold the data stored in the retrieved record
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        int numID = rs.getInt("id");
                        int terminalID = rs.getInt("terminalid");
                        Badge badge = bd1.find(rs.getString("badgeid"));
                        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                        EventType eventtype = EventType.values()[rs.getInt("eventtypeid")];
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

    // Method for listing several punches made by one person on a single day
    public ArrayList<Punch> list(Badge b, LocalDate time) {
        Punch punch = null;
        ArrayList<Punch> results = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        // Convert the provided LocalDate to a Timestamp object for the database
        LocalDateTime convert = time.atStartOfDay();
        Timestamp ts = Timestamp.valueOf(convert);

        try {
            // Create connection to Database and set up prepared statement
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_LIST);
                ps.setString(1, b.getId());
                ps.setTimestamp(2, ts);

                // Execute query and determine if it gets results
                boolean hasresults = ps.execute();
                if (hasresults) {
                    rs = ps.getResultSet();
                    // Loop through result set and create punch objects to hold data obtained from database records
                    while (rs.next()) {
                        int numID = rs.getInt("id");
                        int terminalID = rs.getInt("terminalid");
                        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                        EventType eventtype = EventType.values()[rs.getInt("eventtypeid")];
                        punch = new Punch(numID, terminalID, b, timestamp, eventtype);
                        // Add the created punch to the results list
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

    // New method to list punches over a date range
    public ArrayList<Punch> list(Badge badge, LocalDate begin, LocalDate end) {
        ArrayList<Punch> punchList = new ArrayList<>();

        // Iterate through each date in the range (inclusive of begin and end)
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            // Get punches for the current date using the existing list method
            ArrayList<Punch> dailyPunches = list(badge, date);

            // Add all punches from the current date to the result list
            punchList.addAll(dailyPunches);
        }

        return punchList;
    }

    // Method for creating new punches and inserting them into the database
    public int create(Punch punch) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    int punchID = 0;
    try {
        Connection conn = daoFactory.getConnection();
        if (conn.isValid(0)) {
            // Find the employee associated with this punch using the badge
            Employee emp = daoFactory.getEmployeeDAO().find(punch.getBadge());

            if (emp != null) {
                int departmentTermID = emp.getDepartment().getTerminalId();

                if (punch.getTerminalid() == 0 || punch.getTerminalid() == departmentTermID) {
                    // Set up prepared statement
                    Timestamp tmstmp = Timestamp.valueOf(punch.getOriginaltimestamp().withNano(0));
                    ps = conn.prepareStatement(QUERY_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, punch.getTerminalid());
                    ps.setString(2, punch.getBadge().getId());
                    ps.setTimestamp(3, tmstmp);
                    ps.setInt(4, punch.getPunchtype().ordinal());
                    // Create a new record in the event table with the data from this punch
                    int numRows = ps.executeUpdate();
                    if (numRows > 0) {
                        // Retrieve the punchID from the newly created punch
                        rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            punchID = rs.getInt(1);
                        }
                    }
                } else {
                    punchID = 0; // Invalid terminal ID
                }
            } else {
                punchID = 0; // Employee not found
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
    return punchID;
    }
}
