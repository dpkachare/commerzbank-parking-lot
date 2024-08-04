package org.example.parkinglot.models.lot;

import org.example.parkinglot.models.Size;
import org.example.parkinglot.models.vehicle.Vehicle;

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

    /**
     * Reserves itself for the provided vehicle
     *
     * @param vehicle to be parked
     * @return true if lot is unoccupied and matches the vehicle's size, otherwise false
     */
    public boolean reserve(Vehicle vehicle) {
        if (vehicle == null || !isAvailable() || size != vehicle.getSize()) {
            return false;
        }
        this.vehicle = Optional.of(vehicle);
        parkingStatus = LotParkingStatus.OCCUPIED;
        return true;
    }

    /**
     * Releases the parked vehicle and becomes available for next vehicle. This call invalidates the ticket passed to
     * ensure it cannot be reused multiple times to unpark.
     *
     * @param ticket which was issued for this lot when vehicle was parked
     * @return true if lot is occupied by the same vehicle, otherwise false
     */
    public boolean release(Ticket ticket) {
        if (ticket == null || isAvailable() || !getVehicle().isPresent()
                || ticket.getLotId() != getId() || !getVehicle().get().id().equals(ticket.getVehicleId())
                || size != getVehicle().get().getSize()) {
            return false;
        }

        this.vehicle = Optional.empty();
        parkingStatus = LotParkingStatus.AVAILABLE;
        ticket.invalidateTicket();
        return true;
    }

}