package edu.jsu.mcis.tas_sp20;

import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public class DailySchedule {
    private LocalTime start, stop, lunchStart, lunchStop;
    private int ID, interval, gracePeriod, dock, lunchDeduct, lunchDuration;

    public DailySchedule(int ID, LocalTime start, LocalTime stop, int interval, int gracePeriod, int dock, LocalTime
    lunchStart, LocalTime lunchStop, int lunchDeduct)
    {
        this.ID = ID;
        this.start = start;
        this.stop = stop;
        this.interval = interval;
        this.gracePeriod = gracePeriod;
        this.dock = dock;
        this.lunchStart = lunchStart;
        this.lunchStop = lunchStop;
        this.lunchDeduct = lunchDeduct;
        this.lunchDuration = shiftDuration(lunchStart,lunchStop);
    }

    private int shiftDuration (LocalTime start, LocalTime stop) {
        LocalTime l1 = LocalTime.parse(start.toString());
        LocalTime l2 = LocalTime.parse(stop.toString());
        long time = l1.until(l2, MINUTES);
        return (int) time;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public void setStop(LocalTime stop) {
        this.stop = stop;
    }

    public void setLunchStart(LocalTime lunchStart) {
        this.lunchStart = lunchStart;
    }

    public void setLunchStop(LocalTime lunchStop) {
        this.lunchStop = lunchStop;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setGracePeriod(int gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public void setDock(int dock) {
        this.dock = dock;
    }

    public void setLunchDeduct(int lunchDeduct) {
        this.lunchDeduct = lunchDeduct;
    }

    public void setLunchDuration(int lunchDuration) {
        this.lunchDuration = lunchDuration;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getStop() {
        return stop;
    }

    public LocalTime getLunchStart() {
        return lunchStart;
    }

    public LocalTime getLunchStop() {
        return lunchStop;
    }

    public int getID() {
        return ID;
    }

    public int getInterval() {
        return interval;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public int getDock() {
        return dock;
    }

    public int getLunchDeduct() {
        return lunchDeduct;
    }

    public int getLunchDuration() {
        return lunchDuration;
    }

    @Override
    public String toString() {
        return start + " - " + stop + " (" + shiftDuration(start, stop) + " minutes); Lunch: " + lunchStart
        + " - " + lunchStop + " (" + lunchDuration + " minutes)";
    }
}
