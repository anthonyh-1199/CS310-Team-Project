package edu.jsu.mcis.tas_sp20;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.time.*;

class Punch {
    private int id, terminalid, punchtypeid;
    private Badge badge;
    private Long originaltimestamp, adjustedTimestamp;
    private String adjustmenttype;
    
    Punch(Badge badge, int terminalid, int punchtypeid){
        id = 0;   
        adjustmenttype = null;
        
        this.terminalid = terminalid;
        this.badge = badge;
        this.punchtypeid = punchtypeid;
        
        this.originaltimestamp = System.currentTimeMillis();
        this.adjustedTimestamp = this.originaltimestamp;
    }
    
    Punch(int terminalid, Badge badge, Long timestamp, int punchtypeid){
        id = 0;   
        adjustmenttype = null;
        
        this.terminalid = terminalid;
        this.badge = badge;
        this.punchtypeid = punchtypeid;
        
        this.originaltimestamp = timestamp;
        this.adjustedTimestamp = this.originaltimestamp;
    }
    
    public void adjust(Shift s){
        //Convert shift times to Gregorian Calenders and Longs
        GregorianCalendar originalTSCal = new GregorianCalendar();
            originalTSCal.setTimeInMillis(this.getOriginaltimestamp());
            originalTSCal.clear(GregorianCalendar.SECOND);
        Long punchTime = originalTSCal.getTimeInMillis();

        GregorianCalendar sStartCal = (GregorianCalendar) originalTSCal.clone();
        sStartCal.set(GregorianCalendar.HOUR_OF_DAY, s.getStart().getHour());
        sStartCal.set(GregorianCalendar.MINUTE, s.getStart().getMinute());
        Long sStartLong = sStartCal.getTimeInMillis();
        
        GregorianCalendar sStopCal = (GregorianCalendar) originalTSCal.clone();
        sStopCal.set(GregorianCalendar.HOUR_OF_DAY, s.getStop().getHour());
        sStopCal.set(GregorianCalendar.MINUTE, s.getStop().getMinute());
        Long sStopLong = sStopCal.getTimeInMillis();
        
        GregorianCalendar lStartCal = (GregorianCalendar) originalTSCal.clone();
        lStartCal.set(GregorianCalendar.HOUR_OF_DAY, s.getLunchStart().getHour());
        lStartCal.set(GregorianCalendar.MINUTE, s.getLunchStart().getMinute());
        Long lStartLong = lStartCal.getTimeInMillis();
        
        GregorianCalendar lStopCal = (GregorianCalendar) originalTSCal.clone();
        lStopCal.set(GregorianCalendar.HOUR_OF_DAY, s.getLunchStop().getHour());
        lStopCal.set(GregorianCalendar.MINUTE, s.getLunchStop().getMinute());
        Long lStopLong = lStopCal.getTimeInMillis();
        
        //Convert time ranges to Longs for comparisons
        long sInterval = s.getInterval() * 60000;
        long sGrace = s.getGracePeriod() * 60000;
        long sDock = s.getDock() * 60000;
        
        switch (this.getPunchtypeid()){
            case 0:
                //SHIFT CLOCK-OUTS
                //If clocked-out late && within interval, snap to scheduled clock-out time
                if ((punchTime > sStopLong)
                && (punchTime < sStopLong + sInterval)){
                    this.setAdjustedTimestamp(sStopLong);
                    this.setAdjustmenttype("(Shift Stop)");
                } else
                
                //If clocked-out early, but within grace period
                if ((punchTime < sStopLong)
                && (punchTime > sStopLong - sGrace)){
                    this.setAdjustedTimestamp(sStopLong);
                    this.setAdjustmenttype("(Shift Stop)");
                } else
                
                //If clocked-out early, but outside of grace and within dock
                if ((punchTime < sStopLong - sGrace) 
                && (punchTime > sStopLong - sDock)){
                    //Set AdjTime to starting time + dock # of minutes
                    this.setAdjustedTimestamp(sStopLong - sDock);
                    this.setAdjustmenttype("(Shift Dock)");
                } else
                
                //LUNCH CLOCK-OUT
                //If clocked-out late during lunch break, snap to scheduled lunch-start time
                if ((punchTime > lStartLong)
                && (punchTime < lStopLong)){
                    this.setAdjustedTimestamp(lStartLong);
                    this.setAdjustmenttype("(Lunch Start)");
                } else {
                    
                //Round the timestamp to the nearest Interval
                    //originalTSCal.set(GregorianCalendar.MINUTE, ((originalTSCal.MINUTE + s.getInterval() - 1) / s.getInterval()) * s.getInterval());
                    //punchTime = originalTSCal.getTimeInMillis();
                    this.setAdjustedTimestamp(punchTime);
                    this.setAdjustmenttype("(None)");
                }
                break;
            
            case 1:
                //SHIFT CLOCK-INS
                //If clocked-in early && within interval, snap to scheduled clock-in time
                if ((punchTime < sStartLong)
                && (punchTime > sStartLong - sInterval)){
                    this.setAdjustedTimestamp(sStartLong);
                    this.setAdjustmenttype("(Shift Start)");
                } else
        
                //If clocked-in late, but within grace period
                if ((punchTime > sStartLong)
                && (punchTime < sStartLong - sGrace)){
                    this.setAdjustedTimestamp(sStartLong);
                    this.setAdjustmenttype("(Shift Start)");
                } else
        
                //If clocked-in late, but outside of grace and within dock
                if ((punchTime > sStartLong + sGrace) 
                && (punchTime < sStartLong + sDock)){
                    //Set AdjTime to starting time + dock # of minutes
                    this.setAdjustedTimestamp(sStartLong + sDock);
                    this.setAdjustmenttype("(Shift Dock)");
                } else
                
                //LUNCH CLOCK-INS
                //If clocked-in early, snap to scheduled lunch-stop time
                if ((punchTime < lStopLong)
                && (punchTime > lStartLong)){
                    this.setAdjustedTimestamp(lStopLong);
                    this.setAdjustmenttype("(Lunch Stop)");
                } else {
                    
                //Round the timestamp to the nearest Interval
                    //originalTSCal.set(GregorianCalendar.MINUTE, ((originalTSCal.MINUTE + s.getInterval() - 1) / s.getInterval()) * s.getInterval());
                    //punchTime = originalTSCal.getTimeInMillis();
                    this.setAdjustedTimestamp(punchTime);
                    this.setAdjustmenttype("(None)");
                }
                break;

        }
    }

