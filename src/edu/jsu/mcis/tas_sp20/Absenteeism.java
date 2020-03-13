package edu.jsu.mcis.tas_sp20;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

public class Absenteeism {
    String id;
    Long payperiod;
    double percentage;

    Absenteeism (Long payperiod, String id) {
        this.payperiod = payperiod;
        this.id = id;

        percentage = 0;
    }

    // Setter methods
    public void setID (String id) {this.id = id;}
    public void setPayperiod (Long payperiod) {this.payperiod = payperiod;}
    public void setPercentage (double percentage) {this.percentage = percentage;}

    // Getter methods
    public String getID (String id) {return id;}
    public Long getPayperiod (Long payperiod) {return payperiod;}
    public double getPercentage (double percentage) {return percentage;}

    // toString method
    @Override
    public String toString() {
        String s = id + "(Pay Period Starting " + payperiod + "): " + percentage;
        return s;
    }
}
