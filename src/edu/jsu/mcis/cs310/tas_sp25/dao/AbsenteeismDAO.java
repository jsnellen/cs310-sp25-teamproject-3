/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.Absenteeism;
import edu.jsu.mcis.cs310.tas_sp25.Employee;
import edu.jsu.mcis.cs310.tas_sp25.Punch;
import edu.jsu.mcis.cs310.tas_sp25.Shift;
import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;


/**
 *
 * @author magan
 */

/**
 * Data Access Object for Absenteeism
 */
public class AbsenteeismDAO {

    private final DAOFactory daoFactory;
    private LocalDate payperiod;
    private ArrayList<Punch> punchlist;

    // Constructor to initialize database connection
    public AbsenteeismDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;

    }
    private static final String QUERY_FIND_ABSENTEEISM = 
        "SELECT minutesmissed FROM absenteeism WHERE employeeid = ? AND date = ?";
    
    //Query for create()
    private static final String QUERY_CREATE_ABSENTEEISM =
        "INSERT INTO absenteeism (employeeid, payperiod, percentage) " +
        "VALUES (?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE percentage = VALUES(percentage)";
    

 public Absenteeism find(Employee employee, LocalDate payperiod) {
    Absenteeism absenteeism = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        Connection conn = daoFactory.getConnection();
        if (conn != null && conn.isValid(0)) {
            ps = conn.prepareStatement(QUERY_FIND_ABSENTEEISM);
            ps.setInt(1, employee.getId());
            ps.setDate(2, java.sql.Date.valueOf(payperiod));
            rs = ps.executeQuery();

            if (rs.next()) {
                // Ensure punchlist is properly initialized
                if (punchlist == null) {
                    punchlist = new ArrayList<>();
                }

                // Get the shift
                Shift shift = daoFactory.getShiftDAO().find(employee.getBadge());
                if (shift == null) {
                    throw new DAOException("Shift not found for Employee ID: " + employee.getId());
                }

                // Debugging print statements (remove after testing)
                System.out.println("Employee ID: " + employee.getId());
                System.out.println("Badge: " + employee.getBadge());
                System.out.println("Punchlist size before absenteeism calc: " + punchlist.size());
                System.out.println("Shift: " + shift);

                // Ensure punchlist contains data before passing to DAOUtility
                if (punchlist.isEmpty()) {
                    throw new DAOException("Punch list is empty for Employee ID: " + employee.getId());
                }

                // Calculate absenteeism
                BigDecimal absenteeismPercentage = DAOUtility.calculateAbsenteeism(punchlist, shift);
                absenteeism = new Absenteeism(employee, payperiod, absenteeismPercentage);
            }
        }
    } catch (SQLException e) {
        throw new DAOException("Error finding absenteeism for Employee: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            throw new DAOException("Error closing resources: " + e.getMessage());
        }
    }

    return absenteeism;
}   
    public void create(Absenteeism absenteeism) {
        try (Connection conn = daoFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(QUERY_CREATE_ABSENTEEISM)) {

            Employee employee = absenteeism.getEmployee();
            LocalDate payPeriodStart = absenteeism.getPayPeriodStart();
            BigDecimal absenteeismPercentage = absenteeism.getPercentAbsent();

            // Execute query
            ps.setInt(1, employee.getId());
            ps.setDate(2, java.sql.Date.valueOf(payPeriodStart));
            ps.setBigDecimal(3, absenteeismPercentage);
            ps.executeUpdate();

        }catch (SQLException e) {
            throw new DAOException("Error creating absenteeism record for Employee ID: " + absenteeism.getEmployee().getId() + " - " + e.getMessage());
        }

    }
}
