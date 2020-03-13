package edu.jsu.mcis.tas_sp20;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

public class Absenteeism {
    Badge id;
    long timestamp;
    double percentage;

    Absenteeism (Badge id, long timestamp, double percentage) {
        this.timestamp = timestamp;
        this.id = id;

        this.percentage = percentage;
    }

    // Setter methods
    public void setID (Badge id) {this.id = id;}
    public void setTimestamp (Long timestamp) {this.timestamp = timestamp;}
    public void setPercentage (double percentage) {this.percentage = percentage;}

    // Getter methods
    public Badge getID (Badge id) {return id;}
    public Long getTimestamp (long timestamp) {return timestamp;}
    public double getPercentage (double percentage) {return percentage;}

    // toString method
    @Override
    public String toString() {
        String s = id + "(Pay Period Starting " + timestamp + "): " + percentage;
        return s;
    }
}
