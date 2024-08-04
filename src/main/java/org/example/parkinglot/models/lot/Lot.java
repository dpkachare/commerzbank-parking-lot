package org.example.parkinglot.bo;

import java.util.Optional;

public class Lot {

    private final int id;
    private final Size size;

     private Optional<Vehicle> vehicle = Optional.empty();

    private LotParkingStatus parkingStatus = LotParkingStatus.AVAILABLE;

    public Lot(int id, Size size) {
        if (size == null) {
            throw new RuntimeException();
        }
        this.id = id;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return parkingStatus == LotParkingStatus.AVAILABLE;
    }

    public Optional<Vehicle> getVehicle() {
        return vehicle;
    }

    public boolean reserve(Vehicle vehicle) {
        if (vehicle == null || !isAvailable() || size != vehicle.getSize()) {
            return false;
        }
        this.vehicle = Optional.of(vehicle);
        parkingStatus = LotParkingStatus.OCCUPIED;
        return true;
    }

    public boolean release(Ticket ticket) {
        if (ticket == null || parkingStatus == LotParkingStatus.AVAILABLE || !getVehicle().isPresent()
                || ticket.getLotId() != getId() || !getVehicle().get().id().equals(ticket.getVehicleId())) {
            return false;
        }

        this.vehicle = Optional.empty();
        parkingStatus = LotParkingStatus.AVAILABLE;
        return true;
    }

}