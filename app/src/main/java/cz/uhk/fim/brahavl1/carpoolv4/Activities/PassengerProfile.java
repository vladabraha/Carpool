package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import cz.uhk.fim.brahavl1.carpoolv4.Model.DatabaseConnector;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Passenger;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class PassengerProfile extends AppCompatActivity {

    private Button btnSavePassenger;
    private EditText editTextPassenger;
    private DatabaseConnector databaseConnector;
    private Passenger passenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile);

        databaseConnector = new DatabaseConnector();

        btnSavePassenger = findViewById(R.id.btnSavePassenger);

        editTextPassenger = findViewById(R.id.editPassengerName);


        btnSavePassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passenger = new Passenger(String.valueOf(editTextPassenger.getText()),0);
                databaseConnector.savePassenger(passenger);
            }
        });
    }
}
