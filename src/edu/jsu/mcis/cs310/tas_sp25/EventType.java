package edu.jsu.mcis.cs310.tas_sp25;

/**
 * <p>Represents the type of event that can occur when an employee uses the time clock.</p>
 * <p>Each event type corresponds to a specific kind of punch, such as clocking in or out.</p>
 * 
 * @author Group
 */
public enum EventType {

    /**
     * Represents an employee clocking out.
     */
    CLOCK_OUT("CLOCK OUT"),

    /**
     * Represents an employee clocking in.
     */
    CLOCK_IN("CLOCK IN"),

    /**
     * Represents a timeout event, typically an automatic cutoff (e.g., end of day).
     */
    TIME_OUT("TIME OUT");

    /**
     * The human-readable description of the event type.
     */
    private final String description;

    /**
     * Constructs an EventType with a description.
     *
     * @param d the description of the event
     */
    private EventType(String d) {
        description = d;
    }

    /**
     * Returns the string description of the event type.
     *
     * @return the event type as a string
     */
    @Override
    public String toString() {
        return description;
    }
}
