package edu.jsu.mcis.tas_sp20;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TASDatabase {
    private Connection conn;
    
    public final int DAY_IN_MILLIS = 86400000;

    public static void main(String[] args) {
    }

    public TASDatabase(){
        try {
            String server = "jdbc:mysql://localhost/TAS";
            String user = "admin";
            String pass = "password";
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            conn = DriverManager.getConnection(server, user, pass);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TASDatabase(String server, String user, String pass) {
        try {
            server = "jdbc:mysql://localhost/TAS";
            user = "admin";
            pass = "password";
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            conn = DriverManager.getConnection(server, user, pass);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Punch getPunch(int ID) {
        Punch punch = null;
        try {
            String query;
            PreparedStatement pstPunch, pstBadge;
            ResultSet resultSet;

            query = "SELECT * FROM punch WHERE id=?";
            pstPunch = conn.prepareStatement(query);
            pstPunch.setInt(1, ID);

            pstPunch.execute();
            resultSet = pstPunch.getResultSet();
            resultSet.first();

            int terminalID = resultSet.getInt(2);
            String badgeID = resultSet.getString(3);
            long origTimeStamp = resultSet.getTimestamp(4).getTime();
            int punchTypeID = resultSet.getInt(5);

            query = "SELECT * FROM badge WHERE id=?";
            pstBadge = conn.prepareStatement(query);
            pstBadge.setString(1, badgeID);
            pstBadge.execute();
            resultSet = pstBadge.getResultSet();
            resultSet.first();

            Badge badge = new Badge(resultSet.getString(1), resultSet.getString(2));

            punch = new Punch(ID, terminalID, badge, origTimeStamp, punchTypeID);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return punch;
    }

    public Badge getBadge(String ID) {
        Badge badge = null;
        String query;
        PreparedStatement pst;
        ResultSet resultSet;

        try {
            query = "SELECT * FROM badge WHERE id=?";
            pst = conn.prepareStatement(query);
            pst.setString(1, ID);
            pst.execute();

            resultSet = pst.getResultSet();
            resultSet.first();
            badge = new Badge(ID, resultSet.getString(2));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return badge;
    }

    public Shift getShift(int ID) {
        Shift shift = null;
        String query;
        PreparedStatement pst;
        ResultSet resultSet;

        try {
            query = "SELECT * FROM shift WHERE id=?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, ID);

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();
            java.sql.Time temp;

            int ShiftID = resultSet.getInt(1);
            String description = resultSet.getString(2);
            temp = resultSet.getTime(3);
            LocalTime start = temp.toLocalTime();
            temp = resultSet.getTime(4);
            LocalTime stop = temp.toLocalTime();
            int interval = resultSet.getInt(5);
            int gracePeriod = resultSet.getInt(6);
            int dock = resultSet.getInt(7);
            temp = resultSet.getTime(8);
            LocalTime lunchStart = temp.toLocalTime();
            temp = resultSet.getTime(9);
            LocalTime lunchStop = temp.toLocalTime();
            int lunchDeduct = resultSet.getInt(10);
            shift = new Shift(ShiftID, description, start, stop, interval, gracePeriod, dock, lunchStart, lunchStop, lunchDeduct);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return shift;
    }

    public Shift getShift(Badge badge) {
        Shift shift = null;
        String query;
        PreparedStatement pst;
        ResultSet resultSet;

        try {
            query = "SELECT shiftid FROM employee WHERE badgeid = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, badge.getId());

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();
            java.sql.Time temp;

            int shiftID = resultSet.getInt(1);
            query = "SELECT * FROM shift WHERE id=?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, shiftID);
            pst.execute();

            resultSet = pst.getResultSet();
            resultSet.first();

            String description = resultSet.getString(2);
            temp = resultSet.getTime(3);
            LocalTime start = temp.toLocalTime();
            temp = resultSet.getTime(4);
            LocalTime stop = temp.toLocalTime();
            int interval = resultSet.getInt(5);
            int gracePeriod = resultSet.getInt(6);
            int dock = resultSet.getInt(7);
            temp = resultSet.getTime(8);
            LocalTime lunchStart = temp.toLocalTime();
            temp = resultSet.getTime(9);
            LocalTime lunchStop = temp.toLocalTime();
            int lunchDeduct = resultSet.getInt(10);
            shift = new Shift(shiftID, description, start, stop, interval, gracePeriod, dock, lunchStart, lunchStop, lunchDeduct);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shift;
    }

    public int insertPunch(Punch p) {
        GregorianCalendar ots = new GregorianCalendar();
        ots.setTimeInMillis(p.getOriginaltimestamp());
        String badgeID = p.getBadge().getId();
        int terminalID = p.getTerminalid(), punchTypeID = p.getPunchtypeid();

        try {
            PreparedStatement pst;
            ResultSet resultSet;
            String query;


            if (conn.isValid(0)){
                query = "INSERT INTO punch (terminalid, badgeid, originaltimestamp, punchtypeid) VALUES (?, ?, ?, ?)";
                pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pst.setInt(1, terminalID);
                pst.setString(2, badgeID.toString());
                pst.setString(3, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(ots.getTime()));
                pst.setInt(4, punchTypeID);

                pst.execute();
                resultSet = pst.getGeneratedKeys();
                resultSet.first();
                if (resultSet.getInt(1) > 0) {
                    return resultSet.getInt(1);
                } else {
                    return -1;
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;

    }
    
    public ArrayList<Punch> getDailyPunchList(Badge badge, long ts){
        Timestamp timestamp = new Timestamp(ts);
        String timeLike = timestamp.toString().substring(0, 11);
        timeLike += "%";
        ArrayList<Punch> dailyPunchList = new ArrayList<>();
        
        Timestamp nextDay = new Timestamp(ts + this.DAY_IN_MILLIS);
        String timeLikeNext = nextDay.toString().substring(0, 11);
        timeLikeNext += "%";

        try {
            PreparedStatement pst;
            ResultSet resultSet;
            String query;
            boolean isPaired = true;


            if (conn.isValid(0)){
                query = "SELECT * FROM punch WHERE badgeid = ? AND originaltimestamp LIKE ?";
                pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, badge.getId());
                pst.setString(2, timeLike);

                pst.execute();
                resultSet = pst.getResultSet();
                
                while(resultSet.next()){
                    int punchId = resultSet.getInt("id");
                    
                    Punch temp = this.getPunch(punchId);
                    dailyPunchList.add(temp);
                    
                    isPaired = !isPaired;
                }
                
                if(!isPaired){
                query = "SELECT * FROM punch WHERE badgeid = ? AND originaltimestamp LIKE ?";
                pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, badge.getId());
                pst.setString(2, timeLikeNext);
                
                pst.execute();
                resultSet = pst.getResultSet();
                resultSet.first();
                
                int punchId = resultSet.getInt("id");

                Punch temp = this.getPunch(punchId);
                dailyPunchList.add(temp);     
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dailyPunchList;
    }
    
    public ArrayList<Punch> getPayPeriodPunchList(Badge badge, long ts){
        ArrayList<Punch> returnArray = new ArrayList<>();
        
        for(int i = 0; i < 7; i++){
            ArrayList<Punch> temp = this.getDailyPunchList(badge, ts + (this.DAY_IN_MILLIS * i));
            
            for(Punch p: temp){
                returnArray.add(p);
            }
        }
        
        return returnArray;
    }
    
    public Absenteeism getAbsenteeism(String badgeId, long ts){
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(ts);
        gc.add(Calendar.DAY_OF_WEEK, -(gc.get(Calendar.DAY_OF_WEEK) - 1));
        gc.set(Calendar.HOUR, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        long tsNew = gc.getTimeInMillis();
        
        Timestamp timestamp = new Timestamp(tsNew);
        Absenteeism returnAbsenteeism = null;
                
        try {
            PreparedStatement pst;
            ResultSet resultSet;
            String query;

            if (conn.isValid(0)){
                query = "SELECT * FROM absenteeism WHERE badgeid = ? AND payperiod = ?";
                pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, badgeId);
                pst.setTimestamp(2, timestamp);

                pst.execute();
                resultSet = pst.getResultSet();
                resultSet.first();
                
                double percent = resultSet.getDouble("percentage");
                    
                returnAbsenteeism = new Absenteeism(badgeId, ts, percent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return returnAbsenteeism;

    }
    
    public void insertAbsenteeism(Absenteeism abs){
        System.out.println("Retrieving ab");
        Absenteeism ab = this.getAbsenteeism(abs.getBadgeId(), abs.getTimestampLong());
        System.out.println("Retrieved ab");
        
        
        try {
            if(ab == null){
                PreparedStatement pst;
                String query;
                System.out.println("Inserting");
                
                //Try to request abs, if null, insert

                if (conn.isValid(0)){
                    //Insert
                    query = "INSERT INTO absenteeism (badgeid, payperiod, percentage) VALUES (?, ?, ?)";
                    pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    pst.setString(1, abs.getBadgeId());
                    pst.setTimestamp(2, abs.getTimestamp());
                    pst.setDouble(3, abs.getPercentage());

                    pst.execute();
                }
            }else{
                PreparedStatement pst;
                String query;
                System.out.println("Updating");

                //Try to request abs, if not null, update

                if (conn.isValid(0)){
                    //Update
                    query = "UPDATE absenteeism SET percentage = ?, payperiod = ? WHERE payperiod = ? AND badgeid = ?";
                    pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    pst.setDouble(1, abs.getPercentage());
                    pst.setString(4, abs.getBadgeId());
                    pst.setTimestamp(2, abs.getTimestamp());
                    pst.setTimestamp(3, abs.getTimestamp());

                    pst.execute();
                    
                    System.out.println((new Absenteeism(abs.getBadgeId(), abs.getTimestampLong(), abs.getPercentage())).toString());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
