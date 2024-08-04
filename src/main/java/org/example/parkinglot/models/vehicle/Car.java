package org.example.parkinglot.models.vehicle;

import org.example.parkinglot.models.Size;

public class Car extends AbstractVehicle {


    public Car(String id) {
        super(id);
    }

    @Override
    public Size getSize() {
        return Size.MEDIUM;
    }


}
