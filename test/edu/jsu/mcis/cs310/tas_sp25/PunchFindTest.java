package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import org.junit.*;
import static org.junit.Assert.*;

public class PunchFindTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }

    @Test
    public void testFindPunches1() {

        PunchDAO punchDAO = daoFactory.getPunchDAO();

        /* Retrieve Punches from Database */
        
        Punch p1 = punchDAO.find(3433);
        Punch p2 = punchDAO.find(3325);
        Punch p3 = punchDAO.find(1963);

        /* Compare to Expected Values */
        
        assertEquals("#D2C39273 CLOCK IN: WED 09/05/2018 07:00:07", p1.toString());
        assertEquals("#DFD9BB5C CLOCK IN: TUE 09/04/2018 08:00:00", p2.toString());
        assertEquals("#99F0C0FA CLOCK IN: SAT 08/18/2018 06:00:00", p3.toString());

    }

    @Test
    public void testFindPunches2() {

        PunchDAO punchDAO = daoFactory.getPunchDAO();

        /* Retrieve Punches from Database */

        Punch p4 = punchDAO.find(5702);
        Punch p5 = punchDAO.find(4976);
        Punch p6 = punchDAO.find(2193);

        /* Compare to Expected Values */

        assertEquals("#0FFA272B CLOCK OUT: MON 09/24/2018 17:30:04", p4.toString());
        assertEquals("#FCE87D9F CLOCK OUT: TUE 09/18/2018 17:34:00", p5.toString());
        assertEquals("#FCE87D9F CLOCK OUT: MON 08/20/2018 17:30:00", p6.toString());

    }
    
    @Test
    public void testFindPunches3() {

        PunchDAO punchDAO = daoFactory.getPunchDAO();

        /* Retrieve Punches from Database */

        Punch p7 = punchDAO.find(954);
        Punch p8 = punchDAO.find(258);
        Punch p9 = punchDAO.find(717);

        /* Compare to Expected Values */

        assertEquals("#618072EA TIME OUT: FRI 08/10/2018 00:12:35", p7.toString());
        assertEquals("#0886BF12 TIME OUT: THU 08/02/2018 06:06:38", p8.toString());
        assertEquals("#67637925 TIME OUT: TUE 08/07/2018 23:12:34", p9.toString());

    }
    
    // Tests added by Jordan
    @Test
    public void testFindPunches4() {
        PunchDAO punchDAO = daoFactory.getPunchDAO();

        /* Retrieve Punches from Database */
        Punch p10 = punchDAO.find(231);
        Punch p11 = punchDAO.find(638);
        Punch p12 = punchDAO.find(924);

        /* Compare to Expected Values */
        assertEquals("#0FFA272B CLOCK OUT: WED 08/01/2018 15:33:55", p10.toString());
        assertEquals("#229324A4 CLOCK IN: TUE 08/07/2018 06:59:04", p11.toString());
        assertEquals("#BEAFDB2F CLOCK OUT: THU 08/09/2018 15:32:36", p12.toString());
    }

    @Test
    public void testFindPunches5() {
        PunchDAO punchDAO = daoFactory.getPunchDAO();

        /* Retrieve Punches from Database */
        Punch p13 = punchDAO.find(1152);
        Punch p14 = punchDAO.find(1083);
        Punch p15 = punchDAO.find(1000);
   
        /* Compare to Expected Values */
        assertEquals("#DD6E2C0C CLOCK OUT: SAT 08/11/2018 11:03:50", p13.toString());
        assertEquals("#2A7F5D99 CLOCK IN: SAT 08/11/2018 05:49:34", p14.toString());
        assertEquals("#229324A4 CLOCK IN: FRI 08/10/2018 06:59:46", p15.toString());
    }
    //Test added by Fallon
    @Test
    public void testFindPunches6() {
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        
        Punch p16 = punchDAO.find(235);
        Punch p17 = punchDAO.find(534);
        Punch p18 = punchDAO.find(715);
        
        assertEquals("#76118CDC CLOCK OUT: WED 08/01/2018 15:34:49 ", p16.toString());
        assertEquals("#4382D92D CLOCK IN: MON 08/06/2018 07:00:05 ", p17.toString());
        assertEquals("#398B1563 TIME OUT: TUES 08/07/2018 23:12:34 ", p18.toString());
    }
}
