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
                    String mail = uidEmail.getEmail();
                    mail = mail.replace(".","|");
                    emailList.add(mail);
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
        String newName = passengerName.replace(".","|");

        myRef.child(currentFirebaseUser.getUid())
                .child("passengers")
                .child(newName)
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

    public void saveRide(String distance, long rideTime, ArrayList<Passenger> passengerList, double fuelPrice, double carConsuption, ArrayList<LocationModel> listPositions) {
        //ulozeni cesty do databaze
        Date currentTime = Calendar.getInstance().getTime();
        String time = String.valueOf(currentTime);

        //vyfiltruju jenom nazvy, aby se ukladaly jmena pasazeru do databaze bez dluhu
        ArrayList<String> passengers = new ArrayList<>();
        for (Passenger passenger : passengerList){
            passengers.add(passenger.getPassengerName());
        }

        //AKTUALIZACE DLUHU U PASAZERU
        int passengerCount = passengers.size() + 1;

        distance = distance.replace(",","."); //prehozeni carky na tecku pro double
        //VYPOCITANI CENY ZA CESTU
        double newDistance = Double.valueOf(distance);
        newDistance = newDistance / 1000;
        double priceForTrip = (carConsuption / 100) * newDistance * fuelPrice;
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


            //ULOZENI SAMOTNE JIZDY
            Ride ride = new Ride(time, passengers, distance, rideTime, priceForTrip, listPositions);

            DatabaseReference myRef = database.getReference("user");
            myRef.child(currentFirebaseUser.getUid())
                    .child("Ride")
                    .child(time)
                    .setValue(ride);
        }

        //ZAPSANI DLUHU OSTATNIM UZIVATELUM, POKUD JSOU
        for (Passenger passenger : passengerList){
            String name = passenger.getPassengerName();
            // zjistime u kazdeho, zdali ma ve jmene | - coz znamena, ze uzivatel chtel propojit jmeno s dalsim uzivatelem (vymenili se @ za |)
            // -1 se vrati, pokud tam neni, jinak vrati pozici, na ktery pozici symbolu se dany znak nacházi
            if (name.indexOf('|') != -1){
                //pokud je zadany email druheho cloveka v databazi (tzn. je zaregistrovany)
                if (emailList.contains(name)){
                    //pokud existuje, zjistime jeho uid a vlozime tam jeho dluh
                    for (UidEmail uidEmail : uidEmailsList){
                        String email = uidEmail.getEmail();
                        email = email.replace(".","|");
                        String uid;
                        if (email.equals(name)){
                            uid = uidEmail.getUid();
                            saveDebtToUserProfile(uid, priceForEachPassenger, userID, currentFirebaseUser.getEmail(), time, distance, rideTime, priceForTrip, listPositions);
                        }
                    }
                }
            }
        }
    }

    //metoda, ktera vlozi dluh danemu pasazerovi, ktery ma ucet
    //uid je toho kdo dluzi a komu se to zapise, price je cena za jednoho u te dane jizdy,
    // userIDDriver je od koho ten dluh je (komu se to ma vratit) a driverEmail je nazev uzivatele, který se vlozi pasazerovi, ze mu dluzi
    private void saveDebtToUserProfile(String uidPassenger, double priceForEachPassenger, String userIDDriver, String driverEmail,
                                       String time, String distance, long rideTime, double priceForTrip, ArrayList<LocationModel> listPositions ) {

        //ULOZENI PASAZERA
        double price = priceForEachPassenger - priceForEachPassenger - priceForEachPassenger;
        Passenger passenger = new Passenger(driverEmail, price);
        String email = driverEmail.replace(".","|");

        DatabaseReference myRef = database.getReference("user");
        myRef.child(uidPassenger)
                .child("passengers")
                .child(email)
                .setValue(passenger);

        //ULOZENI SAMOTNE CESTY
        ArrayList<String> passengers = new ArrayList<>();
        passengers.add(email);
        priceForTrip = priceForTrip - priceForTrip - priceForTrip;
        Ride ride = new Ride(time, passengers, distance, rideTime, priceForTrip, listPositions);

        myRef = database.getReference("user");
        myRef.child(uidPassenger)
                .child("Ride")
                .child(time)
                .setValue(ride);
    }


    //vrátí dluh daneho pasazera
    private double getPassengerDebt(String searchName) {
        for (Passenger passenger: this.passengersList ) {
            String name = passenger.getPassengerName();

            if(name.equals(searchName)){
                return Double.valueOf(passenger.getDebt());
            }
        }
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
    }

    public void settlePassengersDebtToHisProfile(Passenger passenger) {
        //ZAPSANI DLUHU OSTATNIM UZIVATELUM, POKUD JSOU

            String name = passenger.getPassengerName();
            // zjistime u kazdeho, zdali ma ve jmene | - coz znamena, ze uzivatel chtel propojit jmeno s dalsim uzivatelem (vymenili se @ za |)
            // -1 se vrati, pokud tam neni, jinak vrati pozici, na ktery pozici symbolu se dany znak nacházi
            if (name.indexOf('|') != -1){
                //pokud je zadany email druheho cloveka v databazi (tzn. je zaregistrovany)
                if (emailList.contains(name)){
                    //pokud existuje, zjistime jeho uid a vlozime tam jeho dluh
                    for (UidEmail uidEmail : uidEmailsList){
                        String email = uidEmail.getEmail();
                        email = email.replace(".","|");
                        String uid;
                        if (email.equals(name)){
                            uid = uidEmail.getUid(); //v tomhle mame uid kam budeme ukladat
                            updateProfileWithUID(uid); //staci nam jenom uid, protoze email se nastavi z prihlaseneho uzivatele
                        }
                    }
                }
            }
    }

    private void updateProfileWithUID(String uid) {

        String unModifiedEmail = currentFirebaseUser.getEmail();
        String email = unModifiedEmail.replace(".","|");
        Passenger passenger = new Passenger(email, 0);

        DatabaseReference myRef = database.getReference("user");
        myRef.child(uid)
                .child("passengers")
                .child(email)
                .setValue(passenger);
    }

    public void settlePassengersDebtToHisProfile(Passenger updatedPassenger, double debt) {

        String name = updatedPassenger.getPassengerName();
        // zjistime u kazdeho, zdali ma ve jmene | - coz znamena, ze uzivatel chtel propojit jmeno s dalsim uzivatelem (vymenili se @ za |)
        // -1 se vrati, pokud tam neni, jinak vrati pozici, na ktery pozici symbolu se dany znak nacházi
        if (name.indexOf('|') != -1){
            //pokud je zadany email druheho cloveka v databazi (tzn. je zaregistrovany)
            if (emailList.contains(name)){
                //pokud existuje, zjistime jeho uid a vlozime tam jeho dluh
                for (UidEmail uidEmail : uidEmailsList){
                    String email = uidEmail.getEmail();
                    email = email.replace(".","|");
                    String uid;
                    if (email.equals(name)){
                        uid = uidEmail.getUid(); //v tomhle mame uid kam budeme ukladat
                        updateProfileWithUID(uid, debt); //staci nam jenom uid, protoze email se nastavi z prihlaseneho uzivatele
                    }
                }
            }
        }
    }

    private void updateProfileWithUID(String uid, double debt) {
        String unModifiedEmail = currentFirebaseUser.getEmail();
        String email = unModifiedEmail.replace(".","|");
        Passenger passenger = new Passenger(email, debt);

        DatabaseReference myRef = database.getReference("user");
        myRef.child(uid)
                .child("passengers")
                .child(email)
                .setValue(passenger);
    }
}
