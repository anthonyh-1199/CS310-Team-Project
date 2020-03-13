package edu.jsu.mcis.tas_sp20;

/**
 * This class is used to create a badge object containing the id of the badge
 * and the name of the badge owner.
 * @author csmit
 */
public class Badge {
    //Initialize Global Variables
    private String id, name;
    
    //Constructor
    /**
     * Constructor for Badge class, all badges must have an id and a name
     * @param id String id of the badge
     * @param name String name of the badge owner
     */
    public Badge(String id, String name){
        this.id = id;
        this.name = name;
    }
    
    //Getters
    /**
     * Getter for the id of this badge
     * @return String id of this badge
     */
    public String getId(){
        return this.id;
    }
    
    /**
     * Getter for the name of this badge owner
     * @return String name of this badge owner
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * Convert this badge to a String in format #id (name)
     * @return String format of this badge
     */
    @Override
    public String toString(){
        String s = "#" + id + " (" + name + ")";
        return s;
    }
}