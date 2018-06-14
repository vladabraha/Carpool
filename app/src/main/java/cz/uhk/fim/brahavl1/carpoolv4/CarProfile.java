package cz.uhk.fim.brahavl1.carpoolv4;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class CarProfile extends AppCompatActivity {

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

    private DatabaseReference myRef;
    private String userID;

    final int max = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_profile);

        btnSaveCarProfile = findViewById(R.id.btnSaveCarProfile);
        editTextCarName = findViewById(R.id.editTextCarName);
        seekBarfuelConsuption = findViewById(R.id.seekBarfuelConsuption);
        textProgress = findViewById(R.id.textProgress);
        database = FirebaseDatabase.getInstance();
        rg = findViewById(R.id.rdGroup);

        //do currentFirebaseUser se vloží uid prihlaseneho uzivatele
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();


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

        //TODO OPRAVIT TAHANI Z DATABAZE - JE TO POKUS JAK CIST HASMAPU???
//        myRef = FirebaseDatabase.getInstance().getReference("user")
//                .child(userID).child("carProfile").child("Auto 1");
//
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
//                Log.d("TAG", dataSnapshot.toString());
//
//                //showText(dataSnapshot.getValue(Car.class));
//                // ...
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//               // showText("nejde");
//                // ...
//            }
//        };
//        myRef.addValueEventListener(postListener);


        //TODO TAKHLE BY TO MELO JIT ALE NEJDE - PROC????
        myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //showData(dataSnapshot);
                Log.d("TAG", dataSnapshot.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Car car = new Car();
            car.setCarType(ds.child(userID).getValue(Car.class).getCarType()); //tohle kombo vytahne typ auta
            car.setFuelConsuption(ds.child(userID).getValue(Car.class).getFuelConsuption());
            car.setName(ds.child(userID).getValue(Car.class).getName());
            //showText(car.getName());

        }
    }

    private void showText(Car car){

        Toast.makeText(this, car.getName(),Toast.LENGTH_SHORT).show();
    }

    private void updateData(int progress) {

        float consuption = Float.valueOf(progress);
        consuption = ((consuption/100) * 12.5f) + 2f;
        fuelConsuption = String.valueOf(consuption);

        textProgress.setText(String.valueOf(consuption));
    }

    private void saveCarProfile() {
        //vytvoreni objektu auta
        if (rg.getCheckedRadioButtonId()==-1 || editTextCarName.getText().toString().trim().length() <= 0 || fuelConsuption == null){
            Toast.makeText(this, "Missing car details",Toast.LENGTH_SHORT).show();
        }else{
            car = new Car(editTextCarName.getText().toString(), Float.parseFloat(fuelConsuption), carType);

            //TODO predelat ukladani do samostane metoddy
            //tyhle 2 radky vloží něco do databaze
            DatabaseReference myRef = database.getReference("user");
            //jednotlive zanorovani se provadi pomoci .child
            myRef.child(currentFirebaseUser.getUid())
                    .child("carProfile")
                    .child(editTextCarName.getText().toString()) //pokud by bylo vice aut, tak se pak bude moct vybirat
                    .setValue(car); //profil auta

            Toast.makeText(this, "CarDetails Succesfully saved",Toast.LENGTH_SHORT).show();

            finish();
        }
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
        }
    }
}
