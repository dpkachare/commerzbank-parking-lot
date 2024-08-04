package org.example.parkinglot.models.vehicle;

import org.example.parkinglot.models.Size;

public class Truck extends AbstractVehicle {

    public Truck(String id) {
        super(id);
    }

    @Override
    public Size getSize() {
        return Size.LARGE;
    }

}
