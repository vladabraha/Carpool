package cz.uhk.fim.brahavl1.carpoolv4.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import cz.uhk.fim.brahavl1.carpoolv4.R;

public class FuelPrice extends AppCompatActivity {

    private SeekBar seekBarfuelPrice;
    private TextView textProgressPrice;
    private float price;

    private Button btnSaveFuelPrice;


    final int max = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_price);

        textProgressPrice = findViewById(R.id.textProgressPrice);

        btnSaveFuelPrice = findViewById(R.id.btnSaveFuelPrice);
        btnSaveFuelPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("fuelPrice", String.valueOf(price));
                setResult(400, resultIntent);
                finish();
            }
        });


        seekBarfuelPrice = findViewById(R.id.seekBarFuelPrice);
        seekBarfuelPrice.setMax(max);
        seekBarfuelPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateData(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }

    private void updateData(int progress) {
        price = Float.valueOf(progress);
        price = (price/10) + 10;

        textProgressPrice.setText(String.valueOf(price));
    }
}
