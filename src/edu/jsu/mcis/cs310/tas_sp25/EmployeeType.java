package edu.jsu.mcis.cs310.tas_sp25;

/**
 * <p>Enumerates the employment types available to employees in the TAS system.</p>
 * <p>Used to distinguish between full-time and temporary/part-time employees.</p>
 * 
 * @author Group
 */
public enum EmployeeType {

    /**
     * Represents a temporary or part-time employee.
     */
    PART_TIME("Temporary / Part-Time"),

    /**
     * Represents a full-time employee.
     */
    FULL_TIME("Full-Time");

    /**
     * The human-readable description of the employee type.
     */
    private final String description;

    /**
     * Constructs an EmployeeType with a description.
     *
     * @param d the description of the employee type
     */
    private EmployeeType(String d) {
        description = d;
    }

    /**
     * Returns the description of the employee type.
     *
     * @return the employee type description
     */
    @Override
    public String toString() {
        return description;
    }

}
