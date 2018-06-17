package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cz.uhk.fim.brahavl1.carpoolv4.Adapter.CarChooserRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Adapter.PassengerChooserRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Car;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Passenger;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class PassengerChooser extends AppCompatActivity implements PassengerChooserRecyclerViewAdapter.onButtonPassengerChooseInterface{

    private RecyclerView mRecyclerView;
    private PassengerChooserRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private DatabaseReference myRef;
    private String userID;
    private FirebaseUser currentFirebaseUser;

    //seznam, ktery budeme posilat do recycler view
    private ArrayList<Passenger> listPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_chooser);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_choose_passenger); //sem hodit z tyhle aktivity id recycler view

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this); //nechat
        mRecyclerView.setLayoutManager(mLayoutManager); //nechat

        //vytvorit seznam, kterej se bude predavat Recycler view
        //takze vytahnout data z databaze

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();

        listPassenger =  new ArrayList<>();

        listPassenger = getPassengersFromDatabase();
    }

    private ArrayList<Passenger> getPassengersFromDatabase(){
        //TODO CTENI Z DATABAZE
        myRef = FirebaseDatabase.getInstance().getReference("user")
                .child(userID).child("passengers");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Passenger passengers = postSnapshot.getValue(Passenger.class);
                    listPassenger.add(passengers);


                }
                mAdapter = new PassengerChooserRecyclerViewAdapter(listPassenger);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnButtonChooseListener(PassengerChooser.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // showText("nejde");

            }
        };
        myRef.addValueEventListener(postListener);
        return listPassenger;
    }

    @Override
    public void onButtonChoose(int position) {

    }

    @Override
    public void onCheckboxChange(ArrayList<Passenger> passengerCheckedList) {
        for (Passenger passenger : passengerCheckedList){
            Toast.makeText(this, passenger.getPassengerName(),Toast.LENGTH_SHORT).show();
        }

    }
}