package org.example.parkinglot;

import org.example.parkinglot.LotManager;
import org.example.parkinglot.LotTracker;
import org.example.parkinglot.models.Size;
import org.example.parkinglot.models.lot.Ticket;
import org.example.parkinglot.models.vehicle.Car;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LotManagerTest {

    @Test
    public void testCannotParkIfVehicleNull() {
        LotManager lotManager = new LotManager(1, 1, 1, LotTracker.Factory.INSTANCE);
        assertNull(lotManager.park(null));
    }

    @Test
    public void testCannotUnParkWithoutTicket() {
        LotManager lotManager = new LotManager(1, 1, 1, LotTracker.Factory.INSTANCE);
        assertFalse(lotManager.unPark(null));
    }

    @Test
    public void verifyParkInvokesReserveLotApiOfTracker() {
        int noOfCarLots = 10, noOfBikeLots = 10, noOfTruckLots = 10;
        LotTracker.Factory factory = spy(LotTracker.Factory.INSTANCE);

        LotTracker smallLotTracker = spy(factory.createLotTracker(noOfBikeLots, Size.SMALL));
        LotTracker mediumLotTracker = spy(factory.createLotTracker(noOfCarLots, Size.MEDIUM));
        LotTracker largeLotTracker = spy(factory.createLotTracker(noOfTruckLots, Size.LARGE));

        when(factory.createLotTracker(noOfBikeLots, Size.SMALL)).thenReturn(smallLotTracker);
        when(factory.createLotTracker(noOfCarLots, Size.MEDIUM)).thenReturn(mediumLotTracker);
        when(factory.createLotTracker(noOfTruckLots, Size.LARGE)).thenReturn(largeLotTracker);

        LotManager lotManager = new LotManager(noOfCarLots, noOfBikeLots, noOfTruckLots, factory);

        for(int i=0; i < noOfCarLots;i++) {
            Car car = new Car(String.valueOf(i));
            Ticket ticket = lotManager.park(car);
            verify(mediumLotTracker).reserveLot(car);
            assertEquals(ticket.getVehicleId(), car.id());
            assertEquals(ticket.getVehicleSize(), car.getSize());
        }
        Car car = new Car(String.valueOf(noOfCarLots+1));
        Ticket ticket = lotManager.park(car);
        verify(mediumLotTracker).reserveLot(car);
        assertNull(ticket);
    }

    @Test
    public void verifyUnParkInvokesReleaseLotApiOfTracker() {
        int noOfCarLots = 10, noOfBikeLots = 10, noOfTruckLots = 10;
        LotTracker.Factory factory = spy(LotTracker.Factory.INSTANCE);

        LotTracker smallLotTracker = spy(factory.createLotTracker(noOfBikeLots, Size.SMALL));
        LotTracker mediumLotTracker = spy(factory.createLotTracker(noOfCarLots, Size.MEDIUM));
        LotTracker largeLotTracker = spy(factory.createLotTracker(noOfTruckLots, Size.LARGE));

        when(factory.createLotTracker(noOfBikeLots, Size.SMALL)).thenReturn(smallLotTracker);
        when(factory.createLotTracker(noOfCarLots, Size.MEDIUM)).thenReturn(mediumLotTracker);
        when(factory.createLotTracker(noOfTruckLots, Size.LARGE)).thenReturn(largeLotTracker);

        LotManager lotManager = new LotManager(noOfCarLots, noOfBikeLots, noOfTruckLots, factory);

        Ticket ticket = mock(Ticket.class);

        when(ticket.getVehicleSize()).thenReturn(Size.SMALL);
        lotManager.unPark(ticket);
        verify(smallLotTracker).releaseLot(ticket);

        when(ticket.getVehicleSize()).thenReturn(Size.MEDIUM);
        lotManager.unPark(ticket);
        verify(mediumLotTracker).releaseLot(ticket);

        when(ticket.getVehicleSize()).thenReturn(Size.LARGE);
        lotManager.unPark(ticket);
        verify(largeLotTracker).releaseLot(ticket);
    }

}
