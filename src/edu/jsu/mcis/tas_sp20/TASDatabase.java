package edu.jsu.mcis.tas_sp20;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;

public class TASDatabase {
    private Connection conn;

    public static void main(String[] args) {
//        TASDatabase db = new TASDatabase();
//        Punch punch = db.getPunch(3433);
    }

    public TASDatabase(){
        try {
            String server = "jdbc:mysql://localhost/TAS";
            String user = "admin";
            String pass = "password";
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            conn = DriverManager.getConnection(server, user, pass);

            //TODO: remove after testing
//            if (conn.isValid(0)) {
//                System.out.println("Connected successfully!");
//            } else {
//                System.out.println("Error connecting!");
//            }

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

            punch = new Punch(terminalID, badge, origTimeStamp, punchTypeID);

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
            query = "SELECT * FROM shift WHERE id=?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, ID);

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();
            java.sql.Time temp;

            int ShiftID = resultSet.getInt("id");
            String description = resultSet.getString("description");
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
            pst.setString(1, badge.getID());

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();
            java.sql.Time temp;

            int shiftID = resultSet.getInt("shiftid");
            query = "SELECT * FROM shift WHERE id=?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, shiftID);
            pst.execute();

            resultSet = pst.getResultSet();
            resultSet.first();

            String description = resultSet.getString("description");
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
            shift = new Shift(shiftID, description, start, stop, interval, gracePeriod, dock, lunchStart, lunchStop, lunchDeduct);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shift;
    }

    public int insertPunch(Punch p) {   //done?
        String badgeID = p.getBadge().getID();
        int terminalID = p.getTerminalid(), punchTypeID = p.getPunchtypeid();
        Long originalTimeStamp = p.getOriginaltimestamp();

        try {
            PreparedStatement pst;
            ResultSet resultSet;
            String query;


            if (conn.isValid(0)){
                query = "INSERT INTO punch (terminalid, badgeid, originaltimestamp, punchtypeid) VALUES (?, ?, ?, ?)";
                pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pst.setInt(1, terminalID);
                pst.setString(2, badgeID.toString());
                Timestamp timestamp = new Timestamp(originalTimeStamp);
                pst.setTimestamp(3, timestamp);
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
                resultSet.first();
                
                while(resultSet.next()){
                    int terminalId = resultSet.getInt("terminalid");
                    long originalTimeStamp = resultSet.getTimestamp("originaltimestamp").getTime();
                    int punchTypeId = resultSet.getInt("punchtypeid");
                    
                    Punch temp = new Punch(terminalId, badge, originalTimeStamp, punchTypeId);
                    dailyPunchList.add(temp);
                    
                    isPaired = !isPaired;
                    //lastId = resultSet.getInt
                }
                
                if(!isPaired){
                query = "SELECT * FROM punch WHERE badgeid = ? AND originaltimestamp LIKE ?";
                pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, badge.getID());
                pst.setString(2, timeLikeNext);
                
                pst.execute();
                resultSet = pst.getResultSet();
                resultSet.first();
                
                int terminalId = resultSet.getInt("terminalid");
                long originalTimeStamp = resultSet.getTimestamp("originaltimestamp").getTime();
                int punchTypeId = resultSet.getInt("punchtypeid");

                Punch temp = new Punch(terminalId, badge, originalTimeStamp, punchTypeId);
                dailyPunchList.add(temp);     
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dailyPunchList;
    }
}
