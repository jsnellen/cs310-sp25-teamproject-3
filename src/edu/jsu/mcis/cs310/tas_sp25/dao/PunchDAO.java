package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;

/**
 * <p>Data Access Object (DAO) for managing {@link Punch} records in the TAS database.</p>
 * <p>Supports finding, listing, and creating punch entries based on badge, date, and ID.</p>
 * 
 * <p>Interacts with the "event" table in the database.</p>
 * 
 * @author Group
 */
public class PunchDAO {

    /**
     * The factory for accessing shared database resources and related DAOs.
     */
    private final DAOFactory daoFactory;

    // SQL query constants
    private static final String QUERY_LIST = "SELECT * FROM event WHERE badgeid = ? AND DATE(timestamp) = ? ORDER BY timestamp";
    private static final String QUERY_FIND = "SELECT * FROM event WHERE id = ?";
    private static final String QUERY_CREATE = "INSERT INTO event(terminalid, badgeid, timestamp, eventtypeid) VALUES (?, ?, ?, ?)";

    /**
     * Constructs a PunchDAO using the specified DAOFactory.
     *
     * @param daoFactory the factory to obtain connections and related DAOs
     */
    PunchDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Finds a punch by its ID.
     *
     * @param id the punch ID
     * @return the {@link Punch} object if found, or {@code null} otherwise
     * @throws DAOException if a database error occurs
     */
    public Punch find(int id) {
        Punch punch = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        BadgeDAO bd1 = daoFactory.getBadgeDAO();

        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND);
                ps.setInt(1, id);

                boolean hasresults = ps.execute();
                if (hasresults) {
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
            if (rs != null) try { rs.close(); } catch (SQLException e) { throw new DAOException(e.getMessage()); }
            if (ps != null) try { ps.close(); } catch (SQLException e) { throw new DAOException(e.getMessage()); }
        }

        return punch;
    }

    /**
     * Retrieves all punches for a specific badge on a given date.
     *
     * @param b    the {@link Badge} to search for
     * @param time the specific {@link LocalDate} to retrieve punches for
     * @return an {@link ArrayList} of {@link Punch} objects
     * @throws DAOException if a database error occurs
     */
    public ArrayList<Punch> list(Badge b, LocalDate time) {
        ArrayList<Punch> results = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        LocalDateTime convert = time.atStartOfDay();
        Timestamp ts = Timestamp.valueOf(convert);

        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_LIST);
                ps.setString(1, b.getId());
                ps.setTimestamp(2, ts);

                boolean hasresults = ps.execute();
                if (hasresults) {
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        int numID = rs.getInt("id");
                        int terminalID = rs.getInt("terminalid");
                        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                        EventType eventtype = EventType.values()[rs.getInt("eventtypeid")];
                        Punch punch = new Punch(numID, terminalID, b, timestamp, eventtype);
                        results.add(punch);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { throw new DAOException(e.getMessage()); }
            if (ps != null) try { ps.close(); } catch (SQLException e) { throw new DAOException(e.getMessage()); }
        }

        return results;
    }

    /**
     * Retrieves all punches for a badge within a given date range (inclusive).
     *
     * @param badge the {@link Badge} to search for
     * @param begin the start date (inclusive)
     * @param end   the end date (inclusive)
     * @return an {@link ArrayList} of {@link Punch} objects over the range
     */
    public ArrayList<Punch> list(Badge badge, LocalDate begin, LocalDate end) {
        ArrayList<Punch> punchList = new ArrayList<>();

        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            ArrayList<Punch> dailyPunches = list(badge, date);
            punchList.addAll(dailyPunches);
        }

        return punchList;
    }

    /**
     * Inserts a new {@link Punch} record into the database.
     *
     * @param punch the {@link Punch} object to insert
     * @return the generated punch ID if successful, or 0 if the insert fails
     * @throws DAOException if a database error occurs
     */
    public int create(Punch punch) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int punchID = 0;

        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                Employee emp = daoFactory.getEmployeeDAO().find(punch.getBadge());

                if (emp != null) {
                    int departmentTermID = emp.getDepartment().getTerminalId();

                    if (punch.getTerminalid() == 0 || punch.getTerminalid() == departmentTermID) {
                        Timestamp tmstmp = Timestamp.valueOf(punch.getOriginaltimestamp().withNano(0));
                        ps = conn.prepareStatement(QUERY_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, punch.getTerminalid());
                        ps.setString(2, punch.getBadge().getId());
                        ps.setTimestamp(3, tmstmp);
                        ps.setInt(4, punch.getPunchtype().ordinal());

                        int numRows = ps.executeUpdate();
                        if (numRows > 0) {
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
            if (rs != null) try { rs.close(); } catch (SQLException e) { throw new DAOException(e.getMessage()); }
            if (ps != null) try { ps.close(); } catch (SQLException e) { throw new DAOException(e.getMessage()); }
        }

        return punchID;
    }
}
