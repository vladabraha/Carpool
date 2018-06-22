package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import cz.uhk.fim.brahavl1.carpoolv4.Model.DatabaseConnector;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Passenger;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class Dashboard extends AppCompatActivity {

    private Button btnCarProfile;
    private Button btnDebtSettlement;
    private Button btnStartCarpool;
    private Button btnManageProfiles;
    private Button btnRides;
    private Button btnLogOut;
    private int resultCode2 = 1;
    private int resultCodeChoosePassengers = 2;

    private String fuelPrice;

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

        btnRides = (Button) findViewById(R.id.btnManageRides);

        btnRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, RideOverview.class);
                startActivity(intent);

            }
        });

        btnStartCarpool = (Button) findViewById(R.id.btnStartCarpool);

        btnStartCarpool.setOnClickListener(new View.OnClickListener() {
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

        databaseConnector.checkEmailInDatabse();


        btnDebtSettlement = findViewById(R.id.btnDebtSettlements);

        btnDebtSettlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDebtSettlement = new Intent(Dashboard.this, PassengerSettlement.class);
                startActivity(intentDebtSettlement);
            }
        });


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

                //zahajeni dalsi aktivity po vyberu vozidla
                ArrayList<Passenger> listPass = new ArrayList<>();
                Intent intentSelectPassengers = new Intent(Dashboard.this, PassengerChooser.class);
                intentSelectPassengers.putExtra("arg_key", listPass);
                startActivityForResult(intentSelectPassengers, resultCodeChoosePassengers);
                break;

            //Vraceni z CarChooser
            case 200:
                //vytahne objekt z intentu
                passengerList = (ArrayList<Passenger>) data.getSerializableExtra("arg_key");

                Intent intentSetFuelPrice = new Intent(Dashboard.this, FuelPrice.class);
                startActivityForResult(intentSetFuelPrice, resultCode);

                break;

                //vraceni z fuelPrice aktivity
            case 400:

                fuelPrice = data.getStringExtra("fuelPrice");
                Intent intentStartPool = new Intent(Dashboard.this, MapsActivity.class);
                startActivityForResult(intentStartPool, resultCode);

                break;

                //vraceni z maps activity
            case 300:
                //vytahne objekt z intentu
                String distance = data.getStringExtra("distance");
                String rideTime = data.getStringExtra("base");
                long drivingTime = Long.valueOf(rideTime);

                Log.d("TAG", "prisla cena za pohonne hmoty ve vysi " + fuelPrice);
                databaseConnector.saveRide(distance, drivingTime, passengerList, Double.valueOf(fuelPrice), Double.valueOf(fuelConsuption));

                break;
        }

    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}


