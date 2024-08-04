package org.example.parkinglot.bo;

public class Truck extends AbstractVehicle {

    protected Truck(String id) {
        super(id);
    }

    @Override
    public Size getSize() {
        return Size.LARGE;
    }

}
