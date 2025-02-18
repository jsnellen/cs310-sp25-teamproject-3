package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDateTime;

public class Employee {
    private int id;
    private String firstname;
    private String middlename;
    private String lastname;
    private LocalDateTime active;
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
    public Badge getBadge() { return badge; }
    public Department getDepartment() { return department; }
    public Shift getShift() { return shift; }
    public EmployeeType getEmployeeType() { return employeeType; }

    @Override
    public String toString() {
        return String.format("ID: %d, Name: %s %s %s, Badge ID: %s, Type: %s, Department: %s, Active Date: %s",
                id, firstname, middlename, lastname, badge.getId(), employeeType.toString(), department.getDescription(), active.toString());
    }
}

