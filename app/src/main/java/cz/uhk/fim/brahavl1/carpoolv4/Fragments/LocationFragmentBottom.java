package cz.uhk.fim.brahavl1.carpoolv4.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cz.uhk.fim.brahavl1.carpoolv4.R;

public class LocationFragmentBottom extends Fragment {
    private Button btnSaveRide;
    private onButtonSaveInterface onLocationUpdateInterface;

    public LocationFragmentBottom() {
        // Required empty public constructor
    }

    //overeni, ze se interface nasetoval
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onLocationUpdateInterface = (LocationFragmentBottom.onButtonSaveInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLocationUpdateInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location_fragment_bottom, container, false);
        btnSaveRide = view.findViewById(R.id.btnSaveRide);

        btnSaveRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLocationUpdateInterface.onButtonClickSave();
            }
        });
        return view;

    }

    //slouuzi pro komunikaci mezi fragmentem a aktivitou
    public interface onButtonSaveInterface{
        void onButtonClickSave();
    }


    //pro mapovou aktivitu nastavi location interface
    public void setOnLocationUpdateInterface(onButtonSaveInterface onLocationUpdateInterface) {
        this.onLocationUpdateInterface = onLocationUpdateInterface;
    }
}
