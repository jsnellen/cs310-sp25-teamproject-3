package edu.jsu.mcis.cs310.tas_sp25;

/**
 * <p>Represents a department in the TAS system.</p>
 * <p>Each department has an ID, a name/description, and a terminal ID associated with it.</p>
 * 
 * @author Group
 */
public class Department {

    /**
     * The unique identifier for the department.
     */
    private int id;

    /**
     * The name or description of the department.
     */
    private String description;

    /**
     * The terminal ID associated with the department.
     */
    private int terminalId;

    /**
     * Constructs a Department with the specified ID, description, and terminal ID.
     *
     * @param id          the department ID
     * @param description the department's name or description
     * @param terminalId  the terminal ID linked to this department
     */
    public Department(int id, String description, int terminalId) {
        this.id = id;
        this.description = description;
        this.terminalId = terminalId;
    }

    /**
     * Gets the department ID.
     *
     * @return the department ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the description of the department.
     *
     * @return the department description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the terminal ID assigned to the department.
     *
     * @return the terminal ID
     */
    public int getTerminalId() {
        return terminalId;
    }

    /**
     * Returns a string representation of the department.
     * Example: "#4 (Grinding), Terminal ID: 104"
     *
     * @return formatted department string
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(this.getId()).append(" (").append(this.getDescription());
        s.append("), Terminal ID: ").append(this.getTerminalId());
        return s.toString();
    }
}
