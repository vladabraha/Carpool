package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cz.uhk.fim.brahavl1.carpoolv4.R;

public class Dashboard extends AppCompatActivity {

    Button btnCarProfile;
    Button btnStartCarPool;
    Button btnSelectCar;
    Button btnDeleteCar;
    int resultCode;
    int resultCode2 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnCarProfile = (Button) findViewById(R.id.btnCarProfile);

        btnCarProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, CarProfile.class);
                startActivity(intent);
            }
        });

        btnStartCarPool = (Button) findViewById(R.id.btnStartCarPool);

        btnStartCarPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStartPool = new Intent(Dashboard.this, MapsActivity.class);
                startActivityForResult(intentStartPool, resultCode);
            }
        });

        btnSelectCar = (Button) findViewById(R.id.btnSelectCar);

        btnSelectCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChooseCar = new Intent(Dashboard.this, CarChooser.class);
//                intentChooseCar.putExtra("carName");
                startActivityForResult(intentChooseCar, resultCode2);
            }
        });

        btnDeleteCar = (Button) findViewById(R.id.btnDeleteCar);

        btnDeleteCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDelCar = new Intent(Dashboard.this, CarDelete.class);
                startActivity(intentDelCar);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        //tady budu switchovat akce podle toho ze ktery aktivity se vracim
        switch (resultCode){
            //Vraceni z CarChooser
            case 100:
                String fuelConsuption = data.getStringExtra("car");

                Log.d("TAG",fuelConsuption );
                Toast.makeText(this, fuelConsuption, Toast.LENGTH_SHORT).show();
                break;
        }


//        if (resultCode == 100) {
//            String choosedOption = data.getStringExtra("car");
//
//            Log.d("TAG",choosedOption );
//            Toast.makeText(this, choosedOption, Toast.LENGTH_SHORT).show();

//            if(paid){
//                //do týhle proměnný hodí výsledek z resultu
//                int price = resultCode;
//
//                //vymaže items na nový záznam
//                this.items.clear();
//                rewriteData();
//            }
        }
    }

