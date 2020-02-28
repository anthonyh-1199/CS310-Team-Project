package edu.jsu.mcis.tas_sp20;

public class Shift {
    // TODO: Determine data types
    String description;
    int id, dock, gracePeriod, interval, lunchDeduct;
    Long start, stop, lunchStart, lunchStop, lunchDuration;

    Shift() {
        description = null;
        id = 0; dock = 0; gracePeriod = 0; interval = 0; lunchDeduct = 0;
        start = null; stop = 0; lunchStart = 0; lunchStop = 0; lunchDuration = 0;

        // Setter methods

    }
}
