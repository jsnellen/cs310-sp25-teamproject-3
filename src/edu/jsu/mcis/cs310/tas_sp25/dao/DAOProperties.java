package edu.jsu.mcis.cs310.tas_sp25.dao;

import java.io.*;
import java.util.Properties;

/**
 * <p>Loads and manages DAO configuration properties from the <code>dao.properties</code> file.</p>
 * <p>Used by the {@link DAOFactory} to establish database connections based on a prefix.</p>
 * 
 * <p>Supports multiple configurations (e.g., development, production) via property prefixes.</p>
 * 
 * @author Group
 */
public class DAOProperties {

    /**
     * The name of the properties file to load.
     */
    private static final String PROPERTIES_FILE = "dao.properties";

    /**
     * The static properties object containing all loaded values.
     */
    private static final Properties PROPERTIES = new Properties();

    /**
     * The prefix used to access a specific set of property keys.
     */
    private final String prefix;

    // Static block to load properties at class initialization
    static {
        try {
            InputStream file = DAOProperties.class.getResourceAsStream(PROPERTIES_FILE);
            PROPERTIES.load(file);
        } catch (IOException e) {
            throw new DAOException(e.getMessage());
        }
    }

    /**
     * Constructs a new DAOProperties object with a specific prefix.
     *
     * @param prefix the prefix to prepend to property keys (e.g., "tas.jdbc")
     */
    public DAOProperties(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Retrieves a property value based on the prefix and given key.
     *
     * @param key the base property key (e.g., "url", "username")
     * @return the full property value, or {@code null} if not found or empty
     */
    public String getProperty(String key) {
        String fullKey = prefix + "." + key;
        String property = PROPERTIES.getProperty(fullKey);

        if (property == null || property.trim().length() == 0) {
            property = null;
        }

        return property;
    }

}
