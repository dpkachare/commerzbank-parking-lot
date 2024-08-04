package org.example.parkinglot.bo;

public class Car extends AbstractVehicle {


    protected Car(String id) {
        super(id);
    }

    @Override
    public Size getSize() {
        return Size.MEDIUM;
    }


}
