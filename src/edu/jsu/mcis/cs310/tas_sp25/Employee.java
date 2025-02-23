package edu.jsu.mcis.cs310.tas_sp25;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Employee {
    private int id;
    private String firstname;
    private String middlename;
    private String lastname;
    private LocalDateTime active;
    //private LocalDateTime inactive; This will be used in the future.//
    private Badge badge;
    private Department department;
    private Shift shift;
    private EmployeeType employeeType;

    // Constructor
    public Employee(int id, String firstname, String middlename, String lastname, LocalDateTime active, Badge badge, Department department, Shift shift, EmployeeType employeeType) {
        this.id = id;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.active = active;
        //this.inactive = inactive;
        this.badge = badge;
        this.department = department;
        this.shift = shift;
        this.employeeType = employeeType;
    }

    // Getters
    public int getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getMiddlename() { return middlename; }
    public String getLastname() { return lastname; }
    public LocalDateTime getActive() { return active; }
    //public LocalDateTime getInactive() {return inactive;}
    public Badge getBadge() { return badge; }
    public Department getDepartment() { return department; }
    public Shift getShift() { return shift; }
    public EmployeeType getEmployeeType() { return employeeType; }

    @Override
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(badge.getId()).append(' ');
        s.append("Original Event: ").append(active.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        //return s.toString();
        //return printOriginal();
        ///Jordan commented this out because it seems unnecessary
        return String.format("ID #%d: "
                + lastname + ", " + firstname + " " + middlename 
                + " (#%s), "
                + "Type: %s, "
                + "Department: %s, "
                + "Active: %s",
                //+ "Active Date: %s, "
                //+ "Inactive Date: %s",
                id, badge.getId(), employeeType.toString(), department.getDescription(), active.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));//, inactive.toString());
        
    }
                
    public String printOriginal() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(badge.getId()).append(' ');
        s.append("Original Event: ").append(active.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        return s.toString();
    }

    public String printAdjusted() {
        StringBuilder s = new StringBuilder();
        s.append('#').append(badge.getId()).append(' ');
        s.append("Adjusted Event: ").append(active.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
        return s.toString();
    }
}






