package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private EditText editTextSettlement;

    private boolean confirm = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_settlement);

        textViewSettlementInformation = findViewById(R.id.textViewSettlementInformation);
        editTextSettlement = findViewById(R.id.editTextSettlement);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_settlement); //sem hodit z tyhle aktivity id recycler view
        mLayoutManager = new LinearLayoutManager(this); //nechat
        mRecyclerView.setLayoutManager(mLayoutManager); //nechat

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();
        listPassenger = new ArrayList<>();
        listPassenger = getAllPassengerFromDatabase();

        databaseConnector = new DatabaseConnector();
        databaseConnector.initializePassengerList();

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
        showAlert(position);
    }

    private void showAlert(final int position) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settle debt");
        builder.setMessage("Are you sure you want to settle this debt?");


        // Set click listener for alert dialog buttons
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // User clicked the Yes button
                        settleDebt(position);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // User clicked the No button
                        break;
                }
            }
        };

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Yes", dialogClickListener);

        // Set the alert dialog no button click listener
        builder.setNegativeButton("No",dialogClickListener);

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

    private void settleDebt(int position) {

        Passenger passenger = listPassenger.get(position);
        double newDebt = 0;
        Passenger updatedPassenger = new Passenger(passenger.getPassengerName(), newDebt);
        databaseConnector.savePassenger(updatedPassenger);
        databaseConnector.settlePassengersDebtToHisProfile(updatedPassenger);
    }


    @Override
    public void onButtonPayback(int position) {

        String valueEntered = String.valueOf(editTextSettlement.getText());

        if (!valueEntered.isEmpty() && valueEntered != null ){
            Passenger passenger = listPassenger.get(position);
            valueEntered.replace(",",".");

            double oldDebt = passenger.getDebt();
            double newDebt = oldDebt + Double.valueOf(valueEntered);

            Passenger updatedPassenger = new Passenger(passenger.getPassengerName(), newDebt);

            databaseConnector.savePassenger(updatedPassenger);
        }else{
            Toast.makeText(this,"Value is empty, please insert value first", Toast.LENGTH_SHORT).show();
        }


    }
}
