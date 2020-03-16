package edu.jsu.mcis.tas_sp20;

import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.HashMap;

public class TASLogic {

    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift){
        int total = 0;
        long inTime = 0;

        for (Punch punch : dailypunchlist) {
            switch (punch.getPunchtypeid()) {
                case 0:
                    int minutes = (int)((punch.getAdjustedtimestamp() - inTime) / 60000);
                    if (minutes > shift.getLunchDeduct()) {
                        minutes -= shift.getLunchDuration();
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
            map.put("punchdata", punch.getAdjustmenttype());
            mapList.add(map);
        }

        return JSONValue.toJSONString(mapList);
    }

    // TODO: Find how to get arrayList from getPayPeriodPunchList
    public static double calculateAbsenteeism(ArrayList<Punch> punchlist, Shift s) {
        double minWorked;
        double percentage;



        for (Punch punch : punchlist){
            calculateTotalMinutes(punchlist, s);
        }

        return 0;
    }

//    public static String getPunchListPlusTotalsAsJSON(ArrayList<Punch> punchList, Shift shift) {//test
//        HashMap<String, String> map;
//        ArrayList<HashMap<String, String>> mapList = new ArrayList<>();
//
//        for (Punch punch : punchList) {
//            map = new HashMap<>();
//            map.put("id", String.valueOf(punch.getId()));
//            map.put("terminalid", String.valueOf(punch.getTerminalid()));
//            map.put("punchtypeid", String.valueOf(punch.getPunchtypeid()));
//            map.put("badgeid", punch.getBadgeid());
//            map.put("originaltimestamp", String.valueOf(punch.getOriginaltimestamp()));
//            map.put("adjustedtimestamp", String.valueOf(punch.getAdjustedtimestamp()));
//            map.put("adjustmenttype", punch.getAdjustmenttype());
//            mapList.add(map);
//        }
//
//        map = new HashMap<>();
//        map.put("totalminutes", String.valueOf(calculateTotalMinutes(punchList, shift)));
//        map.put("absenteeism", String.valueOf(calculateAbsenteeism(punchList, shift)));
//        mapList.add(map);
//
//
//        return JSONValue.toJSONString(mapList);
//    }
}
