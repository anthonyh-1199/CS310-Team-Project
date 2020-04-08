package edu.jsu.mcis.tas_sp20;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.*;
import static org.junit.Assert.*;

public class Feature9 {

    private TASDatabase db;

    @Before
    public void setup() {
        db = new TASDatabase();
    }

    @Test
    public void testEmployee() {

        /* Create Employee Objects */

        Employee e1 = db.getEmployee("9D527CFB");
        Employee e2 = db.getEmployee("29C3C7D4");
        Employee e3 = db.getEmployee("2A5620A0");
        Employee e4 = db.getEmployee("12565C60");
        Employee e5 = db.getEmployee("6C0D1549");
        Employee e6 = db.getEmployee("8709982E");

        /* Test Employee Objects */

        assertEquals("Rodriquez, Jarvis B (#9D527CFB); Type 0 (Temporary Employee); Department 8; Shift 1; Active: 09/22/2015; Inactive: none", e1.toString());
        assertEquals("Gomez, Rose M (#29C3C7D4); Type 0 (Temporary Employee); Department 1; Shift 1; Active: 11/02/2015; Inactive: none", e2.toString());
        assertEquals("Eaton, Curtis M (#2A5620A0); Type 0 (Temporary Employee); Department 2; Shift 1; Active: 10/16/2015; Inactive: none", e3.toString());
        assertEquals("Chapman, Joshua E (#12565C60); Type 0 (Temporary Employee); Department 5; Shift 1; Active: 09/11/2015; Inactive: none", e4.toString());
        assertEquals("Franklin, Ronald W (#6C0D1549); Type 0 (Temporary Employee); Department 1; Shift 1; Active: 09/22/2015; Inactive: none", e5.toString());
        assertEquals("Dent, Judy E (#8709982E); Type 1 (Full-Time Employee); Department 1; Shift 1; Active: 06/27/2016; Inactive: none", e6.toString());

    }

    @Test
    public void testDepartment() {

        /* Create Department Objects */

        Department d1 = db.getDepartment(1);
        Department d2 = db.getDepartment(2);
        Department d3 = db.getDepartment(3);
        Department d4 = db.getDepartment(6);
        Department d5 = db.getDepartment(10);

        /* Test Department Objects */

        assertEquals("Department #1 (Assembly); Terminal #103", d1.toString());
        assertEquals("Department #2 (Cleaning); Terminal #107", d2.toString());
        assertEquals("Department #3 (Warehouse); Terminal #106", d3.toString());
        assertEquals("Department #6 (Office); Terminal #102", d4.toString());
        assertEquals("Department #10 (Maintenance); Terminal #104", d5.toString());

    }

    @Test
    public void testPayPeriodMethods() {

        SimpleDateFormat sdf = new SimpleDateFormat("EEE MM/dd/yyyy HH:mm:ss");

        /* Create Timestamp Objects */

        long v1 = 1534804715125L; // Original Timestamp: Mon 08/20/2018 17:38:35
        long v2 = 1533155511994L; // Original Timestamp: Wed 08/01/2018 15:31:51
        long v3 = 1534800716213L; // Original Timestamp: Mon 08/20/2018 16:31:56
        long v4 = 1536752852125L; // Original Timestamp: Wed 09/12/2018 06:47:32
        long v5 = 1536957789683L; // Original Timestamp: Fri 09/14/2018 15:43:09

        Date d1 = new Date( TASLogic.getStartOfPayPeriod(v1) );
        Date d2 = new Date( TASLogic.getEndOfPayPeriod(v1) );
        Date d3 = new Date( TASLogic.getStartOfPayPeriod(v2) );
        Date d4 = new Date( TASLogic.getEndOfPayPeriod(v2) );
        Date d5 = new Date( TASLogic.getStartOfPayPeriod(v3) );
        Date d6 = new Date( TASLogic.getEndOfPayPeriod(v3) );
        Date d7 = new Date( TASLogic.getStartOfPayPeriod(v4) );
        Date d8 = new Date( TASLogic.getEndOfPayPeriod(v4) );
        Date d9 = new Date( TASLogic.getStartOfPayPeriod(v5) );
        Date d10 = new Date( TASLogic.getEndOfPayPeriod(v5) );

        assertEquals("Sun 08/19/2018 00:00:00", sdf.format(d1));
        assertEquals("Sat 08/25/2018 23:59:59", sdf.format(d2));
        assertEquals("Sun 07/29/2018 00:00:00", sdf.format(d3));
        assertEquals("Sat 08/04/2018 23:59:59", sdf.format(d4));
        assertEquals("Sun 08/19/2018 00:00:00", sdf.format(d5));
        assertEquals("Sat 08/25/2018 23:59:59", sdf.format(d6));
        assertEquals("Sun 09/09/2018 00:00:00", sdf.format(d7));
        assertEquals("Sat 09/15/2018 23:59:59", sdf.format(d8));
        assertEquals("Sun 09/09/2018 00:00:00", sdf.format(d9));
        assertEquals("Sat 09/15/2018 23:59:59", sdf.format(d10));

    }

}