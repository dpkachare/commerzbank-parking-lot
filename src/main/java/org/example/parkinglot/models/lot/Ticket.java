package org.example.parkinglot.bo;

public class Ticket {
    private final int lotId;
    private final String vehicleId;

    public Ticket(int lotId, String vehicleId) {
        this.lotId = lotId;
        this.vehicleId = vehicleId;
    }

    public int getLotId() {
        return lotId;
    }

    public String getVehicleId() {
        return vehicleId;
    }
}
