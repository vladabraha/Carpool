package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import cz.uhk.fim.brahavl1.carpoolv4.Model.DatabaseConnector;
import cz.uhk.fim.brahavl1.carpoolv4.Model.LocationModel;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Passenger;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Ride;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class Dashboard extends NavigationDrawer{

    private int resultCodeChoosePassengers = 2;
    private String fuelPrice;
    private DatabaseConnector databaseConnector;
    private ArrayList<Passenger> passengerList;
    private String fuelConsuption;

    private DatabaseReference myRef;
    private String userID;
    private FirebaseUser currentFirebaseUser;

    private TextView textviewReachedKilometres;
    private TextView textviewTotalPrice;
    private TextView textviewTotalTime;
    private TextView textviewSavedMoney;

    private ArrayList<Ride> listRide = new ArrayList<>();

    private double totalKilometres = 0;
    private double totalPrice = 0;
    private double totalTime = 0;
    private double totalSavedMoney = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_dashboard, frameLayout);

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();

        textviewReachedKilometres = findViewById(R.id.textviewReachedKilometres);
        textviewTotalPrice = findViewById(R.id.textviewTotalPrice);
        textviewTotalTime = findViewById(R.id.textviewTotalTime);
        textviewSavedMoney = findViewById(R.id.textviewSavedMoney);

        textviewReachedKilometres.setText("");
        textviewTotalPrice.setText("");
        textviewTotalTime.setText("");
        textviewSavedMoney.setText("");

        getAllRidesFromDatabase();
        databaseConnector = new DatabaseConnector();
        databaseConnector.initializePassengerList();

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

                    Ride rides = postSnapshot.getValue(Ride.class);
                    listRide.add(rides);

                }
                generateStatistic();
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

    private void generateStatistic() {
        if (!listRide.isEmpty()){
            totalKilometres = 0;
            totalPrice = 0;
            totalTime = 0;
            totalSavedMoney = 0;
            int numberOfPassengers = 1;
            for(Ride ride : listRide){
                totalKilometres = totalKilometres + Double.valueOf(ride.getDistance());
                totalPrice = totalPrice + Double.valueOf(ride.getPrice());
                totalTime = totalTime + Double.valueOf(ride.getRideTime());
                numberOfPassengers = ride.getPassengers().size();
                totalSavedMoney = totalSavedMoney + (Double.valueOf(ride.getPrice()) / numberOfPassengers);
            }
            setNewTitles();
        }else{
            totalKilometres = 0;
            totalPrice = 0;
            totalTime = 0;
            totalSavedMoney = 0;
            setNewTitles();
        }
    }

    private void setNewTitles() {

        if(totalKilometres < 1000){
            textviewReachedKilometres.setText(String.format("%.2f", totalKilometres) + " metres");
        }else{
            totalKilometres = totalKilometres / 1000;
            textviewReachedKilometres.setText(String.format("%.2f", totalKilometres) + " kilometres");
        }
        textviewSavedMoney.setText(String.format("%.2f", totalSavedMoney));
        textviewTotalPrice.setText(String.format("%.2f", totalPrice));
        if (totalTime < 60){
            textviewTotalTime.setText(String.valueOf(String.format("%.2f", totalTime)) + " seconds");
        }else if (totalTime > 60 && totalTime < 3600){
            totalTime = totalTime / 60;
            textviewTotalTime.setText(String.valueOf(String.format("%.2f", totalTime)) + " minutes");
        }else if (totalTime > 3600 && totalTime < 86400){
            totalTime = totalTime / 3600;
            textviewTotalTime.setText(String.valueOf(String.format("%.2f", totalTime)) + " hours");
        }else{
            totalTime = totalTime / 86400;
            textviewTotalTime.setText(String.valueOf(String.format("%.2f", totalTime)) + " days");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                ArrayList<LocationModel> listPosition = new ArrayList<>();
                String distance = data.getStringExtra("distance");
                String rideTime = data.getStringExtra("base");
                listPosition = (ArrayList<LocationModel>) data.getSerializableExtra("positionList");
                for (LocationModel locationModel: listPosition){
                    Log.d("TAG", "v locmodelu je " + locationModel.getLatitude());
                }
                long drivingTime = Long.valueOf(rideTime);
                databaseConnector.saveRide(distance, drivingTime, passengerList, Double.valueOf(fuelPrice), Double.valueOf(fuelConsuption), listPosition);

                //information about price
                String dist = distance;
                String newDist = dist.replace(",",".");

                Log.i("TAG", newDist);
                double dist2 = Double.valueOf(newDist);
                double totalPrice = Double.valueOf(fuelConsuption) * (dist2 / 1000);
                Toast.makeText(this,"price for ride is: " + String.valueOf(String.valueOf(String.format("%.2f", totalPrice))),Toast.LENGTH_LONG).show();
                double priceForEach = totalPrice / (passengerList.size() + 1);
                Toast.makeText(this,"price for each passenger is: " + String.valueOf( String.valueOf(String.format("%.2f", priceForEach))),Toast.LENGTH_LONG).show();

                break;
        }

    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}


