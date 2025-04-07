
package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


/**
 * <p>Represents a record of absenteeism for an employee during a specific pay period.</p>
 * <p>Stores the employee, pay period start date, and percentage of time absent.</p>
 * 
 * @author Jordan
 */

public class Absenteeism {
    /**
     * The employee associated with this absenteeism record.
     */
    private final Employee employee;

    /**
     * The start date of the pay period for this absenteeism record.
     */
    private final LocalDate payPeriodStart;

    /**
     * The percentage of time the employee was absent during the pay period.
     */
    private final BigDecimal percentAbsent;

    /**
     * A string representing the desired date format for output (MM-dd-yyyy).
     */
    String dateFormat = "MM-dd-yyyy";

    /**
     * Formatter used to format the pay period start date.
     */
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

    /**
     * Constructs an absenteeism record with employee, pay period, and absence percentage.
     *
     * @param emp the employee associated with the record
     * @param pps the start date of the pay period
     * @param pa  the percentage of time absent
     */
    public Absenteeism(Employee emp, LocalDate pps, BigDecimal pa){
        employee = emp;
        payPeriodStart = pps;
        percentAbsent = pa;
    }
    
     /**
     * Gets the employee associated with the absenteeism record.
     *
     * @return the employee
     */
    public Employee getEmployee(){
        return employee;
    }

    /**
     * Gets the start date of the pay period for the record.
     *
     * @return the pay period start date
     */
    public LocalDate getPayPeriodStart(){
        return payPeriodStart;
    }

    /**
     * Gets the percentage of time the employee was absent.
     *
     * @return the absence percentage
     */
    public BigDecimal getPercentAbsent(){
        return percentAbsent;
    }

    /**
     * Returns a string representation of the absenteeism record.
     *
     * @return formatted string like "#28DC3FB8 (Pay Period Starting 09-02-2018): 2.50%"
     */
    
    // toString     
    // test output should look like this -> #28DC3FB8 (Pay Period Starting 09-02-2018): 2.50%
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("#").append(employee.getBadge().getId());
        s.append(" (Pay Period Starting ").append(payPeriodStart.format(formatter)).append("): ");
        s.append(percentAbsent).append("%");
        return s.toString();
    }
}
