package edu.jsu.mcis.tas_sp20;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoField; 


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
        //Convert LocalTimes to Long timestamps
        Long shiftStart = (s.getStart()).getLong(ChronoField.MILLI_OF_SECOND);
        Long shiftStop = (s.getStop()).getLong(ChronoField.MILLI_OF_SECOND);
        Long shiftLunchStart = (s.getLunchStart()).getLong(ChronoField.MILLI_OF_SECOND);
        Long shiftLunchStop = (s.getLunchStop()).getLong(ChronoField.MILLI_OF_SECOND);
        
        //If clocked-in early, snap to scheduled clock-in time
        if ((this.getPunchtypeid() == 1) && (this.getOriginaltimestamp() - shiftStart < 0)){
            this.setAdjustedTimestamp(shiftStart);
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
        return "To-do";
    }
    
}
