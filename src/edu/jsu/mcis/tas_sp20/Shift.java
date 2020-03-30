package edu.jsu.mcis.tas_sp20;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;

import static java.time.temporal.ChronoUnit.MINUTES;

public class Shift {
    private String description;
    private DailySchedule defaultSchedule;
    private HashMap<Integer, DailySchedule> scheduleMap;

    public Shift (int ID, String description, LocalTime start, LocalTime stop, int interval, int gracePeriod, int dock,
    LocalTime lunchStart, LocalTime lunchStop, int lunchDeduct) {
        this.defaultSchedule = new DailySchedule(ID, start, stop, interval, gracePeriod, dock, lunchStart, lunchStop, lunchDeduct);
        this.description = description;
    }

    public Shift(String description, DailySchedule schedule) {
        this.description = description;
        this.defaultSchedule = schedule;
        this.scheduleMap = new HashMap<Integer, DailySchedule>();

        scheduleMap.put(Calendar.MONDAY, schedule);
        scheduleMap.put(Calendar.TUESDAY, schedule);
        scheduleMap.put(Calendar.WEDNESDAY, schedule);
        scheduleMap.put(Calendar.THURSDAY, schedule);
        scheduleMap.put(Calendar.FRIDAY, schedule);
    }


    private int shiftDuration (LocalTime start, LocalTime stop) {   //TODO: remove?
        LocalTime l1 = LocalTime.parse(start.toString());
        LocalTime l2 = LocalTime.parse(stop.toString());
        long time = l1.until(l2, MINUTES);
        return (int) time;
    }

    // Setter methods
    public void setDescription (String description) {this.description = description;}

    public void setID(int ID) {
        defaultSchedule.setID(ID);
    }

    public void setID(int ID, int day) {
        scheduleMap.get(day).setID(ID);
    }

    public void setDock(int dock) {
        defaultSchedule.setDock(dock);
    }

    public void setDock(int dock, int day) {
        scheduleMap.get(day).setDock(dock);
    }

    public void setInterval(int interval) {
        defaultSchedule.setInterval(interval);
    }

    public void setInterval(int interval, int day) {
        scheduleMap.get(day).setInterval(interval);
    }

    public void setGracePeriod(int gracePeriod) {
        defaultSchedule.setGracePeriod(gracePeriod);
    }

    public void setGracePeriod(int gracePeriod, int day) {
        scheduleMap.get(day).setGracePeriod(gracePeriod);
    }

    public void setLunchDeduct(int lunchDeduct) {
        defaultSchedule.setLunchDeduct(lunchDeduct);
    }

    public void setLunchDeduct(int lunchDeduct, int day) {
        scheduleMap.get(day).setLunchDeduct(lunchDeduct);
    }

    public void setStart(LocalTime start) {
        defaultSchedule.setStart(start);
    }

    public void setStart(LocalTime start, int day) {
        scheduleMap.get(day).setStart(start);
    }

    public void setStop(LocalTime stop) {
        defaultSchedule.setStop(stop);
    }

    public void setStop(LocalTime stop, int day) {
        scheduleMap.get(day).setStop(stop);
    }

    public void setLunchStart(LocalTime lunchStart) {
        defaultSchedule.setLunchStart(lunchStart);
    }

    public void setLunchStart(LocalTime lunchStart, int day) {
        scheduleMap.get(day).setLunchStart(lunchStart);
    }

    public void setLunchStop(LocalTime lunchStop) {
        defaultSchedule.setLunchStop(lunchStop);
    }

    public void setLunchStop(LocalTime lunchStop, int day) {
        scheduleMap.get(day).setLunchStop(lunchStop);
    }

    public void setDefaultSchedule(DailySchedule schedule) {
        this.defaultSchedule = schedule;
    }

    public void setSchedule(DailySchedule schedule, int day) {
        scheduleMap.put(day, schedule);
    }

    // Getter methods
    public String getDescription () {return description;}

    public int getID() {
        return defaultSchedule.getID();
    }

    public int getID(int day) {
        return scheduleMap.get(day).getID();
    }

    public int getDock() {
        return defaultSchedule.getDock();
    }

    public int getDock(int day) {
        return scheduleMap.get(day).getDock();
    }

    public int getInterval() {
        return defaultSchedule.getInterval();
    }

    public int getInterval(int day) {
        return scheduleMap.get(day).getInterval();
    }

    public int getGracePeriod() {
        return defaultSchedule.getGracePeriod();
    }

    public int getGracePeriod(int day) {
        return scheduleMap.get(day).getGracePeriod();
    }

    public int getLunchDeduct() {
        return defaultSchedule.getLunchDeduct();
    }

    public int getLunchDeduct(int day) {
        return scheduleMap.get(day).getLunchDeduct();
    }

    public int getLunchDuration() {
        return defaultSchedule.getLunchDuration();
    }

    public int getLunchDuration(int day) {
        return scheduleMap.get(day).getLunchDuration();
    }

    public LocalTime getStart() {
        return defaultSchedule.getStart();
    }

    public LocalTime getStart(int day) {
        return scheduleMap.get(day).getStart();
    }

    public LocalTime getStop() {
        return defaultSchedule.getStop();
    }

    public LocalTime getStop(int day) {
        return scheduleMap.get(day).getStop();
    }

    public LocalTime getLunchStart() {
        return defaultSchedule.getLunchStart();
    }

    public LocalTime getLunchStart(int day) {
        return scheduleMap.get(day).getLunchStart();
    }

    public LocalTime getLunchStop() {
        return defaultSchedule.getLunchStop();
    }

    public LocalTime getLunchStop(int day) {
        return scheduleMap.get(day).getLunchStop();
    }

    public DailySchedule getDefaultSchedule() {
        return defaultSchedule;
    }

    public DailySchedule getSchedule(int day) {
        return scheduleMap.get(day);
    }


    @Override
    public String toString(){
        String s = description + ": " + defaultSchedule.toString();
        return s;
    }
}

