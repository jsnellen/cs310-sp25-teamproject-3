package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;

/**
 * <p>Data Access Object (DAO) for retrieving {@link Department} records from the TAS database.</p>
 * <p>Provides methods for querying department details by ID and constructing {@link Department} objects.</p>
 * 
 * @author Group
 */
public class DepartmentDAO {

    /**
     * Reference to the DAOFactory for managing connections.
     */
    private final DAOFactory daoFactory;

    /**
     * SQL query to find a department by ID.
     */
    private static final String QUERY_FIND = "SELECT * FROM department WHERE id = ?";

    /**
     * Constructs a DepartmentDAO using the specified DAOFactory.
     *
     * @param daoFactory the factory used to obtain database connections
     */
    DepartmentDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Finds a department record in the database using its ID.
     *
     * @param id the department ID
     * @return the {@link Department} object if found, or {@code null} if not found
     * @throws DAOException if a database error occurs
     */
    public Department find(int id) {
        Department deptmnt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

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
