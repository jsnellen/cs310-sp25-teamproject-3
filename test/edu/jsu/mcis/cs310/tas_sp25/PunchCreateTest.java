package edu.jsu.mcis.cs310.tas_sp25;

import edu.jsu.mcis.cs310.tas_sp25.dao.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.*;
import static org.junit.Assert.*;

public class PunchCreateTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }

    @Test
    public void testCreatePunch1() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PunchDAO punchDAO = daoFactory.getPunchDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Punch Object */
        
        Punch p1 = new Punch(103, badgeDAO.find("021890C0"), EventType.CLOCK_IN);

        /* Create Timestamp Objects */
        
        LocalDateTime ots, rts;

        /* Get Punch Properties */
        
        String badgeid = p1.getBadge().getId();
        ots = p1.getOriginaltimestamp();
        int terminalid = p1.getTerminalid();
        EventType punchtype = p1.getPunchtype();

        /* Insert Punch Into Database */
        
        int punchid = punchDAO.create(p1);

        /* Retrieve New Punch */
        
        Punch p2 = punchDAO.find(punchid);

        /* Compare Punches */
        
        assertEquals(badgeid, p2.getBadge().getId());

        rts = p2.getOriginaltimestamp();

        assertEquals(terminalid, p2.getTerminalid());
        assertEquals(punchtype, p2.getPunchtype());
        assertEquals(ots.format(dtf), rts.format(dtf));

    }
    
    // Tests added by Jordan
    // This is an dditional test to see if we can add a punch
    @Test
    public void testCreatePunch2() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        
        /* Create New Punch Object */
        Punch p1 = new Punch(102, badgeDAO.find("ADD650A8"), EventType.CLOCK_IN);

        /* Create Timestamp Objects */
        LocalDateTime ots, rts;

        /* Get Punch Properties */
        String badgeid = p1.getBadge().getId();
        ots = p1.getOriginaltimestamp();
        int terminalid = p1.getTerminalid();
        EventType punchtype = p1.getPunchtype();

        /* Insert Punch Into Database */
        int punchid = punchDAO.create(p1);

        /* Retrieve New Punch */
        Punch p2 = punchDAO.find(punchid);

        /* Compare Punches */
        assertEquals(badgeid, p2.getBadge().getId());
        rts = p2.getOriginaltimestamp();
        assertEquals(terminalid, p2.getTerminalid());
        assertEquals(punchtype, p2.getPunchtype());
        assertEquals(ots.format(dtf), rts.format(dtf));
    }
    
    // The next two tests are to see if administrators can add punches directly
    @Test
    public void testCreatePunch3() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        
        /* Create New Punch Object */
        Punch p1 = new Punch(0, badgeDAO.find("2A7F5D99"), EventType.CLOCK_IN);

        /* Create Timestamp Objects */
        LocalDateTime ots, rts;

        /* Get Punch Properties */
        String badgeid = p1.getBadge().getId();
        ots = p1.getOriginaltimestamp();
        int terminalid = p1.getTerminalid();
        EventType punchtype = p1.getPunchtype();

        /* Insert Punch Into Database */
        int punchid = punchDAO.create(p1);

        /* Retrieve New Punch */
        Punch p2 = punchDAO.find(punchid);

        /* Compare Punches */
        assertEquals(badgeid, p2.getBadge().getId());
        rts = p2.getOriginaltimestamp();
        assertEquals(terminalid, p2.getTerminalid());
        assertEquals(punchtype, p2.getPunchtype());
        assertEquals(ots.format(dtf), rts.format(dtf));
    }
    @Test
    public void testCreatePunch4() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        
        /* Create New Punch Object */
        Punch p1 = new Punch(0, badgeDAO.find("E77BFAEA"), EventType.CLOCK_IN);

        /* Create Timestamp Objects */
        LocalDateTime ots, rts;

        /* Get Punch Properties */
        String badgeid = p1.getBadge().getId();
        ots = p1.getOriginaltimestamp();
        int terminalid = p1.getTerminalid();
        EventType punchtype = p1.getPunchtype();

        /* Insert Punch Into Database */
        int punchid = punchDAO.create(p1);

        /* Retrieve New Punch */
        Punch p2 = punchDAO.find(punchid);

        /* Compare Punches */
        assertEquals(badgeid, p2.getBadge().getId());
        rts = p2.getOriginaltimestamp();
        assertEquals(terminalid, p2.getTerminalid());
        assertEquals(punchtype, p2.getPunchtype());
        assertEquals(ots.format(dtf), rts.format(dtf));
    }
    
    // This next two tests are to see if the punchID correctly returns 0 if the punch is unable to be authorized
    @Test
    public void testCreatePunch5() {
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Punch Object */ // The correct terminalid should be 107
        Punch p1 = new Punch(103, badgeDAO.find("9186E711"), EventType.CLOCK_IN);

        /* Insert Punch Into Database */
        int punchid = punchDAO.create(p1);

        /* Retrieve New Punch */
        Punch p2 = punchDAO.find(punchid);

        /* See if p2 exists or not */
        assertEquals(null, p2);
        
        /* See if punchDAO.create() correctly returned 0*/
        assertEquals(0, punchid);
    }
    @Test
    public void testCreatePunch6() {
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Create New Punch Object */ // The correct terminalid should be 104
        Punch p1 = new Punch(67, badgeDAO.find("1C920A23"), EventType.CLOCK_IN);

        /* Insert Punch Into Database */
        int punchid = punchDAO.create(p1);

        /* Retrieve New Punch */
        Punch p2 = punchDAO.find(punchid);

        /* See if p2 exists or not */
        assertEquals(null, p2);
        
        /* See if punchDAO.create() correctly returned 0*/
        assertEquals(0, punchid);
    }
}
