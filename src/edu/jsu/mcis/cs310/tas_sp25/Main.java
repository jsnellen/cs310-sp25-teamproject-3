package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

/* Group 3 Members:
    Jordan Underwood
    Magan Richey**
    Fallon Shell
*/

/**
 * <p>A simple test class used to verify TAS database connectivity and functionality.</p>
 * <p>This class demonstrates basic DAO usage and object retrieval using a known badge ID.</p>
 * 
 * @author Group
 */
public class Main {

    /**
     * Entry point for the TAS test driver.
     * Retrieves a badge object from the database and prints a formatted result to the console.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        
        // test database connectivity; get DAO
        DAOFactory daoFactory = new DAOFactory("tas.jdbc");
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        
        // find badge
        Badge b = badgeDAO.find("C4F37EFF");
        
        // output should be "Test Badge: #C4F37EFF (Welch, Travis C)"
        System.err.println("Test Badge: " + b.toString());

    }

}
