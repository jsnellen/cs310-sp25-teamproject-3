package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;


public class EmployeeDAO{
//Declare class instance variables 
    private final DAOFactory daoFactory;
    private static final String QUERY_FIND = "SELECT * FROM Employee WHERE id = ?";
    private static final String QUERY_FIND_BY_BADGE = "SELECT * FROM Employee WHERE badgeid = ?";
    // Constructor
    public EmployeeDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    // Method for finding a specific employee with a numerical id
    public Employee find(int id) {
        Employee employee = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            Connection conn = daoFactory.getConnection();
            ps = conn.prepareStatement(QUERY_FIND);

            ps.setInt(1, id);
            System.out.println("Executing query: " + ps.toString()); // Add this line for logging
            
            boolean hasResults = ps.execute();
            if (hasResults){
                rs = ps.getResultSet();
                if (rs.next()) {
                    employee = extractEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
        return employee;
    }

    // Method for finding a single employee using a badge id
    public Employee find(Badge id) {
        Employee employee = null;
        
        try{
            Connection conn = daoFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(QUERY_FIND_BY_BADGE);

            ps.setString(1, id.getId());
            System.out.println("Executing query: " + ps.toString()); // Add this line for logging

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    employee = extractEmployeeFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
        return employee;
    }

    // Helper method to extract employee details from ResultSet
    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        int empid = rs.getInt("id");
        String firstname = rs.getString("firstname");
        String middlename = rs.getString("middlename");
        String lastname = rs.getString("lastname");
        LocalDateTime active = rs.getTimestamp("active").toLocalDateTime();
        String badgeID = rs.getString("badgeid");
        int deptID = rs.getInt("departmentid");
        int shiftID = rs.getInt("shiftid");
        int empTypeID = rs.getInt("employeetypeid");

        Badge badge = daoFactory.getBadgeDAO().find(badgeID);
        Department department = daoFactory.getDepartmentDAO().find(deptID);
        Shift shift = daoFactory.getShiftDAO().find(shiftID);
        EmployeeType employeeType = EmployeeType.values()[empTypeID];

        return new Employee(empid, firstname, middlename, lastname, active, badge, department, shift, employeeType);
    }
}


