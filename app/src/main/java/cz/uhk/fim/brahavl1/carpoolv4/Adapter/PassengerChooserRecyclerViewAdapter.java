package cz.uhk.fim.brahavl1.carpoolv4.Adapter;

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
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class PassengerChooserRecyclerViewAdapter extends  RecyclerView.Adapter<PassengerChooserRecyclerViewAdapter.PassengerViewHolder> {

    private ArrayList<Passenger> passengerList; //list toho co sem bude chodit
    private ArrayList<Passenger> passengerCheckedList; //list toho co sem bude chodit

    private PassengerChooserRecyclerViewAdapter.onButtonPassengerChooseInterface OnButtonPassengerChooseListener;


    // Konstruktor  vlozi data, ktera se budou zobrazovat (vola se z aktivity)
    public PassengerChooserRecyclerViewAdapter( ArrayList<Passenger> passengerList) {
        this.passengerList = passengerList;
        passengerCheckedList = new ArrayList<>();
//        Log.d("TAG",String.valueOf(passengerList.size()));
    }

    // Prepsat podle holderu dole
    @Override //vytvori holdery podle poctu co se vejde, vola se jenom pri inicializaci
    public PassengerChooserRecyclerViewAdapter.PassengerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext()); //taha xml nechat

        View view = layoutInflater.inflate(R.layout.passenger_chooser_recyclerview, null); //sem hod nazev xml, kterym se bude plnit recycler view

        PassengerChooserRecyclerViewAdapter.PassengerViewHolder viewHolder = new PassengerChooserRecyclerViewAdapter.PassengerViewHolder(view); //vytvoreni viewholederu (viz. trida dole)
        return viewHolder;
    }

    // spravuje jeden radek
    @Override
    public void onBindViewHolder(PassengerChooserRecyclerViewAdapter.PassengerViewHolder holder, int position) {
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
        private CheckBox checkBoxChoosePassenger;


        public PassengerViewHolder(View itemView) {
            super(itemView);


            //vytahnuti prvku z xml, ktery si zadala v onCreateViewHolder

            checkBoxChoosePassenger = itemView.findViewById(R.id.checkBoxPassenger);


//            checkBoxChoosePassenger.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //tohle zkontroluje jestli je něco vybrany a zavola metodu dole
//                    if (OnButtonPassengerChooseListener != null) { //nutne pro to aby to nehodilo nullpointer
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) { //nutne pro to aby to nehodilo nullpointer
//                            OnButtonPassengerChooseListener.onButtonChoose(position);
//                        }
//                        ;
//                    }
//                }
//            });

            //Pri zmene zaskrtnuti checkboxu se zavola bude isChecked, nebo else
            checkBoxChoosePassenger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(buttonView.isChecked()){
                        Log.i("TAGS", "vola se ischecked");
                        Log.i("TAGS", "pozice seznamu je " + String.valueOf(getAdapterPosition()));
                        passengerCheckedList.add(passengerList.get(getAdapterPosition()));
                        OnButtonPassengerChooseListener.onCheckboxChange(passengerCheckedList);

                    }else{
                        Log.i("TAGS", "vola se unchecked");
                        passengerCheckedList.remove(passengerList.get(getAdapterPosition()));
                        OnButtonPassengerChooseListener.onCheckboxChange(passengerCheckedList);
                    }
                }
            });

        }

        //nasetovani jednotlivych prvku
        public void setPassenger(Passenger passenger) {

            checkBoxChoosePassenger.setText(passenger.getPassengerName());

        }
    }

    //interface na komunikaci s aktivitou
    public interface onButtonPassengerChooseInterface{
        void onButtonChoose(int position);
        void onCheckboxChange(ArrayList<Passenger> passengerCheckedList);

    }

    public void setOnButtonChooseListener(PassengerChooserRecyclerViewAdapter.onButtonPassengerChooseInterface listener){
        this.OnButtonPassengerChooseListener = listener;

    }
}
