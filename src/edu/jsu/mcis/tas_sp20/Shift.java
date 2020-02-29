package edu.jsu.mcis.tas_sp20;

import java.time.LocalTime;

public class Shift {
    // TODO: Determine data types
    String description;
    int ID, interval, gracePeriod, dock, lunchDeduct;
    LocalTime start, stop, lunchStart, lunchStop;

    Shift(int ID, int dock, int gracePeriod, int interval) {
        description = null;
        ID = 0;

        dock = 0;
        gracePeriod = 0;
        interval = 0;
    }

    Shift (int ID, int dock, int gracePeriod, int interval, int lunchDeduct, LocalTime start, LocalTime stop, LocalTime
            lunchStart, LocalTime lunchStop) {
        this.ID = ID;
        this.dock = dock;
        this.gracePeriod = gracePeriod;
        this.interval = interval;
        this.lunchDeduct = lunchDeduct;
        this.start = start;
        this.stop = stop;
        this.lunchStart = lunchStart;
        this.lunchStop = lunchStop;
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
    public String getDescription (String description) {return description;}

    public int getID (int ID) {return ID;}
    public int getDock (int dock) {return dock;}
    public int getInterval (int interval) {return interval;}
    public int getGracePeriod (int gracePeriod) {return gracePeriod;}
    public int getLunchDeduct (int lunchDeduct) {return lunchDeduct;}

    public LocalTime getStart (LocalTime start) {return start;}
    public LocalTime getStop (LocalTime stop) {return stop;}
    public LocalTime getLunchStart (LocalTime lunchStart) {return lunchStart;}
    public LocalTime getLunchStop (LocalTime lunchStop) {return lunchStop;}
}

