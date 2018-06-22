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
import cz.uhk.fim.brahavl1.carpoolv4.Model.Ride;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class PassengerSettlementRecyclerViewAdapter extends RecyclerView.Adapter<PassengerSettlementRecyclerViewAdapter.PassengerViewHolder> {

    private ArrayList<Passenger> passengerList; //list toho co sem bude chodit

    private PassengerSettlementRecyclerViewAdapter.onButtonPassengerActionInterface OnButtonActionListener;


    public PassengerSettlementRecyclerViewAdapter() {
    }

    // Konstruktor  vlozi data, ktera se budou zobrazovat (vola se z aktivity)
    public PassengerSettlementRecyclerViewAdapter( ArrayList<Passenger> passengerList) {
        this.passengerList = passengerList;
    }

    // Prepsat podle holderu dole
    @Override //vytvori holdery podle poctu co se vejde, vola se jenom pri inicializaci
    public PassengerSettlementRecyclerViewAdapter.PassengerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext()); //taha xml nechat

        View view = layoutInflater.inflate(R.layout.passenger_settlement_recyclerview, null); //sem hod nazev xml, kterym se bude plnit recycler view

        PassengerSettlementRecyclerViewAdapter.PassengerViewHolder viewHolder = new PassengerSettlementRecyclerViewAdapter.PassengerViewHolder(view); //vytvoreni viewholederu (viz. trida dole)
        return viewHolder;
    }

    // spravuje jeden radek
    @Override
    public void onBindViewHolder(PassengerSettlementRecyclerViewAdapter.PassengerViewHolder holder, int position) {
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
        private TextView textViewPasssengerSettlementName;
        private TextView textViewPasssengerSettlementDebt;

        private Button buttonSettleDebt;


        public PassengerViewHolder(View itemView) {
            super(itemView);


            //vytahnuti prvku z xml, ktery si zadala v onCreateViewHolder
            textViewPasssengerSettlementName = itemView.findViewById(R.id.textViewPasssengerSettlementName);
            textViewPasssengerSettlementDebt = itemView.findViewById(R.id.textViewPasssengerSettlementDebt);
            buttonSettleDebt = itemView.findViewById(R.id.btnSettleDebt);


            buttonSettleDebt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //tohle zkontroluje jestli je něco vybrany a zavola metodu dole
                    if (OnButtonActionListener != null) { //nutne pro to aby to nehodilo nullpointer
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) { //nutne pro to aby to nehodilo nullpointer
                            OnButtonActionListener.onButtonSettlement(position);
                        }

                    }
                }
            });


        }

        //nasetovani jednotlivych prvku
        public void setPassenger(Passenger passenger) {

            String oldName = passenger.getPassengerName();
            String newName = oldName.replace("|",".");
            textViewPasssengerSettlementName.setText(newName);
            long debt = Math.round(passenger.getDebt());

            if (debt > 0){
                textViewPasssengerSettlementDebt.setText("dluzi " + String.valueOf(debt));
            } else{
                if (debt == 0){
                    textViewPasssengerSettlementDebt.setText("vyrovnano");
                }else{
                    textViewPasssengerSettlementDebt.setText("dluzite " + String.valueOf(debt));
                }
            }
        }
    }

    //interface na komunikaci s aktivitou
    public interface onButtonPassengerActionInterface{
        void onButtonSettlement(int position);
        void onButtonPayback(int position);

    }

    public void setOnButtonActionListener(PassengerSettlementRecyclerViewAdapter.onButtonPassengerActionInterface listener){
        this.OnButtonActionListener = listener;

    }
}