    public void testAdjust(Shift shift) {
        long time = originaltimestamp % 86400000;
        long start = shift.getStart().toSecondOfDay() * 1000;
        long stop = shift.getStop().toSecondOfDay() * 1000;
        long lunchStart = shift.getLunchStart().toSecondOfDay() * 1000;
        long lunchStop = shift.getLunchStop().toSecondOfDay() * 1000;
        long interval = shift.getInterval() * 60000;
        long grace = shift.getGracePeriod() * 60000;
        long dock = shift.getDock() * 60000;
        //long adjustment;

        if ((originaltimestamp % (86400000 * 7)) < 86400000 * 5){
            switch (punchtypeid) {
                case 0: //clock out
                    adjustClockOut(time);
                    break;
                case 1: //clock in
                    adjustClockIn(time);
                    break;
                case 3: //time out
                    adjustTimeOut(time);
                    break;
            }
        }
        else {

        }
    }

    private long adjustClockOut(long time) {
        long start = shift.getStart().toSecondOfDay() * 1000;
        long stop = shift.getStop().toSecondOfDay() * 1000;
        long lunchStart = shift.getLunchStart().toSecondOfDay() * 1000;
        long lunchStop = shift.getLunchStop().toSecondOfDay() * 1000;
        long interval = shift.getInterval() * 60000;
        long grace = shift.getGracePeriod() * 60000;
        long dock = shift.getDock() * 60000;
        //long adjustment;

        if (time < start && time > start - interval) {  //in early within interval
            time = start;
        }
        else if (time > start && time < start + grace) {    //in late within grace
            time = start;
        }
        else if (time > start + grace && time < start + dock) { //dock
            time = start + dock;
        }
        else if ()
    }

    /*Setter methods*/
    public void setBadge(Badge badge){
        this.badge = badge;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public void setTerminalid(int terminalid){
        this.terminalid = terminalid;
    }
    
    public void setPunchtypeid(int punchtypeid){
        this.punchtypeid = punchtypeid;
    }
    
    public void setOriginaltimestamp(Long originaltimestamp){
        this.originaltimestamp = originaltimestamp;
    }
    
    public void setAdjustmenttype(String adjustmenttype){
        this.adjustmenttype = adjustmenttype;
    }
    
    public void setAdjustedTimestamp(Long adjustedtimestamp){
        this.adjustedTimestamp = adjustedtimestamp;
    }
    
    /*Getter methods*/
    public Badge getBadge(){
        return this.badge;
    }
    
    public int getId(){
        return this.id;
    }
    
    public int getTerminalid(){
        return this.terminalid;
    }
    
    public int getPunchtypeid(){
        return this.punchtypeid;
    }
    
    public Long getOriginaltimestamp(){
        return this.originaltimestamp;
    }
    
    public Long getAdjustedtimestamp(){
        return this.adjustedTimestamp;
    }
    
    public String getAdjustmenttype(){
        return this.adjustmenttype;
    }
    
    public String getBadgeid(){
        return (this.getBadge()).getID();
    }
    
    /*Print-out methods*/
    public String printOriginalTimestamp(){
        String s = "";
        
        //Add the relevant information
        s = "#" + this.getBadgeid();
        
        //To-do: make this work on a PunchType object
        switch (this.getPunchtypeid()){
            case 0:
                s += " CLOCKED OUT: ";
                break;
            case 1:
                s += " CLOCKED IN: ";
                break;
            case 2:
                s += " TIMED OUT: ";
        }
        
        DateFormat df = new SimpleDateFormat("EEE MM/dd/yyyy HH:mm:ss");
        Date d = new Date(this.originaltimestamp);
        
        s += (df.format(d)).toUpperCase();
                
        return s;
    }
    
    public String printAdjustedTimestamp(){
        String s = "";
        
        //Add the relevant information
        s = "#" + this.getBadgeid();
        
        //To-do: make this work on a PunchType object
        switch (this.getPunchtypeid()){
            case 0:
                s += " CLOCKED OUT: ";
                break;
            case 1:
                s += " CLOCKED IN: ";
                break;
            case 2:
                s += " TIMED OUT: ";
        }
        
        DateFormat df = new SimpleDateFormat("EEE MM/dd/yyyy HH:mm:ss");
        Date d = new Date(this.getAdjustedtimestamp());
        
        s += (df.format(d)).toUpperCase();
        
        s += " " + (this.getAdjustmenttype());
                
        return s;
    }
    
}
