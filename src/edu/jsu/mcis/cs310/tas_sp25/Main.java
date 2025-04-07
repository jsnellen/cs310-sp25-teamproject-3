package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import static org.junit.Assert.assertEquals;

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
        Badge b1 = badgeDAO.find("C4F37EFF");
        
        // output should be "Test Badge: #C4F37EFF (Welch, Travis C)"
        System.err.println("Test Badge: " + b1.toString());

        AbsenteeismDAO absenteeismDAO = daoFactory.getAbsenteeismDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
		
        /* Get Punch/Employee Objects */
        
        Punch p = punchDAO.find(3634);
        Employee e = employeeDAO.find(p.getBadge());
        Shift s = e.getShift();
        Badge b = e.getBadge();
        
        /* Get Pay Period Punch List */
        
        LocalDate ts = p.getOriginaltimestamp().toLocalDate();
        
        System.err.println("Timestamp: " + ts.toString());
        
        LocalDate begin = ts.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = begin.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        ArrayList<Punch> punchlist = punchDAO.list(b, begin, end);
        
        /* Adjust Punch List */
        
        for (Punch punch : punchlist) {
            punch.adjust(s);
        }
        
        /* Compute Pay Period Total Absenteeism */
        
        BigDecimal percentage = DAOUtility.calculateAbsenteeism(punchlist, s);
        
        System.out.println("Percentage: " + percentage);
        
        /* Insert Absenteeism Into Database */
        
        Absenteeism a1 = new Absenteeism(e, ts, percentage);
        
        System.out.println(a1.toString());
        
        absenteeismDAO.create(a1);
        
        /* Retrieve Absenteeism From Database */
        
        Absenteeism a2 = absenteeismDAO.find(e, ts);
        
        /* Compare to Expected Value */
        
        assertEquals("#28DC3FB8 (Pay Period Starting 09-02-2018): 2.50%", a2.toString());
        
    


    }

}
