package edu.jsu.mcis.tas_sp20;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;

public class TASDatabase {
    private Connection conn;

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

            int terminalID = resultSet.getInt("terminalid");
            String badgeID = resultSet.getString("badgeid");
            long origTimeStamp = resultSet.getTimestamp("originaltimestamp").getTime();
            int punchTypeID = resultSet.getInt("punchtypeid");

            query = "SELECT * FROM badge WHERE id=?";
            pstBadge = conn.prepareStatement(query);
            pstBadge.setString(1, badgeID);
            pstBadge.execute();
            resultSet = pstBadge.getResultSet();
            resultSet.first();

            Badge badge = new Badge(resultSet.getString("id"), resultSet.getString("description"));

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
            badge = new Badge(ID, resultSet.getString("description"));

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
            query = "SELECT * FROM dailyschedule WHERE id=?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, ID);

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();
            java.sql.Time temp;

            int ShiftID = resultSet.getInt("id");
            temp = resultSet.getTime("start");
            LocalTime start = temp.toLocalTime();
            temp = resultSet.getTime("stop");
            LocalTime stop = temp.toLocalTime();
            int interval = resultSet.getInt("interval");
            int gracePeriod = resultSet.getInt("graceperiod");
            int dock = resultSet.getInt("dock");
            temp = resultSet.getTime("lunchstart");
            LocalTime lunchStart = temp.toLocalTime();
            temp = resultSet.getTime("lunchstop");
            LocalTime lunchStop = temp.toLocalTime();
            int lunchDeduct = resultSet.getInt("lunchdeduct");
            
            query = "SELECT * FROM shift WHERE id=?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, ID);

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();
            
            String description = resultSet.getString("description");
            
            DailySchedule schedule = new DailySchedule(ShiftID, start, stop, interval, gracePeriod, dock, lunchStart, lunchStop, lunchDeduct);
            shift = new Shift(description, schedule);

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
            pst.setString(1, badge.getID());

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();

            int shiftID = resultSet.getInt("shiftid");
            
            shift = getShift(shiftID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shift;
    }
    
    public Shift getShift(Badge badge, Long timestamp){
        Shift shift = null;
        String query;
        PreparedStatement pst;
        ResultSet resultSet;

        try {
            query = "SELECT * FROM scheduleoverride";
            pst = conn.prepareStatement(query);

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();
            Long startTimestamp;
            Long endTimestamp;
            String badgeid;
            
            //Get the default value to return if none is found
            shift = getShift(badge);
            
            //Loop through scheduleoverride and check for any applicable overrides
            while (resultSet.next()){
                startTimestamp = resultSet.getTimestamp("start").getTime();
                endTimestamp = resultSet.getTimestamp("start").getTime();
                badgeid = resultSet.getString("badgeid");
                
                if ((timestamp >= startTimestamp) && (timestamp <= endTimestamp)
                && ((badgeid == null) || (badgeid.equals( badge.getID())))){
                    shift = getShift(resultSet.getInt("dailyscheduleid"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shift;
    }

    public int insertPunch(Punch p) {
        GregorianCalendar ots = new GregorianCalendar();
        ots.setTimeInMillis(p.getOriginaltimestamp());
        String badgeID = p.getBadge().getID();
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
        
        Timestamp nextDay = new Timestamp(ts + 86400000);
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
                pst.setString(1, badge.getID());
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
                pst.setString(1, badge.getID());
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
}
