package edu.jsu.mcis.cs310.tas_sp25;

/**
 * <p>Represents an employee badge used for clocking in and out of the TAS system.</p>
 * <p>Each badge has a unique alphanumeric ID and a description, usually the employee's name.</p>
 * 
 * @author Group
 */
public class Badge {

    /**
     * The unique ID of the badge.
     */
    private final String id;

    /**
     * A short description of the badge, typically the employee's name.
     */
    private final String description;

    /**
     * Constructs a new Badge with the given ID and description.
     *
     * @param id          the badge ID
     * @param description the textual description of the badge
     */
    public Badge(String id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Gets the badge ID.
     *
     * @return the ID of the badge
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the badge description.
     *
     * @return the description of the badge
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns a formatted string representation of the badge.
     * Example: "#08D01475 (Littell, Amie D)"
     *
     * @return formatted badge string
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(id).append(' ');
        s.append('(').append(description).append(')');
        return s.toString();
    }

}
