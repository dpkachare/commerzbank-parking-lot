package org.example.parkinglot.models;

import org.example.parkinglot.LotTracker;

public interface LotTrackerFactory {
    public LotTracker createLotTracker(int numberOfLots, Size size);
}
