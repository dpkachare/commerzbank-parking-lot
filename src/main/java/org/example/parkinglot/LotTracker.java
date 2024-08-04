package org.example;

import org.example.parkinglot.bo.Lot;
import org.example.parkinglot.bo.Size;
import org.example.parkinglot.bo.Ticket;
import org.example.parkinglot.bo.Vehicle;

import java.util.Arrays;
import java.util.Optional;

public class LotTracker {

    private int numberOfLots;
    private Lot[] lots;

    private final Size size;

    private int numberOfLotsOccupied = 0;

    public LotTracker(int numberOfLots, Size lotSize) {
        if (numberOfLots < 0 || lotSize == null) {
            throw new RuntimeException();
        }
        this.numberOfLots = numberOfLots;
        this.size = lotSize;
        initLots();
    }

    private void initLots() {
        lots = new Lot[numberOfLots];
        for (int i=0; i<numberOfLots; i++) {
            lots[i] = new Lot(i, getLotSize());
        }
    }

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
            return Optional.of(new Ticket(lot.getId(),vehicle.id()));
        }

        return Optional.empty();
    }

    public boolean releaseLot(Ticket ticket) {
        if (ticket == null || getNumberOfLotsOccupied() <=0) {
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

    public int getNumberOfLotsOccupied() {
        return numberOfLotsOccupied;
    }
}
