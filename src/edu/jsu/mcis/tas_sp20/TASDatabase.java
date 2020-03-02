package edu.jsu.mcis.tas_sp20;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalTime;

public class TASDatabase {
    private Connection conn;

    public static void main(String[] args) {
        TASDatabase db = new TASDatabase();
    }

    public TASDatabase(){
        try {
            String server = "jdbc:mysql://localhost/TAS";
            String user = "admin";
            String pass = "password";
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            conn = DriverManager.getConnection(server, user, pass);

            //TODO: remove after testing
            if (conn.isValid(0)) {
                System.out.println("Connected successfully!");
            } else {
                System.out.println("Error connecting!");
            }

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
            long origTimeStamp = resultSet.getLong(4);
            int punchTypeID = resultSet.getInt(5);

            query = "SELECT * FROM badge WHERE id=?";
            pstBadge = conn.prepareStatement(query);
            pstBadge.setString(1, badgeID);
            pstBadge.execute();
            resultSet = pstBadge.getResultSet();
            resultSet.first();

            Badge badge = new Badge(resultSet.getString(1), resultSet.getString(2));

//            punch = new Punch(terminalID, badge, origTimeStamp, punchTypeID);
            punch = new Punch(badge, terminalID, origTimeStamp, punchTypeID);


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
            pst.setString(1, badge.getID());

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

    public int insertPunch(Punch p) {   //done?
        String badgeID = p.getBadge().getID();
        int terminalID = p.getTerminalID(), punchTypeID = p.getPunchTypeID();
        Long originalTimeStamp = p.getOriginalTimeStamp();

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
}
