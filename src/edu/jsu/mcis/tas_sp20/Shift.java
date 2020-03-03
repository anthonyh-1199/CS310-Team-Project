package edu.jsu.mcis.tas_sp20;

import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MINUTES;

public class Shift {
    // TODO: Determine data types
    String description;
    int ID, interval, gracePeriod, dock, lunchDeduct, lunchDuration;
    LocalTime start, stop, lunchStart, lunchStop;

    Shift(int ID, int dock, int gracePeriod, int interval) {
        ID = 0;
        description = null;

        dock = 0;
        gracePeriod = 0;
        interval = 0;
        lunchDuration = 0;
    }

    Shift (int ID, String description, LocalTime start, LocalTime stop, int interval, int gracePeriod, int dock, LocalTime
            lunchStart, LocalTime lunchStop, int lunchDeduct) {
        this.ID = ID;
        this.description = description;
        this.start = start;
        this.stop = stop;
        this.interval = interval;
        this.gracePeriod = gracePeriod;
        this.dock = dock;
        this.lunchStart = lunchStart;
        this.lunchStop = lunchStop;
        this.lunchDeduct = lunchDeduct;
    }


    // Setter methods
    public void setDescription (String description) {this.description = description;}

    public void setID (int ID) {this.ID = ID;}
    public void setDock (int dock) {this.dock = dock;}
    public void setInterval (int interval) {this.interval = interval;}
    public void setGracePeriod (int gracePeriod) {this.gracePeriod = gracePeriod;}
    public void setLunchDeduct (int lunchDeduct) {this.lunchDeduct = lunchDeduct;}

    public void setStart (LocalTime start) {this.start = start;}
    public void setStop (LocalTime stop) {this.stop = stop;}
    public void setLunchStart (LocalTime lunchStart) {this.lunchStart = lunchStart;}
    public void setLunchStop (LocalTime lunchStop) {this.lunchStop = lunchStop;}


    // Getter methods
    public String getDescription () {return description;}

    public int getID () {return ID;}
    public int getDock () {return dock;}
    public int getInterval () { return interval; }
    public int getGracePeriod () {return gracePeriod;}
    public int getLunchDeduct () {return lunchDeduct;}

    public LocalTime getStart () {return start;}
    public LocalTime getStop () {return stop;}
    public LocalTime getLunchStart () {return lunchStart;}
    public LocalTime getLunchStop () {return lunchStop;}

    public int shiftDuration (LocalTime start, LocalTime stop) {
        LocalTime l1 = LocalTime.parse(start.toString());
        LocalTime l2 = LocalTime.parse(stop.toString());
        long time = l1.until(l2, MINUTES);
        return (int) time;
    }


    // toString method
    @Override
    public String toString(){
        String s = description + ": " + start + " - " + stop + " (" + shiftDuration(start, stop) + " minutes); Lunch: " + lunchStart + " - " + lunchStop + " (" + shiftDuration(lunchStart,lunchStop) + " minutes)";
        return s;
    }
}

