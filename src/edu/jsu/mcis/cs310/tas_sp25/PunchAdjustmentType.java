package edu.jsu.mcis.cs310.tas_sp25;

/**
 * <p>Enumerates the possible adjustment types applied to a punch timestamp.</p>
 * <p>Used during punch rounding and shift-based adjustment logic.</p>
 * 
 * @author Group
 */
public enum PunchAdjustmentType {

    /**
     * No adjustment was applied.
     */
    NONE("None"),

    /**
     * Adjusted to match the shift start time.
     */
    SHIFT_START("Shift Start"),

    /**
     * Adjusted to match the shift stop time.
     */
    SHIFT_STOP("Shift Stop"),

    /**
     * Adjusted due to being late beyond the grace period (dock penalty applied).
     */
    SHIFT_DOCK("Shift Dock"),

    /**
     * Adjusted to the beginning of the lunch period.
     */
    LUNCH_START("Lunch Start"),

    /**
     * Adjusted to the end of the lunch period.
     */
    LUNCH_STOP("Lunch Stop"),

    /**
     * Adjusted based on a time interval rounding rule.
     */
    INTERVAL_ROUND("Interval Round");

    /**
     * The description of the adjustment type.
     */
    private final String description;

    /**
     * Constructs a PunchAdjustmentType with a string description.
     *
     * @param d the description for this adjustment type
     */
    private PunchAdjustmentType(String d) {
        description = d;
    }

    /**
     * Returns the string description of the adjustment type.
     *
     * @return the adjustment description
     */
    @Override
    public String toString() {
        return description;
    }

}
