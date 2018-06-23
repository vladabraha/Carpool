package cz.uhk.fim.brahavl1.carpoolv4.Model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for Ride record - Date (string), List<String> passenger, String distance, long time
 */
@SuppressLint("ParcelCreator")
public class Ride implements Parcelable {
    private String date;
    private List<String> passengers;
    private String distance;
    private long rideTime;
    private double price;
    private ArrayList<LocationModel> route;

    public Ride() {
    }

    public Ride(String date, List<String> passengers, String distance, long rideTime, double price, ArrayList<LocationModel> route) {
        this.date = date;
        this.passengers = passengers;
        this.distance = distance;
        this.rideTime = rideTime;
        this.price = price;
        this.route = route;
    }

    public Ride(String date, String distance, long rideTime, ArrayList<LocationModel> route) {
        this.date = date;
        this.distance = distance;
        this.rideTime = rideTime;
        this.route = route;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<LocationModel> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<LocationModel> route) {
        this.route = route;
    }

    public Ride(String date, List<String> passengers, String distance, long rideTime) {
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

    public void setPassengers(List<String> passengers) {
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

    public List<String> getPassengers() {
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
