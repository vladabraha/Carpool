package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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

import cz.uhk.fim.brahavl1.carpoolv4.Adapter.CarManageRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Car;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class CarDelete extends AppCompatActivity implements CarManageRecyclerViewAdapter.onButtonCarDeleteInterface {

    //seznam, ktery budeme posilat do recycler view
    private ArrayList<Car> listCar;

    private DatabaseReference myRef;
    private String userID;
    private CarManageRecyclerViewAdapter mAdapter;
    private FirebaseUser currentFirebaseUser;
    private FirebaseDatabase database;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_delete);


        //do currentFirebaseUser se vloží uid prihlaseneho uzivatele
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_del2_car); //sem hodit z tyhle aktivity id recycler view
        mLayoutManager = new LinearLayoutManager(this); //nechat
        mRecyclerView.setLayoutManager(mLayoutManager); //nechat


        listCar =  new ArrayList<>();
        listCar = getCarsFromDatabase();
    }


    private ArrayList<Car> getCarsFromDatabase(){
        //TODO CTENI Z DATABAZE
        myRef = FirebaseDatabase.getInstance().getReference("user")
                .child(userID).child("carProfile");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Car car = postSnapshot.getValue(Car.class);
                    Log.d("TAG","nyni se vklada " + car.toString());
                    listCar.add(car);
                    Log.d("TAG", "v seznamu tedka je " + listCar.toString());
                    Log.d("TAG","v seznamu je " + String.valueOf(listCar.size() + " hodnot"));


                }
                Log.d("TAG","velikost je " + String.valueOf(listCar.size()));

                //TODO TADY TO MUSI BYT, ALE NEMELO BY TO TU BYT
//                mAdapter = new CarChooserRecyclerViewAdapter(CarChooser.this,listCar);
                mAdapter = new CarManageRecyclerViewAdapter(listCar);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnButtonChooseListener(CarDelete.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // showText("nejde");
                // ...
            }
        };
        myRef.addValueEventListener(postListener);
        return listCar;
    }
    @Override
    public void onButtonDelete(int position) {

    }
}
