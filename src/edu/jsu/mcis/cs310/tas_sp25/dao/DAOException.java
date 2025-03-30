package edu.jsu.mcis.cs310.tas_sp25.dao;

/**
 * <p>Represents an unchecked exception that occurs within the DAO layer of the TAS system.</p>
 * <p>This exception is typically thrown when a database access error occurs.</p>
 * 
 * @author Group
 */
public class DAOException extends RuntimeException {

    /**
     * Constructs a DAOException with the specified detail message.
     *
     * @param message the detail message to include with the exception
     */
    public DAOException(String message) {
        super(message);
    }

}
