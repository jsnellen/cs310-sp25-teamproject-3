package edu.jsu.mcis.cs310.tas_sp25.dao;

import edu.jsu.mcis.cs310.tas_sp25.*;
import java.sql.*;

/**
 * <p>Data Access Object (DAO) for retrieving {@link Badge} objects from the database.</p>
 * <p>Handles queries and object construction for badge records.</p>
 * 
 * @author Group
 */
public class BadgeDAO {

    /**
     * SQL query to find a badge by its ID.
     */
    private static final String QUERY_FIND = "SELECT * FROM badge WHERE id = ?";

    /**
     * The DAOFactory used to manage database connections.
     */
    private final DAOFactory daoFactory;

    /**
     * Constructs a BadgeDAO using the provided DAOFactory.
     *
     * @param daoFactory the factory to obtain database connections
     */
    BadgeDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Retrieves a {@link Badge} from the database using its ID.
     *
     * @param id the badge ID to look up
     * @return the {@link Badge} object, or {@code null} if not found
     * @throws DAOException if a database error occurs
     */
    public Badge find(String id) {

        Badge badge = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND);
                ps.setString(1, id);

                boolean hasresults = ps.execute();

                if (hasresults) {
                    rs = ps.getResultSet();
                    while (rs.next()) {
                        String description = rs.getString("description");
                        badge = new Badge(id, description);
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

        return badge;
    }

}
