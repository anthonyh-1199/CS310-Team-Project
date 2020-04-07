package edu.jsu.mcis.tas_sp20;

import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

public class Absenteeism {
    private String id;
    private long tsLong;
    private double percentage;
    private Timestamp timestamp;
    private GregorianCalendar gc = new GregorianCalendar();

    public Absenteeism (String id, long tsLong, double percentage) {
        this.timestamp = TASLogic.getStartOfPayperiod(tsLong);
        this.tsLong = timestamp.getTime();
        this.gc.setTimeInMillis(this.tsLong);
        
        this.id = id;
        this.percentage = percentage;
    }

    // Setter methods
    public void setBadgeId (String id) {this.id = id;}
    public void setTimestamp (long tsLong) {this.tsLong = tsLong;}
    public void setPercentage (double percentage) {this.percentage = percentage;}

    // Getter methods
    public String getBadgeId () {return id;}
    public long getTimestampLong () {return tsLong;}
    public double getPercentage () {return percentage;}
    public Timestamp getTimestamp() {return new Timestamp(tsLong);}

    // toString method
    @Override
    public String toString() {
        String pString = (new DecimalFormat("0.00")).format(percentage);
        String s = "#" + id + " " + "(Pay Period Starting " +
                new SimpleDateFormat("MM-dd-yyyy").format(this.gc.getTime()) +
                "): " + pString + "%";
        return s;
    }
}
