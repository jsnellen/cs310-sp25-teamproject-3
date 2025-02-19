
package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;

public class DepartmentDAO {
    
    private final DAOFactory daoFactory;
    private static final String QUERY_FIND = "SELECT * FROM department WHERE id = ?";
    
    // constructor for DepartmentDAO
    DepartmentDAO(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    // Method for finding a specific department
    public Department find(int id) {
        Department deptmnt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // create connection to Database and set up prepared statement
            Connection conn = daoFactory.getConnection();
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND);
                ps.setInt(1, id);
                
                // Execute the query and determine if it returned results
                boolean hasresults = ps.execute();
                if (hasresults) {
                    // get result set and build a Department object to hold the data stored in the retrieved record
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        int numID = rs.getInt("id");
                        String description = rs.getString("description");
                        int terminalID = rs.getInt("terminalid");
                        deptmnt = new Department(numID, description, terminalID);
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
        return deptmnt;
    }
    
}
