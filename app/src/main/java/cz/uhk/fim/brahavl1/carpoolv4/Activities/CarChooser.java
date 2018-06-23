package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import cz.uhk.fim.brahavl1.carpoolv4.Adapter.CarChooserRecyclerViewAdapter;

import cz.uhk.fim.brahavl1.carpoolv4.Model.Car;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class CarChooser extends NavigationDrawer implements CarChooserRecyclerViewAdapter.onButtonCarChooseInterface {

    private RecyclerView mRecyclerView;
    private CarChooserRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView textViewInformation;


    private DatabaseReference myRef;
    private String userID;
    private FirebaseUser currentFirebaseUser;



    //seznam, ktery budeme posilat do recycler view
    private ArrayList<Car> listCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_car_chooser, frameLayout);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_choose_car); //sem hodit z tyhle aktivity id recycler view

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this); //nechat
        mRecyclerView.setLayoutManager(mLayoutManager); //nechat

        //vytvorit seznam, kterej se bude predavat Recycler view
        //takze vytahnout data z databaze

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();

        listCar =  new ArrayList<>();

        listCar = getCarsFromDatabase();

        textViewInformation = findViewById(R.id.textViewCarChooseInformation);

    }


    private ArrayList<Car> getCarsFromDatabase(){
        myRef = FirebaseDatabase.getInstance().getReference("user")
                .child(userID).child("carProfile");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Car car = postSnapshot.getValue(Car.class);
                    listCar.add(car);

                }
                mAdapter = new CarChooserRecyclerViewAdapter(listCar);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnButtonChooseListener(CarChooser.this);

                if (listCar.isEmpty()){
                    textViewInformation.setText("No car has been created");
                }else{
                    textViewInformation.setText("Choose car for ride");
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
        return listCar;
    }

    //sem chodi pozice z recyclerview
    @Override
    public void onButtonChoose(int position) {
//        Toast.makeText(this, listCar.get(position).getName(), Toast.LENGTH_SHORT).show();

        //tohle posle zpátky - v tomhle pripade zvoleny auto
        Intent resultIntent = new Intent();
        resultIntent.putExtra("car", String.valueOf(listCar.get(position).getFuelConsuption()));
        setResult(100, resultIntent);
        finish();



    }
}
