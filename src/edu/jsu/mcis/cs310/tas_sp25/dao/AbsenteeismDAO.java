/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.Absenteeism;
import edu.jsu.mcis.cs310.tas_sp25.Employee;
import edu.jsu.mcis.cs310.tas_sp25.Shift;
import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.math.RoundingMode;


/**
 *
 * @author magan
 */

/**
 * Data Access Object for Absenteeism
 */
public class AbsenteeismDAO {
    
    private final DAOFactory daoFactory;

    // Constructor to initialize database connection
    public AbsenteeismDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    private static final String QUERY_FIND_ABSENTEEISM = 
        "SELECT minutesmissed FROM absenteeism WHERE employeeid = ? AND date = ?";

    public Absenteeism find(Employee employee, LocalDate date) {
        Absenteeism absenteeism = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();
            if (conn != null && conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_ABSENTEEISM);
                ps.setInt(1, employee.getId());
                ps.setDate(2, java.sql.Date.valueOf(date));
                rs = ps.executeQuery();

                if (rs.next()) {
                    double minutesMissed = rs.getDouble("minutesmissed");

                    Shift shift = daoFactory.getShiftDAO().find(employee.getBadge());
                    if (shift == null) {
                        throw new DAOException("Shift not found for Employee ID: " + employee.getId());
                    }

                    // Call DAOUtility to get absenteeism percentage
                    BigDecimal absenteeismPercentage = DAOUtility.calculateAbsenteeism(minutesMissed, shift.getShiftDuration());
                    absenteeism = new Absenteeism(employee, date, absenteeismPercentage);
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
}
