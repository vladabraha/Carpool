package cz.uhk.fim.brahavl1.carpoolv4.Model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

/**
 * Database connector for saving data into database and deleting data in database
 */
public class DatabaseConnector {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userID;
    private FirebaseUser currentFirebaseUser;
    private ArrayList<Car> listCar;


    public DatabaseConnector() {

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();
        listCar = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
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

}
