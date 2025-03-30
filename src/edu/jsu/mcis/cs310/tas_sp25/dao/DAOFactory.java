package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.sql.*;

/**
 * <p>Factory class responsible for managing the creation of DAO instances and maintaining a shared database connection.</p>
 * <p>Uses a prefix to retrieve JDBC configuration settings from a properties file.</p>
 * 
 * <p>Provides access to DAO objects including BadgeDAO, PunchDAO, EmployeeDAO, etc.</p>
 * 
 * @author Group
 */
public final class DAOFactory {

    /**
     * Property key for database URL.
     */
    private static final String PROPERTY_URL = "url";

    /**
     * Property key for database username.
     */
    private static final String PROPERTY_USERNAME = "username";

    /**
     * Property key for database password.
     */
    private static final String PROPERTY_PASSWORD = "password";

    /**
     * Database connection credentials.
     */
    private final String url, username, password;

    /**
     * Shared database connection used by all DAO instances.
     */
    private Connection conn = null;

    /**
     * Constructs a DAOFactory using a configuration prefix.
     * Attempts to establish a connection using credentials found in the DAOProperties.
     *
     * @param prefix the prefix used to load database configuration properties
     * @throws DAOException if a database connection cannot be established
     */
    public DAOFactory(String prefix) {
        DAOProperties properties = new DAOProperties(prefix);
        this.url = properties.getProperty(PROPERTY_URL);
        this.username = properties.getProperty(PROPERTY_USERNAME);
        this.password = properties.getProperty(PROPERTY_PASSWORD);

        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        }
    }

    /**
     * Returns the active database connection.
     *
     * @return the current Connection object
     */
    Connection getConnection() {
        return conn;
    }

    /**
     * Returns an instance of the {@link BadgeDAO}.
     *
     * @return BadgeDAO object
     */
    public BadgeDAO getBadgeDAO() {
        return new BadgeDAO(this);
    }

    /**
     * Returns an instance of the {@link PunchDAO}.
     *
     * @return PunchDAO object
     */
    public PunchDAO getPunchDAO() {
        return new PunchDAO(this);
    }

    /**
     * Returns an instance of the {@link DepartmentDAO}.
     *
     * @return DepartmentDAO object
     */
    public DepartmentDAO getDepartmentDAO() {
        return new DepartmentDAO(this);
    }

    /**
     * Returns an instance of the {@link EmployeeDAO}.
     *
     * @return EmployeeDAO object
     */
    public EmployeeDAO getEmployeeDAO() {
        return new EmployeeDAO(this);
    }

    /**
     * Returns an instance of the {@link ShiftDAO}.
     *
     * @return ShiftDAO object
     */
    public ShiftDAO getShiftDAO() {
        return new ShiftDAO(this);
    }

    /**
     * Returns an instance of the {@link AbsenteeismDAO}.
     *
     * @return AbsenteeismDAO object
     */
    public AbsenteeismDAO getAbsenteeismDAO() {
        return new AbsenteeismDAO(this);
    }
}
