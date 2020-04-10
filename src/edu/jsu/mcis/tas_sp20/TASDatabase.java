package edu.jsu.mcis.tas_sp20;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class TASDatabase {
    private Connection conn;

    public static final int DAY_IN_MILLIS = 86400000;
    public static final int WEEK_IN_MILLIS = 604800000;

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
            query = "SELECT * FROM shift WHERE id=?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, ID);

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();

            String description = resultSet.getString("description");

            DailySchedule schedule = getDailySchedule(ID);
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
            pst.setString(1, badge.getId());

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
            Long startTimestamp;
            Long endTimestamp;
            String badgeid;
            
            //Get the default value to return if no override is found
            shift = getShift(badge);
            
            //Loop through scheduleoverride and check for any applicable overrides
            while (resultSet.next()){
                startTimestamp = resultSet.getTimestamp("start").getTime();
                if (resultSet.getTimestamp("end") != null){
                    endTimestamp = resultSet.getTimestamp("end").getTime();
                } else {
                    endTimestamp = null;
                }
                badgeid = resultSet.getString("badgeid");

                if (timestamp >= startTimestamp && ((endTimestamp == null) || (timestamp <= endTimestamp))){
                    if ((badgeid == null) || (badgeid.equals( badge.getId() ))){
                        DailySchedule schedule = getDailySchedule(resultSet.getInt("dailyscheduleid"));
                        shift.setSchedule(schedule, resultSet.getInt("day"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shift;
    }
    
    private DailySchedule getDailySchedule(int ID) {
        DailySchedule schedule = null;
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

            int ShiftID = resultSet.getInt("id");
            LocalTime start = resultSet.getTime("start").toLocalTime();
            LocalTime stop = resultSet.getTime("stop").toLocalTime();
            int interval = resultSet.getInt("interval");
            int gracePeriod = resultSet.getInt("graceperiod");
            int dock = resultSet.getInt("dock");
            LocalTime lunchStart = resultSet.getTime("lunchstart").toLocalTime();
            LocalTime lunchStop = resultSet.getTime("lunchstop").toLocalTime();
            int lunchDeduct = resultSet.getInt("lunchdeduct");

            schedule = new DailySchedule(ShiftID, start, stop, interval, gracePeriod, dock, lunchStart, lunchStop, lunchDeduct);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return schedule;
    }

    public int insertPunch(Punch p) {
        GregorianCalendar ots = new GregorianCalendar();
        ots.setTimeInMillis(p.getOriginaltimestamp());
        String badgeID = p.getBadge().getId();
        int terminalID = p.getTerminalid();
        int punchTypeID = p.getPunchtypeid();

        try {
            PreparedStatement pst;
            ResultSet resultSet;
            String query;


            if (conn.isValid(0)){
                query = "INSERT INTO punch (terminalid, badgeid, originaltimestamp, punchtypeid) VALUES (?, ?, ?, ?)";
                pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pst.setInt(1, terminalID);
                pst.setString(2, badgeID);
                pst.setString(3, (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(ots.getTime()));
                pst.setInt(4, punchTypeID);

                pst.execute();
                resultSet = pst.getGeneratedKeys();
                resultSet.first();
                if (resultSet.getInt(1) > 0) {
                    return resultSet.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;

    }
    
    public ArrayList<Punch> getDailyPunchList(Badge badge, long ts){
        Timestamp timestamp = new Timestamp(ts);
        String timeLike = timestamp.toString().substring(0, 11) + "%";
        ArrayList<Punch> dailyPunchList = new ArrayList<>();

        try {
            PreparedStatement pst;
            ResultSet resultSet;
            String query;
            boolean isPaired = true;

            if (conn.isValid(0)){
                query = "SELECT * FROM punch WHERE badgeid = ? AND originaltimestamp LIKE ? ORDER BY ORIGINALTIMESTAMP ASC";
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
                    timestamp = new Timestamp(timestamp.getTime() +  DAY_IN_MILLIS);
                    timeLike = timestamp.toString().substring(0, 11) +  "%";

                    query = "SELECT * FROM punch WHERE badgeid = ? AND originaltimestamp LIKE ? ORDER BY ORIGINALTIMESTAMP ASC";
                    pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    pst.setString(1, badge.getId());
                    pst.setString(2, timeLike);

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
        long tsNew = TASLogic.getStartOfPayPeriod(ts);
        ArrayList<Punch> returnArray = new ArrayList<>();

        for(int i = 0; i < 7; i++){
            returnArray.addAll(getDailyPunchList(badge, tsNew + (this.DAY_IN_MILLIS * i)));
        }

        return returnArray;
    }

    public Absenteeism getAbsenteeism(String badgeId, long ts){
        Timestamp timestamp = new Timestamp(TASLogic.getStartOfPayPeriod(ts));
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
        Absenteeism ab = getAbsenteeism(abs.getBadgeId(), abs.getTimestampLong());

        try {
            PreparedStatement pst;
            String query;

            //Try to request abs, if null, insert
            if(ab == null){
                if (conn.isValid(0)){
                    //Insert
                    query = "INSERT INTO absenteeism (badgeid, payperiod, percentage) VALUES (?, ?, ?)";
                    pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    pst.setString(1, abs.getBadgeId());
                    pst.setTimestamp(2, abs.getTimestamp());
                    pst.setDouble(3, abs.getPercentage());

                    pst.execute();
                }
            }
            //Try to request abs, if not null, update
            else{
                if (conn.isValid(0)){
                    //Update
                    query = "UPDATE absenteeism SET percentage = ?, payperiod = ? WHERE payperiod = ? AND badgeid = ?";
                    pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    pst.setDouble(1, abs.getPercentage());
                    pst.setString(4, abs.getBadgeId());
                    pst.setTimestamp(2, abs.getTimestamp());
                    pst.setTimestamp(3, abs.getTimestamp());

                    pst.execute();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Employee getEmployee(String badgeID) {
        Employee employee = null;
        String query;
        PreparedStatement pst;
        ResultSet resultSet;

        try {

            query = "SELECT * FROM employee WHERE badgeid = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, badgeID);

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();

            String firstName = resultSet.getString("firstname");
            String middleName = resultSet.getString("middlename");
            String lastName = resultSet.getString("lastname");
            int employeeTypeID = resultSet.getInt("employeetypeid");
            int departmentID = resultSet.getInt("departmentid");
            int shiftID = resultSet.getInt("shiftid");
            long active = resultSet.getTimestamp("active").getTime();
            long inactive;
            if (resultSet.getTimestamp("inactive") != null){
                inactive = resultSet.getTimestamp("inactive").getTime();
            } else {
                inactive = 0;
            }
            int id = resultSet.getInt("id");

            
            query = "SELECT * FROM employeetype WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, employeeTypeID);

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.first();

            String employeeType = resultSet.getString("description");


            employee = new Employee(badgeID, firstName,  middleName, lastName, employeeTypeID,
             departmentID, shiftID, active, inactive, id, employeeType);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return employee;
    }

    public Department getDepartment(int id){
        Department department = null;
        String query;
        PreparedStatement pst;
        ResultSet resultSet;

        try {
            query = "SELECT * FROM department WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1,id);

            pst.execute();
            resultSet = pst.getResultSet();
            resultSet.next();

            String description = resultSet.getString("description");
            int terminalID = resultSet.getInt("terminalid");

            department = new Department(description, id, terminalID);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return department;
    }

    public ArrayList<HashMap> getBadgeSummaryData() {
        ArrayList<HashMap> data = null;
        try {
            data = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT badge.description AS badgedescription, department.description AS departmentdescription, badgeid, employeetype.description AS employeetypedescription FROM badge JOIN employee ON badge.id = employee.badgeid JOIN department ON employee.departmentid = department.id JOIN employeetype ON employee.employeetypeid = employeetype.id ORDER BY badgedescription");
            boolean resultsAvailable = stmt.execute();
            if (resultsAvailable) {
                ResultSet results = stmt.getResultSet();
                while (results.next()) {
                    HashMap<String, Object> row = new HashMap<>();
                    row.put("badgedescription", results.getString("badgedescription"));
                    row.put("departmentdescription", results.getString("departmentdescription"));
                    row.put("badgeid", results.getString("badgeid"));
                    row.put("employeetypedescription", results.getString("employeetypedescription"));
                    data.add(row);
                }
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        return data;
    }

    public ArrayList<HashMap> getDepartmentSummaryData(int id, long payPeriod) {
        String query;
        PreparedStatement pst;
        ResultSet resultSet;
        ArrayList<HashMap> data = null;

        try{
            query = "SELECT badgeid FROM employee WHERE departmentid = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);

            pst.execute();
            resultSet = pst.getResultSet();
            ArrayList<Employee> employees = new ArrayList<>();
            while (resultSet.next()) {
                employees.add(getEmployee(resultSet.getString("badgeid")));
            }

            for (Employee employee : employees) {
                HashMap<String, Object> row = new HashMap<>();
                Badge badge = getBadge(employee.getBadgeId());
                Shift shift = getShift(employee.getShiftId());
                Department department = getDepartment(id);

                ArrayList<Punch> punchList = getPayPeriodPunchList(badge, payPeriod);
                for (Punch punch : punchList) {
                    punch.adjust(shift);
                }

                double hours = TASLogic.calculateTotalMinutes(punchList, shift) / 60;

                String fullName = employee.getLastName() + ", " + employee.getFirstName() + " " + employee.getMiddleName();
                double regHours;
                double otHours;
                if (hours > 40) {
                    regHours = 40;
                    otHours = hours - 40;
                } else {
                    regHours = hours;
                    otHours = 0;
                }

                row.put("fullName", fullName);
                row.put("department", department.toString());
                row.put("employeeType", employee.getEmployeeType());
                row.put("shiftNum", ("Shift " + employee.getShiftId()));
                row.put("regHours", String.valueOf(regHours));  //TODO: format to 2nd decimal
                row.put("otHours", String.valueOf(otHours));    //TODO: same

                data.add(row);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

}

