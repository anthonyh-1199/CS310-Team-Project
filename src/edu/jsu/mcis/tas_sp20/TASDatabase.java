package edu.jsu.mcis.tas_sp20;

import java.sql.*;
import java.time.LocalTime;

public class TASDatabase {
    public static void main(String[] args) {

    }

    //TODO: Add to this database class?
    public static int insertPunch(Punch p) {
        //TODO: Check variable names
        Badge badgeId = p.getBadgeId();
        int terminalId = p.getTerminalId(), punchTypeId = p.getPunchTypeId();
        LocalTime originalTimeStamp = p.getOriginalTimeStamp;

        try {
            Connection conn;
            PreparedStatement pst;
            ResultSet resultSet;
            String query;
            String server = "jdbc:mysql://localhost/TAS_SP20";
            String user = "root";
            String pass = "root";
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            conn = DriverManager.getConnection(server, user, pass);

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
