package cz.uhk.fim.brahavl1.carpoolv4.Model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Class for Ride record - Date (string), List<String> passenger, String distance, long time
 */
@SuppressLint("ParcelCreator")
public class Ride implements Parcelable {
    private String date;
    private List<Passenger> passengers;
    private String distance;
    private long rideTime;

    public Ride(String date, List<Passenger> passengers, String distance, long rideTime) {
        this.date = date;
        this.passengers = passengers;
        this.distance = distance;
        this.rideTime = rideTime;
    }

    public Ride(String date, String distance, long rideTime) {
        this.date = date;
        this.distance = distance;
        this.rideTime = rideTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setRideTime(long rideTime) {
        this.rideTime = rideTime;
    }

    public String getDate() {
        return date;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public String getDistance() {
        return distance;
    }

    public long getRideTime() {
        return rideTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
