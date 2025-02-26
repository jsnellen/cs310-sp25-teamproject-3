package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;

public class EmployeeDAO {
    private final DAOFactory daoFactory;
    
    // Query Statements
    private static final String QUERY_FIND_NUMID = "SELECT * FROM employee WHERE id = ?";
    // *** Query can be adjusted for simplicity ***
    private static final String QUERY_FIND_BADGEID = "SELECT * FROM employee WHERE badgeid = ?";
    
    // constructor for PunchDAO
    EmployeeDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    // Method for finding a specific employee with a numerical id
    public Employee find(int id) {
        Employee employee = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                
        try {
            // create connection to Database and set up prepared statement
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_NUMID);
                ps.setInt(1, id);
                
                //determine if it returned results
                boolean hasResults = ps.execute();
                if(hasResults){
                    rs = ps.getResultSet();
                    if (rs.next()) {
                    // get values to be stored in employee
                    String firstname = rs.getString("firstname");
                    String middlename = rs.getString("middlename");
                    String lastname = rs.getString("lastname");
                    LocalDateTime active = rs.getTimestamp("active").toLocalDateTime();
                    String badgeID = rs.getString("badgeid");
                    int deptID = rs.getInt("departmentid");
                    int shiftID = rs.getInt("shiftid");
                    int empTypeID = rs.getInt("employeetypeid");
                    // use the IDs to get the corresponding objects
                    Badge badge = daoFactory.getBadgeDAO().find(badgeID);
                    Department department = daoFactory.getDepartmentDAO().find(deptID);
                    Shift shift = daoFactory.getShiftDAO().find(shiftID);
                    EmployeeType employeeType = EmployeeType.values()[empTypeID];
                    employee = new Employee(id, firstname, middlename, lastname, active, badge, department, shift, employeeType);
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
        return employee;
    }
    
    // method for finding a single employee using a badge id
    public Employee find(Badge id){
        Employee employee = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
                
        try {
            // create connection to Database and set up prepared statement
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_BADGEID);
                 ps.setString(1, id.getId()); // Set the badge ID parameter
                
                //determine if it returned results
                boolean hasResults = ps.execute();
                if(hasResults){
                    rs = ps.getResultSet();
                    if (rs.next()) {
                    // get values to be stored in employee
                    // *** Can be adjusted for simplicity ***
                    int empid = rs.getInt("id");
                    String firstname = rs.getString("firstname");
                    String middlename = rs.getString("middlename");
                    String lastname = rs.getString("lastname");
                    LocalDateTime active = rs.getTimestamp("active").toLocalDateTime();
                    String badgeID = rs.getString("badgeid");
                    int deptID = rs.getInt("departmentid");
                    int shiftID = rs.getInt("shiftid");
                    int empTypeID = rs.getInt("employeetypeid");
                    // use the IDs to get the corresponding objects
                    Badge badge = daoFactory.getBadgeDAO().find(badgeID);
                    Department department = daoFactory.getDepartmentDAO().find(deptID);
                    Shift shift = daoFactory.getShiftDAO().find(shiftID);
                    EmployeeType employeeType = EmployeeType.values()[empTypeID];
                    employee = new Employee(empid, firstname, middlename, lastname, active, badge, department, shift, employeeType);
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
        return employee;
    }
}