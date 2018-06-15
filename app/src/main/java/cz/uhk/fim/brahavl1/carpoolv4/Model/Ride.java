package cz.uhk.fim.brahavl1.carpoolv4.Model;

import java.util.List;

/**
 * Class for Ride record - Date (string), List<String> passenger, String distance, long time
 */
public class Ride {
    private String date;
    private List<String> passengers;
    private String distance;
    private long time;

    public Ride(String date, List<String> passengers, String distance, long time) {
        this.date = date;
        this.passengers = passengers;
        this.distance = distance;
        this.time = time;
    }

    public Ride(String date, String distance, long time) {
        this.date = date;
        this.distance = distance;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
