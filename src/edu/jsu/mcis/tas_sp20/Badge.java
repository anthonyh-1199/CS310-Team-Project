package edu.jsu.mcis.tas_sp20;

public class Badge {
    //Initialize Global Variables
    private String id, name;
    
    //Constructor
    public Badge(String id, String name){
        this.id = id;
        this.name = name;
    }
    
    //Getters
    public String getID(){
        return this.id;
    }
    
    public String getName(){
        return this.name;
    }
    
    @Override
    public String toString(){
        String s = "#" + id + " (" + name + ")";
        return s;
    }
}