package cz.uhk.fim.brahavl1.carpoolv4.Model;

public class Passenger {

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
}
