package edu.jsu.mcis.tas_sp20;

import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.lang.Math;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;

public class TASLogic {

    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift){
        int total = 0;
        int day;
        long inTime = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dailypunchlist.get(0).getOriginaltimestamp());
        day = cal.get(Calendar.DAY_OF_WEEK);
        boolean isWeekend = (day == Calendar.SATURDAY) || (day == Calendar.SUNDAY);

        for (Punch punch : dailypunchlist) {
            switch (punch.getPunchtypeid()) {
                case 0:
                    int minutes = (int)((punch.getAdjustedtimestamp() - inTime) / 60000);
                    if (!isWeekend){
                        if (minutes > shift.getLunchDeduct(day)) {
                            minutes -= shift.getLunchDuration(day);
                        }
                    }

                    else {
                        if (minutes > shift.getLunchDeduct()) {
                            minutes -= shift.getLunchDuration();
                        }
                    }
                    total += minutes;
                    break;
                case 1:
                    inTime = punch.getAdjustedtimestamp();
                    break;
                case 2:
                    break;
            }
        }

        return total;
    }

    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist){
        HashMap<String, String> map;
        ArrayList<HashMap<String, String>> mapList = new ArrayList<>();

        for (Punch punch : dailypunchlist) {
            map = new HashMap<>();
            map.put("id", String.valueOf(punch.getId()));
            map.put("terminalid", String.valueOf(punch.getTerminalid()));
            map.put("punchtypeid", String.valueOf(punch.getPunchtypeid()));
            map.put("badgeid", punch.getBadgeid());
            map.put("originaltimestamp", String.valueOf(punch.getOriginaltimestamp()));
            map.put("adjustedtimestamp", String.valueOf(punch.getAdjustedtimestamp()));
            map.put("punchdata", punch.getAdjustmenttype());
            mapList.add(map);
        }

        return JSONValue.toJSONString(mapList);
    }

    // TODO: Find how to get arrayList from getPayPeriodPunchList
    public static double calculateAbsenteeism(ArrayList<Punch> punchlist, Shift s) {    //TODO: move so that it doesn't seperate above and below methods, which are related
        int[] days = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY};
        double minWorked, minScheduled = 0, percentage;
        
        minWorked = calculateTotalMinutes(punchlist, s);
        
        for(int day : days){
            LocalTime start = s.getStart(day);
            LocalTime stop = s.getStop(day);
            minScheduled += start.until(stop, ChronoUnit.MINUTES);
            
            minScheduled -= s.getLunchDuration(day);    //TODO: technically, shouldn't this only happen if the hours are above lunchDeduct? (see calculateTotalMinutes)
        }

        percentage = (1 - (minWorked / minScheduled)) * 10000;
        if (percentage < 0) {   //TODO: get the math whiz to tell us why we're idiots
            percentage = -(Math.round(Math.abs(percentage * 10)));
            percentage /= 10;
        }
        else {
            percentage = Math.round(percentage);
        }

        percentage = percentage / 100;
        return percentage;
    }

    public static String getPunchListPlusTotalsAsJSON(ArrayList<Punch> punchList, Shift shift) {//test
        HashMap<String, String> map;
        ArrayList<HashMap<String, String>> mapList = new ArrayList<>();

        for (Punch punch : punchList) {
            map = new HashMap<>();
            map.put("id", String.valueOf(punch.getId()));
            map.put("terminalid", String.valueOf(punch.getTerminalid()));
            map.put("punchtypeid", String.valueOf(punch.getPunchtypeid()));
            map.put("badgeid", punch.getBadgeid());
            map.put("originaltimestamp", String.valueOf(punch.getOriginaltimestamp()));
            map.put("adjustedtimestamp", String.valueOf(punch.getAdjustedtimestamp()));
            map.put("punchdata", punch.getAdjustmenttype());
            mapList.add(map);
        }

        map = new HashMap<>();
        map.put("totalminutes", String.valueOf(calculateTotalMinutes(punchList, shift)));
        map.put("absenteeism", String.valueOf((new DecimalFormat("0.00")).
                format(calculateAbsenteeism(punchList, shift)) + '%'));
        mapList.add(map);


        return JSONValue.toJSONString(mapList);
    }
    
    public static GregorianCalendar convertLongtoGC(long l){
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(l);
        gc.add(Calendar.DAY_OF_WEEK, -(gc.get(Calendar.DAY_OF_WEEK) - 1));
        gc.set(Calendar.HOUR, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        
        return gc;
    }
}
