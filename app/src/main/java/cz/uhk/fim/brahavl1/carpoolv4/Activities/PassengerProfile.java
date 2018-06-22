package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


import cz.uhk.fim.brahavl1.carpoolv4.Adapter.PassengerProfileRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Car;
import cz.uhk.fim.brahavl1.carpoolv4.Model.DatabaseConnector;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Passenger;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class PassengerProfile extends AppCompatActivity implements PassengerProfileRecyclerViewAdapter.onButtonPassengerDeleteInterface{

    private Button btnSavePassenger;
    private EditText editTextPassenger;
    private DatabaseConnector databaseConnector;
    private Passenger passenger;

    private FirebaseUser currentFirebaseUser;
    private DatabaseReference myRef;
    private String userID;
    private PassengerProfileRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    private ArrayList<Passenger> listPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_manage_passengers); //sem hodit z tyhle aktivity id recycler view
        mLayoutManager = new LinearLayoutManager(this); //nechat
        mRecyclerView.setLayoutManager(mLayoutManager); //nechat

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();
        listPassenger = new ArrayList<>();
        listPassenger = getAllPassengerFromDatabase();

        databaseConnector = new DatabaseConnector();

        btnSavePassenger = findViewById(R.id.btnSavePassenger);

        editTextPassenger = findViewById(R.id.editPassengerName);


        btnSavePassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vymena tecky za | protoze tam tecka byt nesmi
                String name = String.valueOf(editTextPassenger.getText());
                String newName = name.replace(".","|");
                Log.d("TAG","jmeno je " + newName);
                passenger = new Passenger(newName,0);
                databaseConnector.savePassenger(passenger);

                editTextPassenger.setText("");
            }
        });
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

                mAdapter = new PassengerProfileRecyclerViewAdapter(listPassenger);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnButtonDeleteListener(PassengerProfile.this);
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
    public void onButtonDelete(int position) {

        Passenger passenger = listPassenger.get(position);
        databaseConnector.deletePassenger(passenger);
        Toast.makeText(this, "Passenger profiles updated", Toast.LENGTH_SHORT ).show();


    }
}
