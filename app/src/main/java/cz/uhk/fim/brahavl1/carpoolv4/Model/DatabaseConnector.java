package cz.uhk.fim.brahavl1.carpoolv4.Model;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

/**
 * Database connector for saving data into database
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
        //vytvoreni objektu auta
        //TODO predelat ukladani do samostane metoddy
        //tyhle 2 radky vloží něco do databaze
        String carName = car.getName();
        DatabaseReference myRef = database.getReference("user");
        //jednotlive zanorovani se provadi pomoci .child
        myRef.child(currentFirebaseUser.getUid())
                .child("carProfile")
                .child(carName) //pokud by bylo vice aut, tak se pak bude moct vybirat
                .setValue(car); //profil auta

    }

    public void deleteCarProfile(String carName) {

        //TODO predelat ukladani do samostane metoddy
        //tyhle 2 radky vloží něco do databaze
        DatabaseReference myRef = database.getReference("user");
        //jednotlive zanorovani se provadi pomoci .child
        myRef.child(currentFirebaseUser.getUid())
                .child("carProfile")
                .child(carName) //pokud by bylo vice aut, tak se pak bude moct vybirat
                .setValue(null); //profil auta
    }

}
