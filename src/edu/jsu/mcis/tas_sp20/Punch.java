package edu.jsu.mcis.tas_sp20;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class Punch {
    private int id, terminalid, punchtypeid;
    private Badge badge;
    private Long originaltimestamp, adjustedtimestamp;
    private String adjustmenttype;
    
    public Punch(Badge badge, int terminalid, int punchtypeid){
        this.id = -1;   
        this.adjustmenttype = null;
        
        this.terminalid = terminalid;
        this.badge = badge;
        this.punchtypeid = punchtypeid;
        
        this.originaltimestamp = System.currentTimeMillis();
        this.adjustedtimestamp = null;
    }

    public Punch(int id, int terminalid, Badge badge, Long timestamp, int punchtypeid){
        this.id = id;   
        this.adjustmenttype = null;
        
        this.terminalid = terminalid;
        this.badge = badge;
        this.punchtypeid = punchtypeid;
        
        this.originaltimestamp = timestamp;
        this.adjustedtimestamp = null;
    }
    
    public void adjust(Shift s){
        //Convert original timestamp to a calendar
        GregorianCalendar originalTSCal = new GregorianCalendar();
        originalTSCal.setTimeInMillis(this.originaltimestamp);
        originalTSCal.clear(GregorianCalendar.SECOND);

        long punchTime = originalTSCal.getTimeInMillis();

        int day = originalTSCal.get(Calendar.DAY_OF_WEEK);

        if (day != Calendar.SATURDAY && day != Calendar.SUNDAY){ //Check if punch was on a weekday

            //Create calendars based on the schedule for that specific day
            GregorianCalendar sStartCal = (GregorianCalendar) originalTSCal.clone();
            sStartCal.set(GregorianCalendar.HOUR_OF_DAY, s.getStart(day).getHour());
            sStartCal.set(GregorianCalendar.MINUTE, s.getStart(day).getMinute());

            GregorianCalendar sStopCal = (GregorianCalendar) originalTSCal.clone();
            sStopCal.set(GregorianCalendar.HOUR_OF_DAY, s.getStop(day).getHour());
            sStopCal.set(GregorianCalendar.MINUTE, s.getStop(day).getMinute());

            GregorianCalendar lStartCal = (GregorianCalendar) originalTSCal.clone();
            lStartCal.set(GregorianCalendar.HOUR_OF_DAY, s.getLunchStart(day).getHour());
            lStartCal.set(GregorianCalendar.MINUTE, s.getLunchStart(day).getMinute());

            GregorianCalendar lStopCal = (GregorianCalendar) originalTSCal.clone();
            lStopCal.set(GregorianCalendar.HOUR_OF_DAY, s.getLunchStop(day).getHour());
            lStopCal.set(GregorianCalendar.MINUTE, s.getLunchStop(day).getMinute());

            //Get the timestamp equivalents of the calendars
            long sStartLong = sStartCal.getTimeInMillis();
            long sStopLong = sStopCal.getTimeInMillis();
            long lStartLong = lStartCal.getTimeInMillis();
            long lStopLong = lStopCal.getTimeInMillis();

            //Convert time ranges to Longs for comparisons
            long sInterval = s.getInterval(day) * 60000;
            long sGrace = s.getGracePeriod(day) * 60000;
            long sDock = s.getDock(day) * 60000;

            switch (this.getPunchtypeid()){
                case 0:
                    //SHIFT CLOCK-OUTS
                    //If clocked-out late && within interval, snap to scheduled clock-out time
                    if ((punchTime >= sStopLong)
                    && (punchTime <= sStopLong + sInterval)){
                        this.setAdjustedTimestamp(sStopLong);
                        this.setAdjustmenttype("Shift Stop");
                    }

                    //If clocked-out early, but within grace period
                    else if ((punchTime <= sStopLong)
                    && (punchTime >= sStopLong - sGrace)){
                        this.setAdjustedTimestamp(sStopLong);
                        this.setAdjustmenttype("Shift Stop");
                    }

                    //If clocked-out early, but outside of grace and within dock
                    else if ((punchTime <= sStopLong - sGrace)
                    && (punchTime >= sStopLong - sDock)){
                        this.setAdjustedTimestamp(sStopLong - sDock);
                        this.setAdjustmenttype("Shift Dock");
                    }

                    //LUNCH CLOCK-OUT
                    //If clocked-out late during lunch break, snap to scheduled lunch-start time
                    else if ((punchTime >= lStartLong)
                    && (punchTime <= lStopLong)){
                        this.setAdjustedTimestamp(lStartLong);
                        this.setAdjustmenttype("Lunch Start");
                    }

                    //NON-SPECIAL CLOCK-OUT CASES
                    //Default adjustments
                    else {
                        punchDefaultAdjust(sInterval, punchTime);
                    }
                    break;

                case 1:
                    //SHIFT CLOCK-INS
                    //If clocked-in early && within interval, snap to scheduled clock-in time
                    if ((punchTime <= sStartLong)
                    && (punchTime >= sStartLong - sInterval)){
                        this.setAdjustedTimestamp(sStartLong);
                        this.setAdjustmenttype("Shift Start");
                    } 

                    //If clocked-in late, but within grace period
                    else if ((punchTime >= sStartLong)
                    && (punchTime <= sStartLong + sGrace)){
                        this.setAdjustedTimestamp(sStartLong);
                        this.setAdjustmenttype("Shift Start");
                    }

                    //If clocked-in late, but outside of grace and within dock
                    else if ((punchTime >= sStartLong + sGrace)
                    && (punchTime <= sStartLong + sDock)){
                        this.setAdjustedTimestamp(sStartLong + sDock);
                        this.setAdjustmenttype("Shift Dock");
                    }

                    //LUNCH CLOCK-INS
                    //If clocked-in early, snap to scheduled lunch-stop time
                    else if ((punchTime <= lStopLong)
                    && (punchTime >= lStartLong)){
                        this.setAdjustedTimestamp(lStopLong);
                        this.setAdjustmenttype("Lunch Stop");
                    }

                    //NON-SPECIAL CLOCK-IN CASES
                    //Default adjustments
                    else {
                        punchDefaultAdjust(sInterval, punchTime);
                    }
                    break;
                }
        }

        else { //If the day is on the weekend
            //Convert shift Interval to Long for comparisons
            long sInterval = s.getInterval() * 60000;

            //Default adjustments
            punchDefaultAdjust(sInterval, punchTime);
        }
    }

    public void punchDefaultAdjust(long interval, long punchtime){
        //If clocked-in time is not on an even Interval, round it to nearest Interval
        if (punchtime % interval != 0){
            if (this.getOriginaltimestamp() % interval < interval / 2){
                punchtime = Math.round((long)(punchtime / (interval))) * (interval);
            } else {
                punchtime = Math.round((long)(punchtime + interval)/(interval) ) * (interval);
            }
            this.setAdjustmenttype("Interval Round");
        } else {
            //Make no changes
            this.setAdjustmenttype("None");
        }
        
        this.setAdjustedTimestamp(punchtime);
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
        this.adjustedtimestamp = adjustedtimestamp;
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
        return this.adjustedtimestamp;
    }
    
    public String getAdjustmenttype(){
        return this.adjustmenttype;
    }
    
    public String getBadgeid(){
        return this.getBadge().getId();
    }
    
    /*Print-out methods*/
    
    private String buildString(long ts){
        String s;
        
        //Add the relevant information
        s = "#" + this.getBadgeid();
        
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
        Date d = new Date(ts);
        
        s += (df.format(d)).toUpperCase();
        
        return s;
    }
    
    public String printOriginalTimestamp(){
        return buildString(this.originaltimestamp);
    }
    
    public String printAdjustedTimestamp(){
        return (buildString(this.adjustedtimestamp) + " (" + (this.getAdjustmenttype()) + ")");
    }
    
}