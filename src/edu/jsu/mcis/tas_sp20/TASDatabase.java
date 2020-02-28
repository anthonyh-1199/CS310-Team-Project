package edu.jsu.mcis.tas_sp20;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalTime;

public class TASDatabase {
    private Connection conn;

    public static void main(String[] args) {

    }

    public void TASDatabase(){
        try {
            String server = "jdbc:mysql://localhost/TAS_SP20";
            String user = "db_user";    //TODO: team pick new login values
            String pass = "CS488";
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

    public Punch getPunch(int id) { //Done?
        Punch punch = null;
        try {
            String query;
            PreparedStatement pstPunch, pstBadge;
            ResultSet resultSet;
//            ResultSetMetaData metaData;
//            int colCount;

            query = "SELECT * FROM punch WHERE id=?";
            pstPunch = conn.prepareStatement(query);
            //TODO: Pass as string?
            pstPunch.setInt(1, id);

            pstPunch.execute();
            resultSet = pstPunch.getResultSet();

            //TODO: Check types
//            resultSet.next();
            int terminalId = resultSet.getInt(2);
            String badgeId = resultSet.getString(3);
            long origTimeStamp = resultSet.getLong(4);
            int punchTypeId = resultSet.getInt(5);

            query = "SELECT * FROM badge WHERE id=?";
            pstBadge = conn.prepareStatement(query);
            pstBadge.setString(1, badgeId);
            pstBadge.execute();
            resultSet = pstBadge.getResultSet();

            Badge badge = new Badge(resultSet.getString(2));

            punch = new Punch(terminalId, badge, origTimeStamp, punchTypeId);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return punch;
    }

    public Badge getBadge(String id) {  //DONE?
        Badge badge = null;
        String query;
        PreparedStatement pst;
        ResultSet resultSet;

        try {
            query = "SELECT * FROM badge WHERE id=?";
            pst = conn.prepareStatement(query);
            pst.setString(1, id);
            pst.execute();

            resultSet = pst.getResultSet();
            badge = new Badge(id, resultSet.getString(2));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return badge;
    }

    //TODO: id type
    public Shift getShift(String id) {
        Shift shift = null;
        String query;
        PreparedStatement pst;
        ResultSet resultSet;

        try {
            query = "SELECT * FROM shift WHERE id=?";
            pst = conn.prepareStatement(query);
            pst.setString(1, id);
            pst.execute();

            resultSet = pst.getResultSet();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: Add to this database class?
    public int insertPunch(Punch p) {
        //TODO: Check variable names
        Badge badgeId = p.getBadgeId();
        int terminalId = p.getTerminalId(), punchTypeId = p.getPunchTypeId();
        LocalTime originalTimeStamp = p.getOriginalTimeStamp;

        try {
            PreparedStatement pst;
            ResultSet resultSet;
            String query;


            if (conn.isValid(0)){
                query = "INSERT INTO punch (terminalid, badgeid, originaltimestamp, punchtypeid) VALUES (?, ?, ?, ?)";
                pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                //TODO: All strings?
                pst.setString(1, String.valueOf(terminalId));
                pst.setString(2, badgeId.toString());
                pst.setString(3, originalTimeStamp.toString()); //TODO: Default toString()?
                pst.setString(4, String.valueOf(punchTypeId));

                pst.execute();
                resultSet = pst.getGeneratedKeys();
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
