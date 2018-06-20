package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cz.uhk.fim.brahavl1.carpoolv4.Adapter.PassengerProfileRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Adapter.RideOverviewRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Passenger;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Ride;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class RideOverview extends AppCompatActivity implements RideOverviewRecyclerViewAdapter.onButtonRideChooseInterface{

    private FirebaseUser currentFirebaseUser;
    private DatabaseReference myRef;
    private String userID;
    private RideOverviewRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;


    private ArrayList<Ride> listRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_overview);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_ride_overview); //sem hodit z tyhle aktivity id recycler view
        mLayoutManager = new LinearLayoutManager(this); //nechat
        mRecyclerView.setLayoutManager(mLayoutManager); //nechat

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();

        listRide = new ArrayList<>();
        listRide = getAllRidesFromDatabase();

    }

    private ArrayList<Ride> getAllRidesFromDatabase() {
        myRef = FirebaseDatabase.getInstance().getReference("user")
                .child(userID).child("Ride");


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                listRide.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//
                   Ride rides = postSnapshot.getValue(Ride.class);
                    listRide.add(rides);

                }

                mAdapter = new RideOverviewRecyclerViewAdapter(listRide);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnButtonChooseListener(RideOverview.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // showText("nejde");
                // ...
            }
        };
        myRef.addValueEventListener(postListener);
        return listRide;
    }

    @Override
    public void onButtonDelete(ArrayList<Ride> listRides) {

    }
}
