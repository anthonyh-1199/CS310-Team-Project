package edu.jsu.mcis.tas_sp20;

import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MINUTES;

public class Shift {
    // TODO: Determine data types
    String description;
    DailySchedule schedule;

    Shift (int ID, String description, LocalTime start, LocalTime stop, int interval, int gracePeriod, int dock,
    LocalTime lunchStart, LocalTime lunchStop, int lunchDeduct) {
        schedule = new DailySchedule(ID, start, stop, interval, gracePeriod, dock, lunchStart, lunchStop, lunchDeduct);
        this.description = description;
    }

    private int shiftDuration (LocalTime start, LocalTime stop) {
        LocalTime l1 = LocalTime.parse(start.toString());
        LocalTime l2 = LocalTime.parse(stop.toString());
        long time = l1.until(l2, MINUTES);
        return (int) time;
    }

    // Setter methods
    public void setDescription (String description) {this.description = description;}

    public void setID (int ID) {schedule.setID(ID);}
    public void setDock (int dock) {schedule.setDock(dock);}
    public void setInterval (int interval) {schedule.setInterval(interval);}
    public void setGracePeriod (int gracePeriod) {schedule.setGracePeriod(gracePeriod);}
    public void setLunchDeduct (int lunchDeduct) {schedule.setLunchDeduct(lunchDeduct);}

    public void setStart (LocalTime start) {schedule.setStart(start);}
    public void setStop (LocalTime stop) {schedule.setStop(stop);}
    public void setLunchStart (LocalTime lunchStart) {schedule.setLunchStart(lunchStart);}
    public void setLunchStop (LocalTime lunchStop) {schedule.setLunchStop(lunchStop);}


    // Getter methods
    public String getDescription () {return description;}

    public int getID () {return schedule.getID();}
    public int getDock () {return schedule.getDock();}
    public int getInterval () { return schedule.getInterval(); }
    public int getGracePeriod () {return schedule.getGracePeriod();}
    public int getLunchDeduct () {return schedule.getLunchDeduct();}
    public int getLunchDuration() {return schedule.getLunchDuration();}

    public LocalTime getStart () {return schedule.getStart();}
    public LocalTime getStop () {return schedule.getStop();}
    public LocalTime getLunchStart () {return schedule.getLunchStart();}
    public LocalTime getLunchStop () {return schedule.getLunchStop();}




    // toString method
    @Override
    public String toString(){
        String s = description + ": " + schedule.toString();
        return s;
    }
}

