package cz.uhk.fim.brahavl1.carpoolv4.Activities;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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
import cz.uhk.fim.brahavl1.carpoolv4.Adapter.CarManageRecyclerViewAdapter;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Car;
import cz.uhk.fim.brahavl1.carpoolv4.Model.DatabaseConnector;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class CarProfile extends NavigationDrawer implements CarManageRecyclerViewAdapter.onButtonCarDeleteInterface{

    private String carType;
    private Button btnSaveCarProfile;
    private EditText editTextCarName;
    private SeekBar seekBarfuelConsuption;
    private String fuelConsuption;
    private TextView textProgress;
    private FirebaseUser currentFirebaseUser;
    private FirebaseDatabase database;
    private Car car;
    private RadioGroup rg;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    //seznam, ktery budeme posilat do recycler view
    private ArrayList<Car> listCar;

    private DatabaseReference myRef;
    private String userID;
    private CarManageRecyclerViewAdapter mAdapter;

    private DatabaseConnector databaseConnector;

    final int max = 400;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_car_profile, frameLayout);

        btnSaveCarProfile = findViewById(R.id.btnSaveCarProfile);
        editTextCarName = findViewById(R.id.editTextCarName);
        seekBarfuelConsuption = findViewById(R.id.seekBarfuelConsuption);
        textProgress = findViewById(R.id.textProgress);
        database = FirebaseDatabase.getInstance();
        rg = findViewById(R.id.rdGroup);

        //do currentFirebaseUser se vloží uid prihlaseneho uzivatele
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_manage_car); //sem hodit z tyhle aktivity id recycler view
        mLayoutManager = new LinearLayoutManager(this); //nechat
        mRecyclerView.setLayoutManager(mLayoutManager); //nechat


        listCar =  new ArrayList<>();
        listCar = getCarsFromDatabase();

        databaseConnector = new DatabaseConnector();

        seekBarfuelConsuption.setMax(max);
        seekBarfuelConsuption.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                /*percent = seek.getProgress();*/
                updateData(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btnSaveCarProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveCarProfile();
            }
        });
    }

    private void updateData(int progress) {

        float consuption = Float.valueOf(progress);
        consuption = (consuption/10)+ 2f;
        fuelConsuption = String.valueOf(consuption);

        textProgress.setText(String.valueOf(consuption));
    }

    private void saveCarProfile() {
        //vytvoreni objektu auta
        if (rg.getCheckedRadioButtonId()==-1 || editTextCarName.getText().toString().trim().length() <= 0 || fuelConsuption == null){
            Toast.makeText(this, "Missing car details",Toast.LENGTH_SHORT).show();
        }else{
            car = new Car(editTextCarName.getText().toString(), Float.parseFloat(fuelConsuption), carType);
            databaseConnector.saveCarProfile(car);
            Toast.makeText(this, "CarDetails Succesfully saved",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private ArrayList<Car> getCarsFromDatabase(){
        myRef = FirebaseDatabase.getInstance().getReference("user")
                .child(userID).child("carProfile");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                listCar.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Car car = postSnapshot.getValue(Car.class);
                    listCar.add(car);
                }

                mAdapter = new CarManageRecyclerViewAdapter(listCar);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnButtonChooseListener(CarProfile.this);
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


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_petrol:
                if (checked)
                   carType = "petrol";
                    break;
            case R.id.radio_diesel:
                if (checked)
                    carType = "diesel";
                    break;
            case R.id.radio_cng:
                if (checked)
                    carType = "cng";
                    break;
            case R.id.radio_lpg:
                if (checked)
                    carType = "lpg";
                break;
        }
    }


    @Override
    public void onButtonDelete(int position) {

        String carName = listCar.get(position).getName().toString();
        databaseConnector.deleteCarProfile(carName);
        Toast.makeText(this, "CarDetails Succesfully saved",Toast.LENGTH_SHORT).show();
    }
}
