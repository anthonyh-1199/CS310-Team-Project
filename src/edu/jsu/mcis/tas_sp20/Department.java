package edu.jsu.mcis.tas_sp20;

public class Department {
    /*Initialize class variables*/
    private String description;
    private int departmentId, terminalId;
    
    /*Constructor method*/
    public Department(String description, int departmentid, int terminalid){
        this.description = description;
        this.departmentId = departmentid;
        this.terminalId = terminalid;
    }
    
    /*Setter methods*/
    public void setDescription(String description){
        this.description = description;
    }
    
    public void setDepartmentId(int departmentid){
        this.departmentId = departmentid;
    }
    
    public void setTerminalId(int terminalid){
        this.departmentId = terminalid;
    }
    
    /*Getter methods*/
    public String getDescription(){
        return this.description;
    }
    
    public int getDepartmentId(){
        return this.departmentId;
    }
    
    public int getTerminalId(){
        return this.terminalId;
    }

    /*Print-out methods*/
    @Override
    public String toString(){
        String s = "Department #" + departmentId + " (" + description + "); Terminal #" + terminalId;

        return s;
    }
}