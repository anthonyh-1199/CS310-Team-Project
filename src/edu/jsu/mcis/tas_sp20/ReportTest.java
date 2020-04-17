package edu.jsu.mcis.tas_sp20;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;
import org.json.simple.*;

public class ReportTest {

    public static void main(String[] args) {
        //ASSEMBLY: id = 1, punch = 3810
        //GRINDING: id = 4, punch = 2550, 2693?
        //PRESS: id = 7, punch = 1129
        //SHIPPING: id = 8, punch = 4262

        if (true) {
            TASDatabase db = new TASDatabase();
            Punch punch = db.getPunch(1129);

            createTimeSheetSummary("0FFA272B", 1536901200000L);
            createHoursSummary(7, punch.getOriginaltimestamp());
        }
//        createBadgeSummary();
    }
    
    public static void createBadgeSummary() {
        
        try {
            
            // Create Database Connection
        
            TASDatabase db = new TASDatabase();
            
            // Get Report Data; Convert to JasperReports Data Source
            
            ArrayList<HashMap> reportData = db.getBadgeSummaryData();
            JRDataSource jasperDataSource = new JRMapArrayDataSource(reportData.toArray());
            
            // Print Report Data as JSON String (for diagnostic purposes only)
            
            System.out.println(JSONValue.toJSONString(reportData));
            
            // Initialize Report Parameter Collection

            HashMap<String, Object> parameters = new HashMap<>();
            
            parameters.put("subtitle", "(All Active Employees)");
            
            // Get I/O Streams (report file for input; new PDF file for output)
            
            InputStream input = ClassLoader.class.getResourceAsStream("/resources/BadgeSummary.jasper");
            FileOutputStream output = new FileOutputStream(new File("report.pdf"));
            
            // Run Report; Get PDF Data
            
            byte[] pdf = JasperRunManager.runReportToPdf(input, parameters, jasperDataSource);
            
            // Write PDF Data to File
            
            if (pdf.length > 0) {
                
                System.out.println("PDF data received!  Writing to file ...");
                output.write(pdf);
                
            }
            
            // Close I/O Streams
            
            input.close();
            output.close();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
    }

    public static void createHoursSummary(int id, long payPeriod) {

        try {
            TASDatabase db = new TASDatabase();

            ArrayList<HashMap> reportData = db.getDepartmentSummaryData(id, payPeriod);
            JRDataSource jasperDataSource = new JRMapArrayDataSource(reportData.toArray());

            System.out.println(JSONValue.toJSONString(reportData)); //for testing

            HashMap<String, Object> parameters = new HashMap<>();

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            java.util.Date d = new Date(TASLogic.getEndOfPayPeriod(payPeriod));
            parameters.put("subtitle", "Pay Period Ending: " + df.format(d).toUpperCase());

            InputStream in = ClassLoader.class.getResourceAsStream("/resources/departmentReport.jasper");
            FileOutputStream out = new FileOutputStream(new File("hourSummary.pdf"));

            byte[] pdf = JasperRunManager.runReportToPdf(in, parameters, jasperDataSource);

            if (pdf.length > 0) {
                System.out.println("Data successfully retrieved! Writing...");
                out.write(pdf);
            }

            in.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public static void createTimeSheetSummary(String badgeid, long day) {

        try {
            TASDatabase db = new TASDatabase();

            ArrayList<HashMap> reportData = db.getTimeSheetData(badgeid, day);
            JRDataSource jasperDataSource = new JRMapArrayDataSource(reportData.toArray());
            
            System.out.println(JSONValue.toJSONString(reportData)); //for testing

            HashMap<String, Object> parameters = new HashMap<>();
            
            Employee e = db.getEmployee(badgeid);
            parameters.put("subtitle", e.getLastName() + ", " + e.getFirstName() + " " + e.getMiddleName() + " (" + badgeid + ") Time Sheet: ");

            InputStream in = ClassLoader.class.getResourceAsStream("/resources/TimeSheetReport.jasper");
            FileOutputStream out = new FileOutputStream(new File("timesheetSummary.pdf"));

            byte[] pdf = JasperRunManager.runReportToPdf(in, parameters, jasperDataSource);

            if (pdf.length > 0) {
                System.out.println("Data successfully retrieved! Writing...");
                out.write(pdf);
            }

            in.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}