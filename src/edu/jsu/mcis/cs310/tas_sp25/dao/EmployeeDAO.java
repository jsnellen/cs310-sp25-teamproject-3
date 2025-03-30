package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;

/**
 * <p>Data Access Object (DAO) for retrieving and managing {@link Employee} records from the TAS database.</p>
 * <p>Supports searching for employees by ID or badge, and constructs complete Employee objects using related DAO classes.</p>
 * 
 * <p>This class handles the interaction between the application and the "employee" table.</p>
 * 
 * @author Group
 */
public class EmployeeDAO {

    /**
     * DAOFactory for managing connections and DAO dependencies.
     */
    private final DAOFactory daoFactory;

    /**
     * SQL query for finding an employee by numeric ID.
     */
    private static final String QUERY_FIND = "SELECT * FROM Employee WHERE id = ?";

    /**
     * SQL query for finding an employee by badge ID.
     */
    private static final String QUERY_FIND_BY_BADGE = "SELECT * FROM Employee WHERE badgeid = ?";

    /**
     * Constructs an EmployeeDAO with the specified DAOFactory.
     *
     * @param daoFactory the factory used to get DAO dependencies and a DB connection
     */
    public EmployeeDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Finds an employee by their numeric ID.
     *
     * @param id the numeric ID of the employee
     * @return the matching {@link Employee}, or {@code null} if not found
     * @throws DAOException if a database error occurs
     */
    public Employee find(int id) {
        Employee employee = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();
            ps = conn.prepareStatement(QUERY_FIND);

            ps.setInt(1, id);
            System.out.println("Executing query: " + ps.toString());

            boolean hasResults = ps.execute();
            if (hasResults) {
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

    /**
     * Finds an employee by their badge.
     *
     * @param id the {@link Badge} object representing the employee
     * @return the matching {@link Employee}, or {@code null} if not found
     * @throws DAOException if a database error occurs
     */
    public Employee find(Badge id) {
        Employee employee = null;

        try {
            Connection conn = daoFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(QUERY_FIND_BY_BADGE);

            ps.setString(1, id.getId());
            System.out.println("Executing query: " + ps.toString());

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

    /**
     * Helper method to extract an {@link Employee} object from a ResultSet row.
     *
     * @param rs the {@link ResultSet} containing the employee data
     * @return a fully constructed {@link Employee} object
     * @throws SQLException if a database field cannot be read
     */
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
