package edu.jsu.mcis.tas_sp20;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Employee {
    /*Initialize class variables*/
    private String badgeId, firstName, middleName, lastName, employeeType;
    private int employeeTypeId, departmentId, shiftId, employeeId;
    private Long active, inactive;
    
    /*Constructor method*/
    public Employee(String badgeid, String firstname, String middlename, String lastname, int employeetypeid, 
        int departmentid, int shiftid, long active, long inactive, int id, String employeetype){
        this.badgeId = badgeid;
        this.firstName = firstname;
        this.middleName = middlename;
        this.lastName = lastname;
        this.employeeTypeId = employeetypeid;
        this.departmentId = departmentid;
        this.shiftId = shiftid;
        this.active = active;
        this.inactive = inactive;
        this.employeeId = id;
        this.employeeType = employeetype;
    }
    
    /*Setter methods*/
    public void setBadgeId(String badgeid){
        this.badgeId = badgeid;
    }
    
    public void setFirstName(String firstname){
        this.firstName = firstname;
    }
    
    public void setMiddleName(String middlename){
        this.middleName = middlename;
    }
    
    public void setLastName(String lastname){
        this.lastName = lastname;
    }
    
    public void setEmployeeTypeId(int employeetypeid){
        this.employeeTypeId = employeetypeid;
    }
    
    public void setDepartmentId(int departmentid){
        this.departmentId = departmentid;
    }
    
    public void setShiftId(int shiftid){
        this.shiftId = shiftid;
    }
    
    public void setActive(long active){
        this.active = active;
    }
    
    public void setInactive(long inactive){
        this.inactive = inactive;
    }
    
    public void setEmployeeId(int employeeid){
        this.employeeId = employeeid;
    }
    
    /*Getter methods*/
    public String getBadgeId(){
        return this.badgeId;
    }
    
    public String getFirstName(){
        return this.firstName;
    }
    
    public String getMiddleName(){
        return this.middleName;
    }
    
    public String getLastName(){
        return this.lastName;
    }
    
    public int getEmployeeTypeId(){
        return this.employeeTypeId;
    }
    
    public int getDepartmentId(){
        return this.departmentId;
    }
    
    public int getShiftId(){
        return this.shiftId;
    }
    
    public long getActive(){
        return this.active;
    }
    
    public long getInactive(){
        return this.inactive;
    }
    
    public int getEmployeeId(){
        return this.employeeId;
    }
    
    public String getEmployeeType(){
        return this.employeeType;
    }

    /*Print-out methods*/
    @Override
    public String toString(){
        String s = lastName + ", " + firstName + " " + middleName + " (#" + badgeId + "); Type " + employeeTypeId + " (" + employeeType + 
            "); Department " + departmentId + "; Shift " + shiftId + "; Active: ";

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date d = new Date(active);
        
        s += (df.format(d)) + "; Inactive: ";
        
        if (inactive == 0){
            s += "none";
        } else {
            d = new Date(inactive);
            s += (df.format(d));
        }

        return s;
    }
}