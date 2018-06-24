package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import cz.uhk.fim.brahavl1.carpoolv4.Model.LocationModel;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class RideDetail extends FragmentActivity implements OnMapReadyCallback {
    SupportMapFragment mapFragment;
    ArrayList<LocationModel> positionList = new ArrayList<>();
    private GoogleMap mMap;
    private String date;
    private String passengers;
    private String distance;
    private String rideTime;
    private String price;

    private TextView textViewDate;
    private TextView textViewPassengers;
    private TextView textViewDistance;
    private TextView textViewRideTime;
    private TextView textViewPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
            } else {
                positionList = (ArrayList<LocationModel>) extras.getSerializable("listPositions");
                date = extras.getString("date");
                passengers = extras.getString("passengers");
                distance = extras.getString("distance");
                rideTime = extras.getString("rideTime");
                price = extras.getString("price");
            }
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);      //vytahne z xml
        mapFragment.getMapAsync(this); //zavola on map ready

        initializeInformationTextView();
    }

    private void initializeInformationTextView() {
        textViewDate = findViewById(R.id.textViewDate);
        textViewPassengers = findViewById(R.id.textViewPassengers);
        textViewDistance = findViewById(R.id.textViewDistance);
        textViewRideTime = findViewById(R.id.textViewRideTime);
        textViewPrice = findViewById(R.id.textViewPrice);

        textViewDate.setText("Date of ride: " + date);

        StringBuilder pass = new StringBuilder(passengers);
        pass.delete(0,2);
        String newName = pass.toString();
        textViewPassengers.setText("Passengers on ride: " + newName);
        textViewDistance.setText("Distance: " + distance);
        if(Double.valueOf(rideTime) < 60){
            textViewRideTime.setText("Ride time in seconds: " + rideTime);
        } else if (Double.valueOf(rideTime) > 60 && Double.valueOf(rideTime) < 3600){
            Double time = Double.valueOf(rideTime);
            time = time / 60;
            textViewRideTime.setText("Ride time in minutes: " + String.valueOf(time));
        } else if (Double.valueOf(rideTime) > 36000){
            Double time = Double.valueOf(rideTime);
            time = time / 3600;
            textViewRideTime.setText("Ride time in hours: " + time);
        }
        double newPrice = Double.valueOf(price);
        textViewPrice.setText("Price for ride: " + String.format("%.2f", newPrice));

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker to your position and move the camera
        double currentLat;
        double currentLng;
        double previousLat = positionList.get(0).getLatitude(); //vezmeme prvni body
        double previousLng = positionList.get(0).getLongitude();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(previousLat, previousLng), 12.5f));

        for (LocationModel location : positionList) {
            currentLat = location.getLatitude();
            currentLng = location.getLongitude();

            mMap.addPolyline(new PolylineOptions().clickable(false).add( //vykreslime acru
                    new LatLng(previousLat, previousLng),
                    new LatLng(currentLat, currentLng)
            ));

            previousLat = currentLat; //prohodime
            previousLng = currentLng;
        }
    }

}
