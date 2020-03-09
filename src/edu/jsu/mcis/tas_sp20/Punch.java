package edu.jsu.mcis.tas_sp20;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoField; 
import java.sql.Timestamp;
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
        this.id = 0;   
        this.adjustmenttype = null;
        
        this.terminalid = terminalid;
        this.badge = badge;
        this.punchtypeid = punchtypeid;
        
        this.originaltimestamp = timestamp;
        this.adjustedTimestamp = this.originaltimestamp;
    }
    
    public void adjust(Shift s){
        //Convert LocalTimes to Long timestamps
        DateFormat df = new SimpleDateFormat("HH:mm:00");
        Date d = new Date(this.originaltimestamp);
        String str = df.format(d);
        LocalTime lt = LocalTime.parse(str);
        
        Long l = this.getOriginaltimestamp() % 86400000;
        Long daysTime = this.getOriginaltimestamp() - l ;
        
        switch (this.getPunchtypeid()){
            case 0:
                //SHIFT CLOCK-OUTS
                //If clocked-out late && within interval, snap to scheduled clock-out time
                if ((lt.compareTo(s.getStop()) > 0)
                && (lt.compareTo(s.getStop().plusMinutes(s.getInterval())) < 0)){
                    this.setAdjustedTimestamp((s.getStop()).toSecondOfDay() * 1000 + daysTime + 18000000);
                    this.setAdjustmenttype("(Shift Stop)");
                } else
                
                //If clocked-out early, but within grace period
                if (lt.compareTo(s.getStop().minusMinutes(s.getGracePeriod())) > 0){
                    this.setAdjustedTimestamp((s.getStop()).toSecondOfDay() * 1000 + daysTime + 18000000);
                    this.setAdjustmenttype("(Shift Stop)");
                } else
                
                //If clocked-out early, but outside of grace and within dock
                if ((lt.compareTo(s.getStart().plusMinutes(s.getGracePeriod())) <= 0) 
                && (lt.compareTo(s.getStart().plusMinutes(s.getDock())) >= 0)){
                    //Set AdjTime to starting time + dock # of minutes
                    this.setAdjustedTimestamp((s.getStart()).toSecondOfDay() * 1000 + daysTime + 18000000
                    + (s.getDock() * 60000));
                    this.setAdjustmenttype("(Shift Dock)");
                } else
                
                //LUNCH CLOCK-OUT
                //If clocked-out late during lunch break, snap to scheduled lunch-start time
                if ((lt.compareTo(s.getLunchStart()) > 0)
                && (lt.compareTo(s.getLunchStop()) < 0)){
                    this.setAdjustedTimestamp((s.getLunchStart()).toSecondOfDay() * 1000 + daysTime + 18000000);
                    this.setAdjustmenttype("(Lunch Start)");
                }
                break;
            
            case 1:
                //SHIFT CLOCK-INS
                //If clocked-in early && within interval, snap to scheduled clock-in time
                if ((lt.compareTo(s.getStart()) < 0)
                && (lt.compareTo(s.getStart().plusMinutes(s.getInterval())) > 0)){
                    this.setAdjustedTimestamp((s.getStart()).toSecondOfDay() * 1000 + daysTime + 18000000);
                    this.setAdjustmenttype("(Shift Start)");
                } else
        
                //If clocked-in late, but within grace period
                if (lt.compareTo(s.getStart().plusMinutes(s.getGracePeriod())) < 0){
                    this.setAdjustedTimestamp((s.getStart()).toSecondOfDay() * 1000 + daysTime + 18000000);
                    this.setAdjustmenttype("(Shift Start)");
                } else
        
                //If clocked-in late, but outside of grace and within dock
                if ((lt.compareTo(s.getStart().plusMinutes(s.getGracePeriod())) > 0) 
                && (lt.compareTo(s.getStart().plusMinutes(s.getDock())) <= 0)){
                    //Set AdjTime to starting time + dock # of minutes
                    this.setAdjustedTimestamp((s.getStart()).toSecondOfDay() * 1000 + daysTime + 18000000
                    + (s.getDock() * 60000));
                    this.setAdjustmenttype("(Shift Dock)");
                } else
                
                //LUNCH CLOCK-INS
                //If clocked-in early, snap to scheduled lunch-stop time
                if ((lt.compareTo(s.getLunchStop()) < 0)
                && (lt.compareTo(s.getLunchStart()) > 0)){
                    this.setAdjustedTimestamp((s.getLunchStop()).toSecondOfDay() * 1000 + daysTime + 18000000);
                    this.setAdjustmenttype("(Lunch Stop)");
                } else {
                    this.setAdjustmenttype("(None)");
                }
                break;

        }
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
