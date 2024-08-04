 package org.example.parkinglot;

import org.example.parkinglot.models.LotTrackerFactory;
import org.example.parkinglot.models.lot.Lot;
import org.example.parkinglot.models.Size;
import org.example.parkinglot.models.lot.Ticket;
import org.example.parkinglot.models.vehicle.Vehicle;

import java.util.Arrays;
import java.util.Optional;

/**
A simple data structure to encapsulate {@link Lot} and track number of available lots.
Since we will have finite countable number of lots, the lots are numbered numerically starting with ID=0.

This data structure maintains an array of lots indexed by its ID.This ensures faster lookups and access to individual
lots using index in array.

Not a ThreadSafe class as in actual world, only 1 vehicle would be allowed to enter or exit the parking lot
at a given time.
*/
public class LotTracker {

    private int numberOfLots;
    private Lot[] lots;

    private final Size size;

    private int numberOfLotsOccupied = 0;

    public static class Factory implements LotTrackerFactory {
        public static final Factory INSTANCE = new Factory();

        private Factory() {}

        public LotTracker createLotTracker(int numberOfLots, Size size) {
            return new LotTracker(numberOfLots, size);
        }
    }

    private LotTracker(int numberOfLots, Size lotSize) {
        if (numberOfLots < 0 || lotSize == null) {
            throw new RuntimeException();
        }
        this.numberOfLots = numberOfLots;
        this.size = lotSize;
        initLots();
    }

    /**
     * Initializes the {@link Lot}'s with monotonically increasing IDs starting with 0 to numberOfLots - 1
     */
    private void initLots() {
        lots = new Lot[numberOfLots];
        for (int i=0; i<numberOfLots; i++) {
            lots[i] = new Lot(i, getLotSize());
        }
    }

    /**
     * Finds an available lot and attempts to reserve it. If successful, increments the count of lots occupied.
     *
     * @param vehicle to be parked
     * @return A valid {@link Ticket} if vehicle size matches lot size and lot is available, otherwise an empty Optional
     */
    public Optional<Ticket> reserveLot(Vehicle vehicle) {
        if (vehicle == null || getLotSize() != vehicle.getSize() || getNumberOfLotsOccupied() == getNumberOfLots()) {
            return Optional.empty();
        }

        Optional<Lot> lotOptional = findAvailableLot(lots);
        if(!lotOptional.isPresent()) {
            return Optional.empty();
        }

        Lot lot = lotOptional.get();
        if(lot.reserve(vehicle)) {
            numberOfLotsOccupied++;
            return Optional.of(new Ticket(lot.getId(),vehicle.id(), vehicle.getSize()));
        }

        return Optional.empty();
    }

    /**
     * Finds the lot corresponding to the {@link Ticket} and attempts to release it.
     * If successful, decrements the count of lots occupied.
     *
     * @param ticket for the lot to unpark the vehicle
     * @return True if lot was occupied by same vehicle, otherwise False
     */
    public boolean releaseLot(Ticket ticket) {
        if (ticket == null || getNumberOfLotsOccupied() <=0 || !ticket.isValid()) {
            return false;
        }

        Optional<Lot> lotOptional = findLot(ticket.getLotId());
        if(!lotOptional.isPresent()) {
            return false;
        }

        Lot lot = lotOptional.get();
        if(lot.release(ticket)) {
            numberOfLotsOccupied--;
            return true;
        }

        return false;
    }

    private Size getLotSize() {
        return size;
    }

    private Optional<Lot> findAvailableLot(Lot[] lots) {
        return Arrays.stream(lots).filter(lot -> lot.isAvailable()).findFirst();
    }

    private Optional<Lot> findLot(int lotId) {
        if (lotId >= numberOfLots) {
            return Optional.empty();
        }
        return Optional.of(lots[lotId]);
    }

    private int getNumberOfLots() {
        return numberOfLots;
    }

    /**
     *
     * @return Number of lots currently occupied by vehicles
     */
    public int getNumberOfLotsOccupied() {
        return numberOfLotsOccupied;
    }
}
