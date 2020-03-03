package edu.jsu.mcis.tas_sp20;

import java.util.ArrayList;

public class TASLogic {


    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift){
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
                    inTime = punch.getAdjustedTimestamp();
                    break;
                case 1:
                    //TODO: is it possible to have a punch out before a punch in?
                    int minutes = (punch.getAdjustedTimestamp - inTime) / 60000; //TODO: how to round?
                    if (minutes > shift.getLunchDeduct()) {//TODO: double check logic
                        minutes -= shift.getLunchDuration();
                    }
                    total += minutes;
                    break;
                case 2:
                    break;
            }
        }
        return -1;
    }
}
