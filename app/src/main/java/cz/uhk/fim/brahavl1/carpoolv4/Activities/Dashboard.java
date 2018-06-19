package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import cz.uhk.fim.brahavl1.carpoolv4.Model.DatabaseConnector;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Passenger;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Ride;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class Dashboard extends AppCompatActivity {

    private Button btnCarProfile;
    private Button btnStartCarPool;
    private Button btnSelectCar;
    private Button btnManageProfiles;
    private Button btnSelectPassengers;
    private Button btnLogOut;
    private int resultCode;
    private int resultCode2 = 1;
    private int resultCodeChoosePassengers = 2;

    private DatabaseConnector databaseConnector;
    private ArrayList<Passenger> passengerList;
    private String fuelConsuption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseConnector = new DatabaseConnector();
        databaseConnector.initializePassengerList();

        btnCarProfile = (Button) findViewById(R.id.btnCarProfile);

        btnCarProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, CarProfile.class);
                startActivity(intent);
            }
        });

//        btnStartCarPool = (Button) findViewById(R.id.btnStartCarPool);
//
//        btnStartCarPool.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentStartPool = new Intent(Dashboard.this, MapsActivity.class);
//                startActivityForResult(intentStartPool, resultCode);
//
//            }
//        });

        btnSelectCar = (Button) findViewById(R.id.btnSelectCar);

        btnSelectCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChooseCar = new Intent(Dashboard.this, CarChooser.class);
                startActivityForResult(intentChooseCar, resultCode2);
            }
        });


        btnManageProfiles = findViewById(R.id.btnManagePassengers);

        btnManageProfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentManageProfiles = new Intent(Dashboard.this, PassengerProfile.class);
                startActivity(intentManageProfiles);
            }
        });

        btnLogOut = findViewById(R.id.btnLogout);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tohle posle zpátky - v tomhle pripade zvoleny auto
                Intent resultIntent = new Intent();
                setResult(100, resultIntent);
                finish();
            }
        });

//        btnSelectPassengers = findViewById(R.id.btnSelectPassenger);
//
//        btnSelectPassengers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                ArrayList<Passenger> listPass = new ArrayList<>();
////                Intent intentSelectPassengers = new Intent(Dashboard.this, PassengerChooser.class);
////                intentSelectPassengers.putExtra("arg_key", listPass);
////                startActivityForResult(intentSelectPassengers, resultCodeChoosePassengers);
//
//                ArrayList<Passenger> testList = new ArrayList<>();
//
//                testList.add(new Passenger("Martin",0));
//                testList.add(new Passenger("Miloš Gabrle",30));
//                databaseConnector.saveRide("13.7", 25, testList, 30.0, Double.valueOf(35.3));
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        //tady budu switchovat akce podle toho ze ktery aktivity se vracim
        switch (resultCode) {
            //pokud je akce zrusena uzivatelem
            case 0:
                Toast.makeText(this,"action was cancelled", Toast.LENGTH_SHORT).show();
                break;
            //Vraceni z CarChooser
            case 100:
                fuelConsuption = data.getStringExtra("car");

                Log.d("TAG", fuelConsuption);
//                Toast.makeText(this, fuelConsuption, Toast.LENGTH_SHORT).show();

                //zahajeni dalsi aktivity po vyberu vozidla
                ArrayList<Passenger> listPass = new ArrayList<>();
                Intent intentSelectPassengers = new Intent(Dashboard.this, PassengerChooser.class);
                intentSelectPassengers.putExtra("arg_key", listPass);
                startActivityForResult(intentSelectPassengers, resultCodeChoosePassengers);
                break;

            //Vraceni z CarChooser
            case 200:
                //vytahne objekt z intentu
//                ArrayList<Passenger> passengerList = (ArrayList<Passenger>) data.getSerializableExtra("arg_key");
                passengerList = (ArrayList<Passenger>) data.getSerializableExtra("arg_key");

                for (Passenger passenger : passengerList) {
                    Log.d("TAG", passenger.getPassengerName().toString());
                }
                Intent intentStartPool = new Intent(Dashboard.this, MapsActivity.class);
                startActivityForResult(intentStartPool, resultCode);
                //TODO UNCHECK
//                databaseConnector.initializePassengerList();

                break;

                //vraceni z maps activity
            case 300:
                //vytahne objekt z intentu

                String distance = data.getStringExtra("distance");
                String rideTime = data.getStringExtra("base");
                long drivingTime = Long.valueOf(rideTime);

//                Log.d("TAG", "prisla vzdalenost  " + distance + " a ridetime " + rideTime);

                //TODO DODELAT CENU ZA POHONNE HMOTY
                databaseConnector.saveRide(distance, drivingTime, passengerList, 300000.0, Double.valueOf(fuelConsuption));


//                ArrayList<Passenger> testList = new ArrayList<>();
//                testList.add(new Passenger("Karel Dvořák",0));
//                databaseConnector.saveRide("13.7", 25, testList, 30.0, Double.valueOf(35.3));

                break;

        }

    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}


