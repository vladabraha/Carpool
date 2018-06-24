package cz.uhk.fim.brahavl1.carpoolv4.Adapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    private RideOverviewRecyclerViewAdapter.onButtonRideChooseInterface onButtonRideChooseListener;

    public RideOverviewRecyclerViewAdapter() {
    }

    // Konstruktor  vlozi data, ktera se budou zobrazovat (vola se z aktivity)
    public RideOverviewRecyclerViewAdapter(ArrayList<Ride> rideList) {
        this.rideList = rideList;
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
        private TextView textViewPriceForRide;
        private TextView textViewRidePassengers;
        private Button btnDeleteRide;
        private Button btnShowRide;


        public RideViewHolder(View itemView) {
            super(itemView);


            //vytahnuti prvku z xml, ktery si zadala v onCreateViewHolder
            textViewRideDate = itemView.findViewById(R.id.textViewRideDate);
            textViewRideDistance = itemView.findViewById(R.id.textViewRideDistance);
            textViewRideTime = itemView.findViewById(R.id.textViewRideTime);
            textViewPriceForRide = itemView.findViewById(R.id.textViewPriceForRide);

            textViewRidePassengers = itemView.findViewById(R.id.textViewRidePassengers);
            btnDeleteRide = itemView.findViewById(R.id.btnDeleteRide);
            btnShowRide = itemView.findViewById(R.id.btnShowRide);


            btnDeleteRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onButtonRideChooseListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            onButtonRideChooseListener.onButtonDelete(position);
                        }
                    }
                }
            });

            btnShowRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onButtonRideChooseListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            onButtonRideChooseListener.onButtonShowRide(position);
                        }
                    }
                }
            });
        }

        //nasetovani jednotlivych prvku
        public void setRide(Ride rides) {
            //smazani gtm...
            StringBuilder date = new StringBuilder(rides.getDate());
            date.delete(20,30); //smazani 20 - 30 znaku

            textViewRideDate.setText(date.toString());
            textViewRideDistance.setText("distance " + rides.getDistance());
            if (rides.getRideTime() < 60){
                textViewRideTime.setText(String.valueOf("Ride time " + rides.getRideTime() + " seconds"));
            }else if (rides.getRideTime() < 3600 && rides.getRideTime() > 60){
                long time = rides.getRideTime();
                time = time / 60;
                textViewRideTime.setText(String.valueOf("Ride time " + String.valueOf(time) + " minutes"));
            }else if (rides.getRideTime() < 86400 && rides.getRideTime() > 3600){
                long time = rides.getRideTime();
                time = time / 3600;
                textViewRideTime.setText(String.valueOf("Ride time " + String.valueOf(time) + " hours"));
            }else {
                long time = rides.getRideTime();
                time = time / 86400;
                textViewRideTime.setText(String.valueOf("Ride time " + String.valueOf(time) + " days"));
            }

            textViewPriceForRide.setText("Price for this ride is: " + String.format("%.2f", rides.getPrice()));


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
            //smazani prvni carky
            StringBuilder passNames = new StringBuilder(passengerNames);
            passNames.delete(0,2); //smazani 20 - 30 znaku
            String name = passNames.toString();
            String clearName = name.replace("|", "."); //vymena znaku pro zobrazeni
            textViewRidePassengers.setText(clearName);
        }
    }

    //interface na komunikaci s aktivitou
    public interface onButtonRideChooseInterface {
        void onButtonDelete(int position);
        void onButtonShowRide(int position);
    }

    public void setOnButtonChooseListener(RideOverviewRecyclerViewAdapter.onButtonRideChooseInterface listener) {
        this.onButtonRideChooseListener = listener;

    }
}
