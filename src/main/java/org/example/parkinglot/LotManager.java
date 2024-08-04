package org.example.parkinglot;

import org.example.parkinglot.models.LotTrackerFactory;
import org.example.parkinglot.models.Size;
import org.example.parkinglot.models.lot.Ticket;
import org.example.parkinglot.models.vehicle.Vehicle;

import java.util.EnumMap;

/**
 * Service class which provides API to park and unPark vehicle.
 * It maintains map of {@link LotTracker} keyed by their sizes. All the heavy work is encapsulated
 * in {@link LotTracker} and this class simply delegates the reserving/releasing
 * and tracking the number of vehicles to corresponding {@link LotTracker} instances based on the size
 * of incoming or outgoing vehicle.
 */
public class LotManager {

    private final EnumMap<Size, LotTracker> lotTrackerBySizeMap = new EnumMap<>(Size.class);

    public LotManager(int noOfCarLots, int noOfBikeLots, int noOfTruckLots, LotTrackerFactory factory) {
        lotTrackerBySizeMap.put(Size.SMALL, factory.createLotTracker(noOfBikeLots, Size.SMALL));
        lotTrackerBySizeMap.put(Size.MEDIUM, factory.createLotTracker(noOfCarLots, Size.MEDIUM));
        lotTrackerBySizeMap.put(Size.LARGE, factory.createLotTracker(noOfTruckLots, Size.LARGE));
    }

    /**
     *
     *
     * @param vehicle to be parked
     * @return A valid {@link Ticket} if empty slots are available for vehicle size, else null;
     */
     public Ticket park(Vehicle vehicle) {
        if (vehicle == null || vehicle.getSize() == null) {
            return null;
        }
        return lotTrackerBySizeMap.get(vehicle.getSize())
                .reserveLot(vehicle)
                .orElse(null);
    }

    /**
     *
     * @param ticket for the vehicle to be unParked.
     * @return True if ticket is valid for the vehicle, else False. True indicates vehicle can be unParked.
     */
    public boolean unPark(Ticket ticket) {
        if (ticket == null || ticket.getVehicleSize() == null) {
            return false;
        }
        return lotTrackerBySizeMap.get(ticket.getVehicleSize()).releaseLot(ticket);
    }

}
