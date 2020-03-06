package edu.jsu.mcis.tas_sp20;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.HashMap;

public class TASLogic {

    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift){//test
        //Total number of minutes minus lunch deducts, where applicable
        //Deal with time out punches, where employees forgot to clock out
        //Lunch break should be deducted from time only when time > min required for lunch break

        //punchType 0 == clock in
        //punchType 1 == clock out
        //punchType 2 == time out
        //Punch objects will have an adjustedTimestamp internal variable

        int total = 0;
        long inTime = 0;

        for (Punch punch : dailypunchlist) {
            switch (punch.getPunchtypeid()) {
                case 0:
                    inTime = punch.getAdjustedtimestamp();
                    break;
                case 1:
                    //TODO: is it possible to have a punch out before a punch in?
                    int minutes = (int)((punch.getAdjustedtimestamp() - inTime) / 60000);
                    if (minutes > shift.getLunchDeduct()) {//TODO: double check logic
                        minutes -= shift.getLunchDuration();
                    }
                    total += minutes;
                    break;
                case 2:
                    break;
            }
        }

        return total;
    }

    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist){//test
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
            map.put("adjustmenttype", punch.getAdjustmenttype());
            mapList.add(map);
        }

        return JSONValue.toJSONString(mapList);
    }

    public static String getPunchListPlusTotalsAsJSON(ArrayList<Punch> punchList, Shift shift) {//test
        //TODO: Can I just call getPunchListAsJSON() and reconstitute the string instead of redoing the work?
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
            map.put("adjustmenttype", punch.getAdjustmenttype());
            mapList.add(map);
        }

        map = new HashMap<>();
        map.put("totalminutes", String.valueOf(calculateTotalMinutes(punchList, shift)));
        map.put("absenteeism", String.valueOf(calculateAbsenteeism(punchList, shift)));
        mapList.add(map);


        return JSONValue.toJSONString(mapList);
    }
}
