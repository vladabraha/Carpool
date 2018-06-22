package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cz.uhk.fim.brahavl1.carpoolv4.Adapter.PassengerProfileRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Adapter.PassengerSettlementRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Adapter.RideOverviewRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Model.DatabaseConnector;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Passenger;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Ride;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class PassengerSettlement extends AppCompatActivity implements PassengerSettlementRecyclerViewAdapter.onButtonPassengerActionInterface{

    private FirebaseUser currentFirebaseUser;
    private DatabaseReference myRef;
    private String userID;
    private PassengerSettlementRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    private DatabaseConnector databaseConnector;
    private ArrayList<Passenger> listPassenger;

    private TextView textViewSettlementInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_settlement);

        textViewSettlementInformation = findViewById(R.id.textViewSettlementInformation);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_settlement); //sem hodit z tyhle aktivity id recycler view
        mLayoutManager = new LinearLayoutManager(this); //nechat
        mRecyclerView.setLayoutManager(mLayoutManager); //nechat

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();
        listPassenger = new ArrayList<>();
        listPassenger = getAllPassengerFromDatabase();

        databaseConnector = new DatabaseConnector();

    }

    private ArrayList<Passenger> getAllPassengerFromDatabase() {

        myRef = FirebaseDatabase.getInstance().getReference("user")
                .child(userID).child("passengers");


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                listPassenger.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

//                    Log.d("TAG", postSnapshot.getValue(Passenger.class).getPassengerName());
//
                    Passenger passengers = postSnapshot.getValue(Passenger.class);
                    listPassenger.add(passengers);

                }

                mAdapter = new PassengerSettlementRecyclerViewAdapter(listPassenger);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnButtonActionListener(PassengerSettlement.this);

                if (listPassenger.isEmpty()){
                    textViewSettlementInformation.setText("No passengers has been created yet");
                }else{
                    textViewSettlementInformation.setText("Choose action");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // showText("nejde");
                // ...
            }
        };
        myRef.addValueEventListener(postListener);
        return listPassenger;
    }

    @Override
    public void onButtonSettlement(int position) {

    }

    @Override
    public void onButtonPayback(int position) {

    }
}
