package cz.uhk.fim.brahavl1.carpoolv4.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cz.uhk.fim.brahavl1.carpoolv4.Model.Passenger;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class PassengerProfileRecyclerViewAdapter extends RecyclerView.Adapter<PassengerProfileRecyclerViewAdapter.PassengerViewHolder> {

    private ArrayList<Passenger> passengerList; //list toho co sem bude chodit

    private PassengerProfileRecyclerViewAdapter.onButtonPassengerDeleteInterface OnButtonPassengerDeleteListener;


    public PassengerProfileRecyclerViewAdapter() {
    }

    // Konstruktor  vlozi data, ktera se budou zobrazovat (vola se z aktivity)
    public PassengerProfileRecyclerViewAdapter( ArrayList<Passenger> passengerList) {
        this.passengerList = passengerList;
        Log.d("TAG",String.valueOf(passengerList.size()));
    }

    // Prepsat podle holderu dole
    @Override //vytvori holdery podle poctu co se vejde, vola se jenom pri inicializaci
    public PassengerProfileRecyclerViewAdapter.PassengerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext()); //taha xml nechat

        View view = layoutInflater.inflate(R.layout.passenger_profile_recyclerview, null); //sem hod nazev xml, kterym se bude plnit recycler view

        PassengerProfileRecyclerViewAdapter.PassengerViewHolder viewHolder = new PassengerProfileRecyclerViewAdapter.PassengerViewHolder(view); //vytvoreni viewholederu (viz. trida dole)
        return viewHolder;
    }

    // spravuje jeden radek
    @Override
    public void onBindViewHolder(PassengerProfileRecyclerViewAdapter.PassengerViewHolder holder, int position) {
        Passenger passenger = passengerList.get(position); // zde upravit podle toho, co se bude v holderu zobrazovat, ale pozice se bude zjitovat vzdy
        holder.setPassenger(passenger);

    }

    // Potrebuje to vedet jak velky pole to bude - upravit nazvy
    @Override
    public int getItemCount() {
        return passengerList.size();
    }


    public class PassengerViewHolder extends RecyclerView.ViewHolder {

        //tady si vytahnout id z layoutu a nasetovat je podle toho jak to co se v nich ma zobrazit
        //de facto tady vytvoris jednotlivy polozky co se budou v recyclerview zobrazovat
        private TextView textViewPasengerName;
        private TextView textViewPassengerDebt;

        private Button buttonChoosePassenger;


        public PassengerViewHolder(View itemView) {
            super(itemView);


            //vytahnuti prvku z xml, ktery si zadala v onCreateViewHolder
            textViewPasengerName = itemView.findViewById(R.id.textViewPasengerName);
            textViewPassengerDebt = itemView.findViewById(R.id.textViewPasengerDebt);
            buttonChoosePassenger = itemView.findViewById(R.id.btnPassengerDelete);


            buttonChoosePassenger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //tohle zkontroluje jestli je něco vybrany a zavola metodu dole
                    if (OnButtonPassengerDeleteListener != null) { //nutne pro to aby to nehodilo nullpointer
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) { //nutne pro to aby to nehodilo nullpointer
                            OnButtonPassengerDeleteListener.onButtonDelete(position);
                        }
                        ;
                    }
                }
            });


        }

        //nasetovani jednotlivych prvku
        public void setPassenger(Passenger passenger) {

            textViewPasengerName.setText(passenger.getPassengerName());
            long debt = Math.round(passenger.getDebt());
            textViewPassengerDebt.setText("dluzi " + String.valueOf(debt));

        }
    }

    //interface na komunikaci s aktivitou
    public interface onButtonPassengerDeleteInterface{
        void onButtonDelete(int position);


    }

    public void setOnButtonDeleteListener(PassengerProfileRecyclerViewAdapter.onButtonPassengerDeleteInterface listener){
        this.OnButtonPassengerDeleteListener = listener;

    }
}

