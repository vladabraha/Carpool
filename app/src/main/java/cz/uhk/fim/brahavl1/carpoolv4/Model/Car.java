package cz.uhk.fim.brahavl1.carpoolv4.Model;

public class Car {

    private String name;
    private float fuelConsuption;
    private String carType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getFuelConsuption() {
        return fuelConsuption;
    }

    public void setFuelConsuption(float fuelConsuption) {
        this.fuelConsuption = fuelConsuption;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public Car(String name, float fuelConsuption, String carType) {
        this.name = name;
        this.fuelConsuption = fuelConsuption;
        this.carType = carType;
    }

    public Car() {
    }
}
