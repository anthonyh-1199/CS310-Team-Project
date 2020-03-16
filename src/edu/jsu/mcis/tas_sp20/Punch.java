package edu.jsu.mcis.tas_sp20;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class Punch {
    private int id, terminalid, punchtypeid;
    private Badge badge;
    private Long originaltimestamp, adjustedTimestamp;
    private String adjustmenttype;
    
    Punch(Badge badge, int terminalid, int punchtypeid){
        this.id = 0;   
        this.adjustmenttype = null;
        
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
    
    Punch(int id, int terminalid, Badge badge, Long timestamp, int punchtypeid){
        this.id = id;   
        this.adjustmenttype = null;
        
        this.terminalid = terminalid;
        this.badge = badge;
        this.punchtypeid = punchtypeid;
        
        this.originaltimestamp = timestamp;
        this.adjustedTimestamp = this.originaltimestamp;
    }
    
    public void adjust(Shift s){
        //Convert shift times to Gregorian Calenders and Longs
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(this.originaltimestamp);
        int day = cal.get(Calendar.DAY_OF_WEEK);

        if (day != Calendar.SATURDAY && day != Calendar.SUNDAY){
            GregorianCalendar originalTSCal = new GregorianCalendar();
            originalTSCal.setTimeInMillis(this.getOriginaltimestamp());
            originalTSCal.clear(GregorianCalendar.SECOND);
            Long punchTime = originalTSCal.getTimeInMillis();

            GregorianCalendar sStartCal = (GregorianCalendar) originalTSCal.clone();
            sStartCal.set(GregorianCalendar.HOUR_OF_DAY, s.getStart(day).getHour());
            sStartCal.set(GregorianCalendar.MINUTE, s.getStart(day).getMinute());
            Long sStartLong = sStartCal.getTimeInMillis();

            GregorianCalendar sStopCal = (GregorianCalendar) originalTSCal.clone();
            sStopCal.set(GregorianCalendar.HOUR_OF_DAY, s.getStop(day).getHour());
            sStopCal.set(GregorianCalendar.MINUTE, s.getStop(day).getMinute());
            Long sStopLong = sStopCal.getTimeInMillis();

            GregorianCalendar lStartCal = (GregorianCalendar) originalTSCal.clone();
            lStartCal.set(GregorianCalendar.HOUR_OF_DAY, s.getLunchStart(day).getHour());
            lStartCal.set(GregorianCalendar.MINUTE, s.getLunchStart(day).getMinute());
            Long lStartLong = lStartCal.getTimeInMillis();

            GregorianCalendar lStopCal = (GregorianCalendar) originalTSCal.clone();
            lStopCal.set(GregorianCalendar.HOUR_OF_DAY, s.getLunchStop(day).getHour());
            lStopCal.set(GregorianCalendar.MINUTE, s.getLunchStop(day).getMinute());
            Long lStopLong = lStopCal.getTimeInMillis();

            //Convert time ranges to Longs for comparisons
            long sInterval = s.getInterval(day) * 60000;
            long sGrace = s.getGracePeriod(day) * 60000;
            long sDock = s.getDock(day) * 60000;

            //Check if the punch was during a weekend
            if ((originalTSCal.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SATURDAY) && (originalTSCal.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SUNDAY)){
                switch (this.getPunchtypeid()){
                    case 0:
                        //SHIFT CLOCK-OUTS
                        //If clocked-out late && within interval, snap to scheduled clock-out time
                        if ((punchTime >= sStopLong)
                        && (punchTime <= sStopLong + sInterval)){
                            this.setAdjustedTimestamp(sStopLong);
                            this.setAdjustmenttype("Shift Stop");
                        } else

                        //If clocked-out early, but within grace period
                        if ((punchTime <= sStopLong)
                        && (punchTime >= sStopLong - sGrace)){
                            this.setAdjustedTimestamp(sStopLong);
                            this.setAdjustmenttype("Shift Stop");
                        } else

                        //If clocked-out early, but outside of grace and within dock
                        if ((punchTime <= sStopLong - sGrace)
                        && (punchTime >= sStopLong - sDock)){
                            this.setAdjustedTimestamp(sStopLong - sDock);
                            this.setAdjustmenttype("Shift Dock");
                        } else

                        //LUNCH CLOCK-OUT
                        //If clocked-out late during lunch break, snap to scheduled lunch-start time
                        if ((punchTime >= lStartLong)
                        && (punchTime <= lStopLong)){
                            this.setAdjustedTimestamp(lStartLong);
                            this.setAdjustmenttype("Lunch Start");
                        } else

                        //NON-SPECIAL CLOCK-OUT CASES
                        //If clocked-out time is not on an even Interval, round it to nearest Interval
                        if (punchTime % sInterval != 0){
                            if (this.getOriginaltimestamp() % sInterval < sInterval / 2){
                                punchTime = Math.round((long)punchTime/(sInterval) ) * (sInterval);
                            } else {
                                punchTime = Math.round((long)(punchTime + sInterval)/(sInterval) ) * (sInterval);
                            }
                            this.setAdjustedTimestamp(punchTime);
                            this.setAdjustmenttype("Interval Round");
                        } else
                        //Make no changes
                        {
                            this.setAdjustedTimestamp(punchTime); //Needed to clear Seconds field
                            this.setAdjustmenttype("None");
                        }
                        break;

                    case 1:
                        //SHIFT CLOCK-INS
                        //If clocked-in early && within interval, snap to scheduled clock-in time
                        if ((punchTime <= sStartLong)
                        && (punchTime >= sStartLong - sInterval)){
                            this.setAdjustedTimestamp(sStartLong);
                            this.setAdjustmenttype("Shift Start");
                        } else

                        //If clocked-in late, but within grace period
                        if ((punchTime >= sStartLong)
                        && (punchTime <= sStartLong + sGrace)){
                            this.setAdjustedTimestamp(sStartLong);
                            this.setAdjustmenttype("Shift Start");
                        } else

                        //If clocked-in late, but outside of grace and within dock
                        if ((punchTime >= sStartLong + sGrace)
                        && (punchTime <= sStartLong + sDock)){
                            this.setAdjustedTimestamp(sStartLong + sDock);
                            this.setAdjustmenttype("Shift Dock");
                        } else

                        //LUNCH CLOCK-INS
                        //If clocked-in early, snap to scheduled lunch-stop time
                        if ((punchTime <= lStopLong)
                        && (punchTime >= lStartLong)){
                            this.setAdjustedTimestamp(lStopLong);
                            this.setAdjustmenttype("Lunch Stop");
                        } else

                        //NON-SPECIAL CLOCK-IN CASES
                        //If clocked-in time is not on an even Interval, round it to nearest Interval
                        if (punchTime % sInterval != 0){
                            if (this.getOriginaltimestamp() % sInterval < sInterval / 2){
                                punchTime = Math.round((long)punchTime/(sInterval) ) * (sInterval);
                            } else {
                                punchTime = Math.round((long)(punchTime + sInterval)/(sInterval) ) * (sInterval);
                            }
                            this.setAdjustedTimestamp(punchTime);
                            this.setAdjustmenttype("Interval Round");
                        } else
                        //Make no changes
                        {
                            this.setAdjustedTimestamp(punchTime); //Needed to clear Seconds field
                            this.setAdjustmenttype("None");
                        }
                        break;
                }
            }

        }

        else { //If the day is on the weekend
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

            //If clocked-in time is not on an even Interval, round it to nearest Interval
            if (punchTime % sInterval != 0){
                if (this.getOriginaltimestamp() % sInterval < sInterval / 2){
                    punchTime = Math.round((long)punchTime/(sInterval) ) * (sInterval);
                } else {
                    punchTime = Math.round((long)(punchTime + sInterval)/(sInterval) ) * (sInterval);
                }
                this.setAdjustedTimestamp(punchTime);
                this.setAdjustmenttype("Interval Round");
            } else 
            //Make no changes
            {
                this.setAdjustedTimestamp(punchTime); //Needed to clear Seconds field
                this.setAdjustmenttype("None");
            }
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
        return (this.getBadge()).getId();
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
        
        s += " (" + (this.getAdjustmenttype()) + ")";
                
        return s;
    }
    
}
