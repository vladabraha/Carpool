package cz.uhk.fim.brahavl1.carpoolv4;


import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment {

    private Button btnStartTracking;
    private Button btnStopTracking;
    private Boolean mRequestingLocationUpdates = false;
    private EditText editKilometres;
    private Chronometer chronometer;

    private onButtonInterface onLocationUpdateInterface;

    public LocationFragment() {
        // Required empty public constructor
    }

    //overeni, ze se interface nasetoval
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onLocationUpdateInterface = (onButtonInterface) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLocationUpdateInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_location, container, false);

        editKilometres = view.findViewById(R.id.editKilometres);

        chronometer = view.findViewById(R.id.chronometer);

        btnStartTracking = view.findViewById(R.id.btnStartTracking);

        btnStartTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequestingLocationUpdates = true;
                onLocationUpdateInterface.onButtonClickStart(mRequestingLocationUpdates);

                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
            }
        });

        btnStopTracking = view.findViewById(R.id.btnStopTracking);

        btnStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRequestingLocationUpdates = false;
                onLocationUpdateInterface.onButtonClickStop();
                chronometer.stop();
            }
        });

        return view;


    }

    //slouuzi pro komunikaci mezi fragmentem a aktivitou
    public interface onButtonInterface{
        void onButtonClickStart(Boolean mRequestingLocationUpdates);
        void onButtonClickStop();

    }

    public void startChronometr(){
       chronometer.start(); // stop a chronometer
    }

    public void stopChronometr(){
        chronometer.stop(); // stop a chronometer
        String formatType = chronometer.getFormat();

    }

    //pro mapovou aktivitu nastavi location interface
    public void setOnLocationUpdateInterface(onButtonInterface onLocationUpdateInterface) {
        this.onLocationUpdateInterface = onLocationUpdateInterface;
    }


    public void updateDistance(double distance){

        editKilometres.setText(String.format("%.2f", distance));
//        editKilometres.setText(String.valueOf(distance));

    }
}
