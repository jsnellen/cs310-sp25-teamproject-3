
package edu.jsu.mcis.cs310.tas_sp25;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


/**
 * @author Jordan
 */

public class Absenteeism {
    // class members
    private final Employee employee;
    private final LocalDate payPeriodStart;
    private final BigDecimal percentAbsent;
    
    // LocalDate format and formatter
    String dateFormat = "MM-ee-yyyy";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
    
    // constructor
    public Absenteeism(Employee emp, LocalDate pps, BigDecimal pa){
        employee = emp;
        payPeriodStart = pps;
        percentAbsent = pa;
    }
    
    // getters
    public Employee getEmployee(){
        return employee;
    }
    public LocalDate getPayPeriodStart(){
        return payPeriodStart;
    }
    public BigDecimal getPercentAbsent(){
        return percentAbsent;
    }
    
    // toString     
    // test output should look like this -> #28DC3FB8 (Pay Period Starting 09-02-2018): 2.50%
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("#").append(employee.getBadge().getId());
        s.append(" (Pay Period Starting ").append(payPeriodStart.format(formatter)).append("): ");
        s.append(percentAbsent).append("%");
        return "";
    }
}
