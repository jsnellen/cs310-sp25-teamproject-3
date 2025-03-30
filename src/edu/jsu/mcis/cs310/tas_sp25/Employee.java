package edu.jsu.mcis.cs310.tas_sp25;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * <p>Represents an employee in the TAS system.</p>
 * <p>Each employee has personal info, badge, department, shift, and employment type.</p>
 * 
 * @author Group
 */
public class Employee {

    /**
     * The unique identifier for the employee.
     */
    private int id;

    /**
     * The employee's first name.
     */
    private String firstname;

    /**
     * The employee's middle name.
     */
    private String middlename;

    /**
     * The employee's last name.
     */
    private String lastname;

    /**
     * The date and time when the employee became active.
     */
    private LocalDateTime active;

    // private LocalDateTime inactive; // Will be used in future versions.

    /**
     * The badge assigned to the employee.
     */
    private Badge badge;

    /**
     * The department in which the employee works.
     */
    private Department department;

    /**
     * The shift assigned to the employee.
     */
    private Shift shift;

    /**
     * The employee's employment type (e.g., Full-Time, Part-Time).
     */
    private EmployeeType employeeType;

    /**
     * Constructs an Employee object with full initialization.
     *
     * @param id           the employee's ID
     * @param firstname    the employee's first name
     * @param middlename   the employee's middle name
     * @param lastname     the employee's last name
     * @param active       the activation date/time of the employee
     * @param badge        the employee's badge
     * @param department   the employee's department
     * @param shift        the employee's shift
     * @param employeeType the type of employment
     */
    public Employee(int id, String firstname, String middlename, String lastname, LocalDateTime active, Badge badge, Department department, Shift shift, EmployeeType employeeType) {
        this.id = id;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.active = active;
        this.badge = badge;
        this.department = department;
        this.shift = shift;
        this.employeeType = employeeType;
    }

    /**
     * Gets the employee ID.
     *
     * @return the employee's ID
     */
    public int getId() { return id; }

    /**
     * Gets the employee's first name.
     *
     * @return the first name
     */
    public String getFirstname() { return firstname; }

    /**
     * Gets the employee's middle name.
     *
     * @return the middle name
     */
    public String getMiddlename() { return middlename; }

    /**
     * Gets the employee's last name.
     *
     * @return the last name
     */
    public String getLastname() { return lastname; }

    /**
     * Gets the activation date and time of the employee.
     *
     * @return the LocalDateTime of activation
     */
    public LocalDateTime getActive() { return active; }

    // public LocalDateTime getInactive() { return inactive; } // Reserved for future use

    /**
     * Gets the employee's assigned badge.
     *
     * @return the Badge object
     */
    public Badge getBadge() { return badge; }

    /**
     * Gets the department of the employee.
     *
     * @return the Department object
     */
    public Department getDepartment() { return department; }

    /**
     * Gets the shift assigned to the employee.
     *
     * @return the Shift object
     */
    public Shift getShift() { return shift; }

    /**
     * Gets the employee's type of employment.
     *
     * @return the EmployeeType
     */
    public EmployeeType getEmployeeType() { return employeeType; }

    /**
     * Returns a formatted string summarizing the employee's details.
     *
     * @return a formatted string with name, badge, department, etc.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(badge.getId()).append(' ');
        s.append("Original Event: ").append(active.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

        return String.format("ID #%d: "
                + lastname + ", " + firstname + " " + middlename 
                + " (#%s), "
                + "Type: %s, "
                + "Department: %s, "
                + "Active: %s",
                id, badge.getId(), employeeType.toString(), department.getDescription(), active.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
    }

    /**
     * Returns the original event string using the badge and active date.
     *
     * @return formatted original event string
     */
    public String printOriginal() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(badge.getId()).append(' ');
        s.append("Original Event: ").append(active.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        return s.toString();
    }

    /**
     * Returns the adjusted event string using the badge and active date.
     *
     * @return formatted adjusted event string
     */
    public String printAdjusted() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(badge.getId()).append(' ');
        s.append("Adjusted Event: ").append(active.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        return s.toString();
    }
}
