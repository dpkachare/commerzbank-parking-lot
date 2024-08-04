package org.example.parkinglot.bo;

public class Bike extends AbstractVehicle {

    protected Bike(String id) {
        super(id);
    }

    @Override
    public Size getSize() {
        return Size.SMALL;
    }


}
