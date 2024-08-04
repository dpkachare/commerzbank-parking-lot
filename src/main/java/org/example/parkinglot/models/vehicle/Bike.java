package org.example.parkinglot.models.vehicle;

import org.example.parkinglot.models.Size;

public class Bike extends AbstractVehicle {

    public Bike(String id) {
        super(id);
    }

    @Override
    public Size getSize() {
        return Size.SMALL;
    }


}
