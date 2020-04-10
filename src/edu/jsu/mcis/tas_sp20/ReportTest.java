package edu.jsu.mcis.tas_sp20;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;
import org.json.simple.*;

public class ReportTest {
    
    public static void main(String[] args) {
        
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
    
}