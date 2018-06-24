package cz.uhk.fim.brahavl1.carpoolv4.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cz.uhk.fim.brahavl1.carpoolv4.Model.Car;
import cz.uhk.fim.brahavl1.carpoolv4.R;

public class CarManageRecyclerViewAdapter extends RecyclerView.Adapter<CarManageRecyclerViewAdapter.CarViewHolder> {
    private ArrayList<Car> carList; //list toho co sem bude chodt

    private onButtonCarDeleteInterface OnButtonCarDeleteListener;


    public CarManageRecyclerViewAdapter() {
    }

    // Konstruktor  vlozi data, ktera se budou zobrazovat (vola se z aktivity)
    public CarManageRecyclerViewAdapter(ArrayList<Car> carList) {
        this.carList = carList;
    }

    // Prepsat podle holderu dole
    // Create new views (invoked by the layout manager)
    @Override //vytvori holdery polde poctu co se vejde vola se jenom pri inicializaci
    public CarManageRecyclerViewAdapter.CarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext()); //taha xml nechat

        View view = layoutInflater.inflate(R.layout.car_manage_recyclerview, null); //sem hod nazev xml, kterym se bude plnit recycler view

        CarManageRecyclerViewAdapter.CarViewHolder viewHolder = new CarManageRecyclerViewAdapter.CarViewHolder(view); //vytvoreni carviewholederu (viz. trida dole)
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager) --spravuje jeden radek
    @Override
    public void onBindViewHolder(CarManageRecyclerViewAdapter.CarViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Car car = carList.get(position); // zde upravit podle toho, co se bude v holderu zobrazovat, ale pozice z recyclerview vzdycky bude stejna
        holder.setCar(car);

    }

    // Potrebuje to vedet jak velky pole to bude - upravit nazvy
    @Override
    public int getItemCount() {
        return carList.size();
    }


    public class CarViewHolder extends RecyclerView.ViewHolder {

        //tady si vytahnout id z layoutu a nasetovat je podle toho jak to co se v nich ma zobrazit
        //de facto tady vytvoris jednotlivy polozky co se budou v recyclerview zobrazovat
        private TextView textViewCarName;
        private TextView textViewCarFuelConsuption;
        private TextView textViewCarType;
        private Button buttonChooseCar;

        public CarViewHolder(View itemView) {
            super(itemView);
            //vytahnuti prvku z xml, ktery si zadala v onCreateViewHolder
            textViewCarName = itemView.findViewById(R.id.textViewCarName2);
            textViewCarFuelConsuption = itemView.findViewById(R.id.textViewCarFuelConsuption2);
            textViewCarType = itemView.findViewById(R.id.textViewCarType2);
            buttonChooseCar = itemView.findViewById(R.id.btnDeleteCar2);


            buttonChooseCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //tohle zkontroluje jestli je něco vybrany a zavola metodu dole
                    if (OnButtonCarDeleteListener != null) { //nutne pro to aby to nehodilo nullpointer
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) { //nutne pro to aby to nehodilo nullpointer
                            OnButtonCarDeleteListener.onButtonDelete(position);
                        }
                    }
                }
            });
        }

        //nasetovani jednotlivych prvku
        public void setCar(final Car car) {
            textViewCarName.setText(car.getName());
            textViewCarFuelConsuption.setText(String.valueOf(car.getFuelConsuption()));
            textViewCarType.setText(car.getCarType());
        }
    }

    //interface na komunikaci s aktivitou
    public interface onButtonCarDeleteInterface {
        void onButtonDelete(int position);
    }

    public void setOnButtonChooseListener(onButtonCarDeleteInterface listener) {
        this.OnButtonCarDeleteListener = listener;
    }
}
