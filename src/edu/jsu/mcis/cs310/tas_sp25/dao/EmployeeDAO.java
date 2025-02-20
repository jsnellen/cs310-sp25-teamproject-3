package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;

public class EmployeeDAO {
    private final DAOFactory daoFactory;
    
    // Query Statements
    private static final String QUERY_FIND = "SELECT * FROM employee WHERE id = ?";
    
    // constructor for PunchDAO
    EmployeeDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    // Method for finding a specific employee
    public Employee find(int id) {
        Employee employee = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps.setInt(1, id);
        rs = ps.executeQuery();
        EmployeeDAO emp = daoFactory.getEmployeeDAO();
                
        try {
            // create connection to Database and set up prepared statement
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND);
                ps.setInt(1, id);
                
                //determine if it returned results
                if (rs.next()) {
                String firstname = rs.getString("firstname");
                String middlename = rs.getString("middlename");
                String lastname = rs.getString("lastname");
                LocalDateTime active = rs.getTimestamp("active").toLocalDateTime();
                Badge badge = new BadgeDAO().find(rs.getInt("badge_id"));
                Department department = new DepartmentDAO().find(rs.getInt("department_id "));
                Shift shift = new ShiftDAO().find(rs.getInt("shift_id"));
                EmployeeType employeeType = EmployeeType.values()[rs.getInt("employee_type")];

                employee = new Employee(id, firstname, middlename, lastname, active, badge, department, shift, employeeType);
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
        return employee;
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
}