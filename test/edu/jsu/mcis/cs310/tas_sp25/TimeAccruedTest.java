package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.*;
import static org.junit.Assert.*;
import java.math.BigDecimal;

public class TimeAccruedTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }

    @Test
    public void testMinutesAccruedShift1Weekday() {
        
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();

        /* Get Punch/Badge/Shift Objects */

        Punch p = punchDAO.find(3634);
        Badge b = p.getBadge();
        Shift s = shiftDAO.find(b);
        
        /* Get/Adjust Punch List */

        ArrayList<Punch> dailypunchlist = punchDAO.list(b, p.getOriginaltimestamp().toLocalDate());

        for (Punch punch : dailypunchlist) {
            punch.adjust(s);
        }

        /* Compute Pay Period Total */
        
        int m = DAOUtility.calculateTotalMinutes(dailypunchlist, s);

        /* Compare to Expected Value */
        
        assertEquals(480, m);

    }

    @Test
    public void testMinutesAccruedShift1WeekdayWithTimeout() {
        
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();

        /* Get Punch/Badge/Shift Objects */

        Punch p = punchDAO.find(436);
        Badge b = p.getBadge();
        Shift s = shiftDAO.find(b);
        
        /* Get/Adjust Punch List */

        ArrayList<Punch> dailypunchlist = punchDAO.list(b, p.getOriginaltimestamp().toLocalDate());

        for (Punch punch : dailypunchlist) {
            punch.adjust(s);
        }

        /* Compute Pay Period Total */
        
        int m = DAOUtility.calculateTotalMinutes(dailypunchlist, s);

        /* Compare to Expected Value */
        
        assertEquals(0, m);

    }

    @Test
    public void testMinutesAccruedShift1Weekend() {
        
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();

        /* Get Punch/Badge/Shift Objects */

        Punch p = punchDAO.find(1087);
        Badge b = p.getBadge();
        Shift s = shiftDAO.find(b);
        
        /* Get/Adjust Punch List */

        ArrayList<Punch> dailypunchlist = punchDAO.list(b, p.getOriginaltimestamp().toLocalDate());

        for (Punch punch : dailypunchlist) {
            punch.adjust(s);
        }

        /* Compute Pay Period Total */
        
        int m = DAOUtility.calculateTotalMinutes(dailypunchlist, s);

        /* Compare to Expected Value */
        
        assertEquals(360, m);

    }

    @Test
    public void testMinutesAccruedShift2Weekday() {
        
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();

        /* Get Punch/Badge/Shift Objects */

        Punch p = punchDAO.find(4943);
        Badge b = p.getBadge();
        Shift s = shiftDAO.find(b);
        
        /* Get/Adjust Punch List */

        ArrayList<Punch> dailypunchlist = punchDAO.list(b, p.getOriginaltimestamp().toLocalDate());

        for (Punch punch : dailypunchlist) {
            punch.adjust(s);
        }

        /* Compute Pay Period Total */
        
        int m = DAOUtility.calculateTotalMinutes(dailypunchlist, s);

        /* Compare to Expected Value */
        
        assertEquals(540, m);

    }
    
    @Test
    public void testCalculateTotalMinutes_TotalShift() {
        // Initialize DAO objects
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();

        // Define test parameters
        int employeeId = 6; // Employee ID for Harry King
        LocalDate date = LocalDate.of(2018, 9, 21); // Date of the punches

        // Retrieve the employee's badge ID
        Employee employee = employeeDAO.find(employeeId);
        Badge badge = employee.getBadge();

        // Retrieve punches for the employee on the specified date
        ArrayList<Punch> dailypunchlist = punchDAO.list(badge, date);

        // Print punches for debugging
        System.out.println("Punches for " + badge.getId() + " on " + date + ":");
           for (Punch punch : dailypunchlist) {
            System.out.println(punch);
        }

        // Retrieve the shift for the employee
        Shift shift = shiftDAO.find(badge);

        // Print shift for debugging
        System.out.println("Shift for " + badge.getId() + ": " + shift);

        // Adjust the punches according to the shift rules
            for (Punch punch : dailypunchlist) {
             punch.adjust(shift);
        }

        // Calculate total minutes worked
        int totalMinutes = DAOUtility.calculateTotalMinutes(dailypunchlist, shift);

        // Print total minutes for debugging
        System.out.println("Total minutes calculated: " + totalMinutes);

        // Compare to Expected Value (total shift duration)
        int expectedTotalMinutes = 510; // 8.5 hours (510 minutes)
        assertEquals(expectedTotalMinutes, totalMinutes);
    }

  // Later add test for total time with lunch deducted
    
    @Test
    public void testCalculateAbsenteeism() {
    // Initialize DAO objects
    PunchDAO punchDAO = daoFactory.getPunchDAO();
    ShiftDAO shiftDAO = daoFactory.getShiftDAO();
    EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();

    // Define test parameters
    int employeeId = 6; // Employee ID for Harry King
    LocalDate startDate = LocalDate.of(2018, 9, 1); // Start of the pay period
    LocalDate endDate = LocalDate.of(2018, 9, 30); // End of the pay period

    // Retrieve the employee's badge ID
    Employee employee = employeeDAO.find(employeeId);
    Badge badge = employee.getBadge();

    // Retrieve the shift for the employee
    Shift shift = shiftDAO.find(badge);

    // Retrieve all punches for the employee within the pay period
    ArrayList<Punch> punchlist = new ArrayList<>();
    LocalDate currentDate = startDate;
    while (!currentDate.isAfter(endDate)) {
        punchlist.addAll(punchDAO.list(badge, currentDate));
        currentDate = currentDate.plusDays(1);
    }

    // Adjust all punches according to the shift rules
    for (Punch punch : punchlist) {
        punch.adjust(shift);
    }

    // Calculate absenteeism
    BigDecimal absenteeism = DAOUtility.calculateAbsenteeism(punchlist, shift);

    // Print absenteeism for debugging
    System.out.println("Absenteeism for " + badge.getId() + ": " + absenteeism + "%");

    // Compare to Expected Value
    BigDecimal expectedAbsenteeism = new BigDecimal("-374.2700");
    assertEquals(expectedAbsenteeism, absenteeism);
    }
}
