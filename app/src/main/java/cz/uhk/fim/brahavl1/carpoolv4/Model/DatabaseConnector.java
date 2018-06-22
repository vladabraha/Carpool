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
    private ArrayList<UidEmail> uidEmailsList; //seznam emailu a uid
    private ArrayList<String> emailList; //seznam jenom emailu pro vyhledavani, zdali je uz v databazi


    public DatabaseConnector() {

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();
        listCar = new ArrayList<>();
        emailList = new ArrayList<>();
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
                passengersList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Passenger passenger = postSnapshot.getValue(Passenger.class);

                    passengersList.add(passenger);
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

        initializeUidEmails();

    }

    private void initializeUidEmails() {
        uidEmailsList = new ArrayList<>();

//        passengersList = passList;
        myRef = FirebaseDatabase.getInstance().getReference("uid");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                uidEmailsList.clear();
                emailList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    UidEmail uidEmail = postSnapshot.getValue(UidEmail.class);
                    uidEmailsList.add(uidEmail);
                    emailList.add(uidEmail.getEmail());
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

        //AKTUALIZACE DLUHU U PASAZERU
        int passengerCount = passengers.size();

        distance = distance.replaceAll(",","."); //prehozeni carky na tecku pro double

        double priceForTrip = (carConsuption / 100) * Double.valueOf(distance);

        double priceForEachPassenger = priceForTrip / passengerCount;


        for (String passenger : passengers){


            double originalDebt = getPassengerDebt(passenger);
            if(originalDebt == -1){
                return;
            }
            myRef = database.getReference("user");


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

        //ZAPSANI DLUHU OSTATNIM UZIVATELUM, POKUD JSOU
        for (Passenger passenger : passengerList){
            String name = passenger.getPassengerName();
            // zjistime u kazdeho, zdali ma ve jmene @ - coz znamena, ze uzivatel chtel propojit jmeno s dalsim uzivatelem
            // -1 se vrati, pokud tam neni, jinak vrati pozici, na ktery pozici symbolu se dany znak nacházi
            if (name.indexOf('@') != -1){
                //pokud je zadany email druheho cloveka v databazi (tzn. je zaregistrovany)
                if (emailList.contains(name)){
                    //pokud existuje, zjistime jeho uid a vlozime tam jeho dluh
                    for (UidEmail uidEmail : uidEmailsList){
                        String email = uidEmail.getEmail();
                        String uid;
                        if (email == name){
                            uid = uidEmail.getUid();
                            saveDebtToUserProfile(uid, priceForEachPassenger, userID, currentFirebaseUser.getEmail());
                        }

                    }
                }
            }
        }


    }

    //metoda, ktera vlozi dluh danemu pasazerovi, ktery ma ucet
    //uid je toho kdo dluzi a komu se to zapise, price je cena za jednoho u te dane jizdy,
    // userIDDriver je od koho ten dluh je (komu se to ma vratit) a driverEmail je nazev uzivatele, který se vlozi pasazerovi, ze mu dluzi
    private void saveDebtToUserProfile(String uidPassenger, double priceForEachPassenger, String userIDDriver, String driverEmail) {

        double price = priceForEachPassenger - priceForEachPassenger - priceForEachPassenger;
        Passenger passenger = new Passenger(driverEmail, price);
        DatabaseReference myRef = database.getReference("user");
        myRef.child(uidPassenger)
                .child("passengers")
                .child(driverEmail)
                .setValue(passenger);
    }


    //vrátí dluh daneho pasazera
    private double getPassengerDebt(String searchName) {
        for (Passenger passenger: this.passengersList ) {
            String name = passenger.getPassengerName();

            Log.i("TAG", "nyni se prohledava " + passenger.getPassengerName());
            if(name.equals(searchName)){
                Log.i("TAG", "nalezl se dluh " + passenger.getDebt());
                return Double.valueOf(passenger.getDebt());

            }
        }
        Log.i("TAG", "dluh se nenalezl ");
        return -1;
    }

    public void deleteRide(Ride ride) {

        DatabaseReference myRef = database.getReference("user");
        String rideDate = ride.getDate();
        myRef.child(currentFirebaseUser.getUid())
                .child("Ride")
                .child(rideDate)
                .setValue(null);

    }

    public void checkEmailInDatabse() {

        String email = currentFirebaseUser.getEmail();
        UidEmail uidEmail = new UidEmail(email, userID);

        DatabaseReference myRef = database.getReference("uid");
        myRef.child(currentFirebaseUser.getUid())
                .setValue(uidEmail);
        Log.i("TAG", "email je " + myRef.toString());
    }
}
