package cz.uhk.fim.brahavl1.carpoolv4.Adapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import cz.uhk.fim.brahavl1.carpoolv4.Model.Passenger;
import cz.uhk.fim.brahavl1.carpoolv4.Model.Ride;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class RideOverviewRecyclerViewAdapter extends RecyclerView.Adapter<RideOverviewRecyclerViewAdapter.RideViewHolder>  {

    private ArrayList<Ride> rideList; //list toho co sem bude chodit

    private RideOverviewRecyclerViewAdapter.onButtonRideChooseInterface onButtonRideChooseInterface;

    // Konstruktor  vlozi data, ktera se budou zobrazovat (vola se z aktivity)
    public RideOverviewRecyclerViewAdapter(ArrayList<Ride> rideList) {
        this.rideList = rideList;
//        Log.d("TAG",String.valueOf(passengerList.size()));
    }

    // Prepsat podle holderu dole
    @Override //vytvori holdery podle poctu co se vejde, vola se jenom pri inicializaci
    public RideOverviewRecyclerViewAdapter.RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext()); //taha xml nechat

        View view = layoutInflater.inflate(R.layout.ride_overview_recyclerview, null); //sem hod nazev xml, kterym se bude plnit recycler view

        RideOverviewRecyclerViewAdapter.RideViewHolder viewHolder = new RideOverviewRecyclerViewAdapter.RideViewHolder(view); //vytvoreni viewholederu (viz. trida dole)
        return viewHolder;
    }

    // spravuje jeden radek
    @Override
    public void onBindViewHolder(RideOverviewRecyclerViewAdapter.RideViewHolder holder, int position) {
        Ride ride = rideList.get(position); // zde upravit podle toho, co se bude v holderu zobrazovat, ale pozice se bude zjitovat vzdy
        holder.setRide(ride);

    }

    // Potrebuje to vedet jak velky pole to bude - upravit nazvy
    @Override
    public int getItemCount() {
        return rideList.size();
    }


    public class RideViewHolder extends RecyclerView.ViewHolder {

        //tady si vytahnout id z layoutu a nasetovat je podle toho jak to co se v nich ma zobrazit
        //de facto tady vytvoris jednotlivy polozky co se budou v recyclerview zobrazovat
        private TextView textViewRideDate;
        private TextView textViewRideDistance;
        private TextView textViewRideTime;
        private TextView textViewRidePassengers;
        private Button btnDeleteRide;


        public RideViewHolder(View itemView) {
            super(itemView);


            //vytahnuti prvku z xml, ktery si zadala v onCreateViewHolder
            textViewRideDate = itemView.findViewById(R.id.textViewRideDate);
            textViewRideDistance = itemView.findViewById(R.id.textViewRideDistance);
            textViewRideTime = itemView.findViewById(R.id.textViewRideTime);
            textViewRidePassengers = itemView.findViewById(R.id.textViewRidePassengers);
            btnDeleteRide = itemView.findViewById(R.id.btnDeleteRide);


            btnDeleteRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            //Pri zmene zaskrtnuti checkboxu se zavola bude isChecked, nebo else
//            btnDeleteRide.setOnClickListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (buttonView.isChecked()) {
//                        passengerCheckedList.add(passengerList.get(getAdapterPosition()));
//                        OnButtonPassengerChooseListener.onCheckboxChange(passengerCheckedList);
//
//                    } else {
//                        passengerCheckedList.remove(passengerList.get(getAdapterPosition()));
//                        OnButtonPassengerChooseListener.onCheckboxChange(passengerCheckedList);
//                    }
//                }
//            });

        }

        //nasetovani jednotlivych prvku
        public void setRide(Ride rides) {
            textViewRideDate.setText(rides.getDate());
            textViewRideDistance.setText("distance " + rides.getDistance());;
            textViewRideTime.setText(String.valueOf("Ride time " + rides.getRideTime()));

            //projdu vsechny pasazery, hodim si je do pole
            ArrayList<String> passengers = new ArrayList<>();
            passengers.clear();
            for (int i = 0; i < rides.getPassengers().size(); i++){
                passengers.add(rides.getPassengers().get(i));
            }

            //a vytvorim jeden string se všema jmenama
            String passengerNames = "";
            //pokud neni zadny pasazer, tak se carka neudela
            if(!passengers.isEmpty()){
                for (String name : passengers){
                    passengerNames = passengerNames + ", " + name;
                }
            }
            textViewRidePassengers.setText(passengerNames);;
        }
    }

    //interface na komunikaci s aktivitou
    public interface onButtonRideChooseInterface {
        void onButtonDelete(ArrayList<Ride> listRides);

    }

    public void setOnButtonChooseListener(RideOverviewRecyclerViewAdapter.onButtonRideChooseInterface listener) {
        this.onButtonRideChooseInterface = listener;

    }
}
