package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.*;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Map;
import java.util.stream.Collectors;

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

        // Added by Jordan
        // Print punches for debugging
        System.out.println("Punches for " + badge.getId() + " on " + date + ":");
           for (Punch punch : dailypunchlist) {
            System.out.println(punch.getAdjustedTimestamp());
        }
        // END OF CODE ADDED \\
        
        // Calculate total minutes worked
        int totalMinutes = DAOUtility.calculateTotalMinutes(dailypunchlist, shift);

        // Print total minutes for debugging
        System.out.println("Total minutes calculated: " + totalMinutes);

        // Compare to Expected Value (total shift duration)
        int expectedTotalMinutes = 480; // 8 hours (480 minutes)
        assertEquals(expectedTotalMinutes, totalMinutes);
    }

    @Test
    public void testCalculateAbsenteeismByWeek() {
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();
        EmployeeDAO employeeDAO = daoFactory.getEmployeeDAO();

        int employeeId = 6;
        LocalDate startDate = LocalDate.of(2018, 9, 1);
        LocalDate endDate = LocalDate.of(2018, 9, 30);

        Employee employee = employeeDAO.find(employeeId);
        Badge badge = employee.getBadge();
        Shift shift = shiftDAO.find(badge);

        // Get all punches for the month and adjust them
        ArrayList<Punch> allPunches = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            allPunches.addAll(punchDAO.list(badge, currentDate));
            currentDate = currentDate.plusDays(1);
        }
        allPunches.forEach(p -> p.adjust(shift));

        // Expected values by week
        Map<Integer, BigDecimal> expectedWeekPercentages = Map.of(
            1, new BigDecimal("-18.75"),
            2, new BigDecimal("-35.63"),
            3, new BigDecimal("-32.03"),
            4, new BigDecimal("-30.00"),
            5, new BigDecimal("0.00")
        );

        Map<Integer, BigDecimal> expectedWeekendPercentages = Map.of(
            1, new BigDecimal("0.00"),
            2, new BigDecimal("0.00"),
            3, new BigDecimal("0.00"),
            4, new BigDecimal("0.00")
        );

        // Calculate by week
        int weekNumber = 1;
        LocalDate weekStart = startDate;
        while (weekStart.isBefore(endDate)) {
            // Create final copies for use in lambda
            final LocalDate weekStartFinal = weekStart;
            final LocalDate weekEndFinal = weekStart.plusDays(6).isAfter(endDate) ? 
             endDate : weekStart.plusDays(6);

            System.out.printf("\n=== Week %d (%s to %s) ===\n", 
             weekNumber, weekStartFinal, weekEndFinal);
        
            // Filter punches for this week using final variables
            ArrayList<Punch> weekPunches = allPunches.stream()
                .filter(p -> {
                    LocalDate punchDate = p.getAdjustedTimestamp().toLocalDate();
                    return !punchDate.isBefore(weekStartFinal) && 
                       !punchDate.isAfter(weekEndFinal);
                })
                .collect(Collectors.toCollection(ArrayList::new));

        BigDecimal weekAbsenteeism = DAOUtility.calculateAbsenteeism(weekPunches, shift);
        System.out.println("Calculated Weekly Absenteeism: " + weekAbsenteeism + "%");
        
        BigDecimal expectedWeek = expectedWeekPercentages.get(weekNumber);
        if (expectedWeek != null) {
            System.out.println("Expected Weekly Absenteeism: " + expectedWeek + "%");
            assertEquals("Week " + weekNumber + " absenteeism mismatch", 
                        expectedWeek, weekAbsenteeism);
        }

        // Weekend calculation
        ArrayList<Punch> weekendPunches = weekPunches.stream()
            .filter(p -> p.getAdjustedTimestamp().getDayOfWeek() == DayOfWeek.SATURDAY)
            .collect(Collectors.toCollection(ArrayList::new));
        
        if (!weekendPunches.isEmpty()) {
            BigDecimal weekendAbsenteeism = DAOUtility.calculateAbsenteeism(weekendPunches, shift);
            System.out.println("Calculated Weekend Absenteeism: " + weekendAbsenteeism + "%");
            
            BigDecimal expectedWeekend = expectedWeekendPercentages.get(weekNumber);
            if (expectedWeekend != null) {
                System.out.println("Expected Weekend Absenteeism: " + expectedWeekend + "%");
                assertEquals("Week " + weekNumber + " weekend absenteeism mismatch", 
                            expectedWeekend, weekendAbsenteeism);
            }
        }

        weekStart = weekStart.plusWeeks(1);
        weekNumber++;
    }

    // Full month calculation
    BigDecimal monthlyAbsenteeism = DAOUtility.calculateAbsenteeism(allPunches, shift);
    System.out.println("\nMonthly Absenteeism: " + monthlyAbsenteeism + "%");
    assertEquals(new BigDecimal("-33.68"), monthlyAbsenteeism);
}
}