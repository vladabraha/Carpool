package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cz.uhk.fim.brahavl1.carpoolv4.R;

public class Dashboard extends AppCompatActivity {

    Button btnCarProfile;
    Button btnStartCarPool;
    Button btnSelectCar;
    int resultCode;
    int resultCode2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnCarProfile = (Button)findViewById(R.id.btnCarProfile);

        btnCarProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Dashboard.this, CarProfile.class );
                startActivity(intent);
            }
        });

        btnStartCarPool = (Button)findViewById(R.id.btnStartCarPool);

        btnStartCarPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStartPool = new Intent (Dashboard.this, MapsActivity.class );
                startActivityForResult(intentStartPool, resultCode);
            }
        });

        btnSelectCar = (Button) findViewById(R.id.btnSelectCar);

        btnSelectCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChooseCar = new Intent(Dashboard.this, CarChooser.class);
                startActivityForResult(intentChooseCar,resultCode2);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
