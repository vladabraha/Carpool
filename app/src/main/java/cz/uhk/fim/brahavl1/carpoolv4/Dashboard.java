package cz.uhk.fim.brahavl1.carpoolv4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Dashboard extends AppCompatActivity {

    Button btnCarProfile;
    Button btnStartCarPool;
    int resultCode;

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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
