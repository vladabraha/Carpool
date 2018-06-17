package cz.uhk.fim.brahavl1.carpoolv4.Model;


import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressLint("ParcelCreator")
public class Passenger implements Parcelable{ //prida se Parcalable a umozni si to posilat objekty (nebo list objektu) skrz intenty +

    private String passengerName;
    private double debt;

    public Passenger() {
    }

    public Passenger(String passengerName, double debt) {
        this.passengerName = passengerName;
        this.debt = debt;
    }

    public Passenger(double debt, String passengerName) {
        this.passengerName = passengerName;
        this.debt = debt;
    }

    //sem prepsat co se bude predavat v jakym typu
    protected Passenger(Parcel in) {
        passengerName = in.readString();
        debt = in.readDouble();
    }

    public static final Creator<Passenger> CREATOR = new Creator<Passenger>() {
        @Override
        public Passenger createFromParcel(Parcel in) {
            return new Passenger(in);
        }

        @Override
        public Passenger[] newArray(int size) {
            return new Passenger[size];
        }
    };

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    @Override //muze byt nula - nevola se v tomhle pripade, ale je to overide metoda pro implementaci - musi byt
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(passengerName);
        dest.writeDouble(debt);
    }
}
