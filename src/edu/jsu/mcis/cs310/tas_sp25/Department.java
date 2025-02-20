package edu.jsu.mcis.cs310.tas_sp25.model;

public class Department {
    
    private int id;
    private String description;
    private int terminalId;

    // Constructor
    public Department(int id, String description, int terminalId) {
        this.id = id;
        this.description = description;
        this.terminalId = terminalId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getTerminalId() {
        return terminalId;
    }

    // toString() Method for Debugging
    @Override
    public String toString() {
        return "Department {" +
                "ID: " + id +
                ", Description: \"" + description + "\"" +  // Corrected Quotes
                ", Terminal ID: " + terminalId +
                '}';
    }
}
