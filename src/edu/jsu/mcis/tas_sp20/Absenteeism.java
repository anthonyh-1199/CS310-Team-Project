package edu.jsu.mcis.tas_sp20;

import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.DecimalFormat;

public class Absenteeism {
    String id;
    long tsLong;
    double percentage;
    GregorianCalendar gc = new GregorianCalendar();

    Absenteeism (String id, long tsLong, double percentage) {
        this.gc.setTimeInMillis(tsLong);
        this.gc.add(Calendar.DAY_OF_WEEK, -(gc.get(Calendar.DAY_OF_WEEK) - 1));
        this.gc.set(Calendar.HOUR, 0);
        this.gc.set(Calendar.MINUTE, 0);
        this.gc.set(Calendar.SECOND, 0);
        this.gc.set(Calendar.MILLISECOND, 0);
        
        this.tsLong = gc.getTimeInMillis();
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
                "): " + pString + "%"; //TODO: CHANGE "TIME" TO DATE
        return s;
    }
}
