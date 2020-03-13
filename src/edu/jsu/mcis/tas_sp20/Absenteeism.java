package edu.jsu.mcis.tas_sp20;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

public class Absenteeism {
    String id;
    long tsLong;
    double percentage;

    Absenteeism (String id, long tsLong, double percentage) {
        this.tsLong = tsLong;
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
        String s = id + "(Pay Period Starting " + tsLong + "): " + percentage;
        return s;
    }
}
