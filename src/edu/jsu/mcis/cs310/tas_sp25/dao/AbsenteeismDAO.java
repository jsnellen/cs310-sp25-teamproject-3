/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.Absenteeism;
import edu.jsu.mcis.cs310.tas_sp25.Employee;
import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


/**
 *
 * @author magan
 */

/**
 * Data Access Object for Absenteeism
 */
public class AbsenteeismDAO {

    private final DAOFactory daoFactory;
    private LocalDate payPeriodStart;

    // Constructor to initialize database connection
    public AbsenteeismDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        
    }
    
    private void getPayPeriodStart(LocalDate date) {
        payPeriodStart = date.minusDays(date.getDayOfWeek().getValue() % 7);
    }

    
    private static final String QUERY_FIND_ABSENTEEISM = 
        "SELECT * FROM absenteeism WHERE employeeid = ? AND payperiod = ?";
    
    //Query for create()
    private static final String QUERY_CREATE_ABSENTEEISM =
        "INSERT INTO absenteeism (employeeid, payperiod, percentage) " +
        "VALUES (?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE percentage = VALUES(percentage)";
    

    public Absenteeism find(Employee employee, LocalDate payperiod) {
        Absenteeism absenteeism = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        getPayPeriodStart(payperiod);  // or if changed--absenteeism.getPayPeriodStart()


        try {
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_ABSENTEEISM);
                ps.setInt(1, employee.getId());
                ps.setDate(2, java.sql.Date.valueOf(payPeriodStart));
                
                boolean hasresults = ps.execute();
                if (hasresults) {
                    rs = ps.getResultSet();
                    if (rs.next()) {
                        BigDecimal percentage = rs.getBigDecimal("percentage");
                        absenteeism = new Absenteeism(employee, payPeriodStart, percentage);
                    }
                }
            }
        } catch (Exception e) {
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
        try {
            Connection conn = daoFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(QUERY_CREATE_ABSENTEEISM);

            Employee employee = absenteeism.getEmployee();
            getPayPeriodStart(absenteeism.getPayPeriodStart());
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
