package cz.uhk.fim.brahavl1.carpoolv4.Model;

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
import java.util.Calendar;
import java.util.Date;

import cz.uhk.fim.brahavl1.carpoolv4.Activities.CarChooser;
import cz.uhk.fim.brahavl1.carpoolv4.Adapter.CarChooserRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Adapter.PassengerChooserRecyclerViewAdapter;

/**
 * Database connector for saving data into database and deleting data in database
 */
public class DatabaseConnector {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userID;
    private FirebaseUser currentFirebaseUser;
    private ArrayList<Car> listCar;


    private PassengerChooserRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Passenger> passengersList;


    public DatabaseConnector() {

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();
        listCar = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
    }

    public void initializePassengerList(){
        passengersList = new ArrayList<>();

//        passengersList = passList;
        myRef = FirebaseDatabase.getInstance().getReference("user")
                .child(userID).child("passengers");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Passenger passenger = postSnapshot.getValue(Passenger.class);

                    passengersList.add(passenger);
                    Log.i("TAG", "seznam se nafoukl na:  " + passengersList.size());
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

    }


    public void saveCarProfile(Car car) {
        String carName = car.getName();
        DatabaseReference myRef = database.getReference("user");
        //jednotlive zanorovani se provadi pomoci .child
        myRef.child(currentFirebaseUser.getUid())
                .child("carProfile")
                .child(carName) //pokud by bylo vice aut, tak se pak bude moct vybirat
                .setValue(car); //profil auta

    }

    public void deleteCarProfile(String carName) {
        DatabaseReference myRef = database.getReference("user");
        //jednotlive zanorovani se provadi pomoci .child
        myRef.child(currentFirebaseUser.getUid())
                .child("carProfile")
                .child(carName)
                .setValue(null); //null vymaze danou hodnotu
    }

    public void savePassenger(Passenger passenger) {

        DatabaseReference myRef = database.getReference("user");
        String passengerName = passenger.getPassengerName();

        myRef.child(currentFirebaseUser.getUid())
                .child("passengers")
                .child(passengerName)
                .setValue(passenger);

    }

    public void deletePassenger(Passenger passenger) {

        DatabaseReference myRef = database.getReference("user");
        String passengerName = passenger.getPassengerName();

        myRef.child(currentFirebaseUser.getUid())
                .child("passengers")
                .child(passengerName)
                .setValue(null);

    }

    public void saveRide(String distance, long rideTime, ArrayList<Passenger> passengerList, double fuelPrice, double carConsuption) {
        //todo pridat pasazerum dluh za cestu
        //ulozeni cesty do databaze
        Date currentTime = Calendar.getInstance().getTime();
        String time = String.valueOf(currentTime);

        //vyfiltruju jenom nazvy, aby se ukladaly jmena pasazeru do databaze bez dluhu
        ArrayList<String> passengers = new ArrayList<>();
        for (Passenger passenger : passengerList){
            passengers.add(passenger.getPassengerName());
        }

        Ride ride = new Ride(time, passengers, distance, rideTime);

        DatabaseReference myRef = database.getReference("user");

        myRef.child(currentFirebaseUser.getUid())
                .child("Ride")
                .child(time)
                .setValue(ride);

        //aktualizace dluhu u pasazeru
        int passengerCount = passengers.size();
        Log.i("TAG", "pocet pasazeru je " + String.valueOf(passengerCount));

        distance = distance.replaceAll(",","."); //prehozeni carky na tecku pro double

        double priceForTrip = (carConsuption / 100) * Double.valueOf(distance);
        Log.i("TAG", "cena za vsechny je " + String.valueOf(priceForTrip));
        double priceForEachPassenger = priceForTrip / passengerCount;
        Log.i("TAG", "cena za jednoho je " + String.valueOf(priceForEachPassenger));

        for (String passenger : passengers){

            Log.i("TAG", "aktualizuje se počet pasazeru:  " + passengers.size());
            double originalDebt = getPassengerDebt(passenger);
            if(originalDebt == -1){
                return;
            }
            myRef = database.getReference("user");

            //TODO PŘIDĚLAT MAZANI PASAZERU
            Passenger updatedPassenger = new Passenger(passenger, (originalDebt + priceForEachPassenger));

            myRef.child(currentFirebaseUser.getUid())
                    .child("passengers")
                    .child(passenger)
                    .setValue(null);

            myRef.child(currentFirebaseUser.getUid())
                    .child("passengers")
                    .child(passenger)
                    .setValue(updatedPassenger);


        }

    }

    //vrátí dluh daneho pasazera
    private double getPassengerDebt(String searchName) {
        Log.i("TAG", "velikost prohledavaneho pole je " + passengersList.size());
        Log.i("TAG", "hledame " + searchName);
        for (Passenger passenger: this.passengersList ) {
            String name = passenger.getPassengerName();

            Log.i("TAG", "nyni se prohledava " + passenger.getPassengerName());
            if(!name.equals(searchName)){
                Log.i("TAG", "nalezl se dluh " + passenger.getDebt());
                return Double.valueOf(passenger.getDebt());

            }
        }
        Log.i("TAG", "dluh se nenalezl ");
        return -1;
    }
}
