package org.example.parkinglot;

import org.example.parkinglot.models.Size;
import org.example.parkinglot.models.lot.Ticket;
import org.example.parkinglot.models.vehicle.Bike;
import org.example.parkinglot.models.vehicle.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LotTrackerTest {
    @ParameterizedTest
    @ValueSource(ints = {-2, -1, Integer.MIN_VALUE})
    public void lotTrackerWithNegativeCountOfLotsShouldThrow(int numberOfLots) {
        assertThrows(RuntimeException.class, () -> LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, Size.SMALL));
        assertThrows(RuntimeException.class, () -> LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, Size.MEDIUM));
        assertThrows(RuntimeException.class, () -> LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, Size.LARGE));
    }
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, Integer.MAX_VALUE})
    public void lotTrackerWithNullLotSizeShouldThrow(int numberOfLots) {
        assertThrows(RuntimeException.class, () -> LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, null));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    public void lotTrackerWithPositiveCountAndSizeShouldNotThrow(int numberOfLots) {
        assertDoesNotThrow(() -> LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, Size.SMALL));
        assertDoesNotThrow(() -> LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, Size.MEDIUM));
        assertDoesNotThrow(() -> LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, Size.LARGE));
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotTrackerDoesNotIssueTicketIfVehicleNull(Size size) {
        LotTracker tracker = LotTracker.Factory.INSTANCE.createLotTracker(10, size);
        assertFalse(tracker.reserveLot(null).isPresent());
    }

    @ParameterizedTest
    @MethodSource(value = "incompatibleSizes")
    public void testLotTrackerDoesNotIssueTicketIfVehicleSizeIncompatible(Size lotSize, Size vehicleSize) {
        Vehicle vehicle = mock(Vehicle.class);
        LotTracker tracker = LotTracker.Factory.INSTANCE.createLotTracker(10, lotSize);

        when(vehicle.getSize()).thenReturn(vehicleSize);
        assertFalse(tracker.reserveLot(vehicle).isPresent());
    }

    private static Stream<Arguments> incompatibleSizes() {
        return Stream.of(
                Arguments.of(Size.SMALL, Size.MEDIUM),
                Arguments.of(Size.SMALL, Size.LARGE),
                Arguments.of(Size.MEDIUM, Size.SMALL),
                Arguments.of(Size.MEDIUM, Size.LARGE),
                Arguments.of(Size.LARGE, Size.SMALL),
                Arguments.of(Size.LARGE, Size.MEDIUM)
        );
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotTrackerDoesNotIssueTicketIfLotsAreFull(Size lotSize) {
        int numberOfLots = 10;
        LotTracker lotTracker = spy(LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, lotSize));
        when(lotTracker.getNumberOfLotsOccupied()).thenReturn(numberOfLots);

        Vehicle vehicle = mock(Vehicle.class);
        assertFalse(lotTracker.reserveLot(vehicle).isPresent());
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotTrackerIssuesTicket(Size lotSize) {
        int numberOfLots = 10;
        LotTracker lotTracker = LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, lotSize);
        Vehicle vehicle = mock(Vehicle.class);

        when(vehicle.getSize()).thenReturn(lotSize);
        when(vehicle.id()).thenReturn("Vehicle1");

        Optional<Ticket> ticketOptional = lotTracker.reserveLot(vehicle);
        assertTrue(ticketOptional.isPresent());
    }

    @Test
    public void testLotTrackerIssuesTicketForMultipleVehicle() {
        int numberOfLots = 10;
        LotTracker lotTracker = LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, Size.SMALL);
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getSize()).thenReturn(Size.SMALL);

        for (int i=0; i<numberOfLots; i++) {
            when(vehicle.id()).thenReturn(String.valueOf(i));
            Optional<Ticket> ticket = lotTracker.reserveLot(vehicle);
            assertTrue(ticket.isPresent());
            assertEquals(lotTracker.getNumberOfLotsOccupied(), i+1);
        }
    }

    @Test
    public void testLotTrackerDoesNotIssueTicketWhenVehicleCountExceedsLotCount() {
        int numberOfLots = 10;
        LotTracker lotTracker = LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, Size.SMALL);
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getSize()).thenReturn(Size.SMALL);
        when(vehicle.id()).thenReturn("Vehicle1");

        for (int i=0; i<numberOfLots; i++) {
            Optional<Ticket> ticket = lotTracker.reserveLot(vehicle);
            assertTrue(ticket.isPresent());
            assertEquals(lotTracker.getNumberOfLotsOccupied(), i+1);
        }

        Optional<Ticket> ticket = lotTracker.reserveLot(vehicle);
        assertFalse(ticket.isPresent());
        assertEquals(lotTracker.getNumberOfLotsOccupied(), numberOfLots);
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    //This tests scenario when Ticket is lost or possible Tail gate during entry(hence no ticket available)
    public void testLotTrackerDoesNotReleaseWithoutTicket(Size size) {
        LotTracker tracker = LotTracker.Factory.INSTANCE.createLotTracker(10, size);
        assertFalse(tracker.releaseLot(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1 , 0 ,Integer.MIN_VALUE})
    public void testLotTrackerDoesNotReleaseIfLotNotOccupied(int noOfLotsOccupied) {
        LotTracker tracker = spy(LotTracker.Factory.INSTANCE.createLotTracker(10, Size.SMALL));
        Ticket ticket = mock(Ticket.class);
        when(tracker.getNumberOfLotsOccupied()).thenReturn(noOfLotsOccupied);
        assertFalse(tracker.releaseLot(ticket));
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11 , 100 ,Integer.MAX_VALUE})
    public void testLotTrackerDoesNotReleaseWhenTicketWithInvalidLotIdIsPresented(int invalidLotId) {
        LotTracker tracker = spy(LotTracker.Factory.INSTANCE.createLotTracker(10, Size.SMALL));
        Ticket ticket = mock(Ticket.class);
        when(ticket.getLotId()).thenReturn(invalidLotId);
        when(tracker.getNumberOfLotsOccupied()).thenReturn(10);
        assertFalse(tracker.releaseLot(ticket));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 4 ,9})
    public void testTrackerDoesNotReleaseWhenTicketForLotWhichWasNotOccupiedIsPresented(int lotIdNotOccupied) {
        LotTracker tracker = spy(LotTracker.Factory.INSTANCE.createLotTracker(10, Size.SMALL));
        Ticket ticket = mock(Ticket.class);
        when(ticket.getLotId()).thenReturn(lotIdNotOccupied);
        when(tracker.getNumberOfLotsOccupied()).thenReturn(1);
        assertFalse(tracker.releaseLot(ticket));
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotTrackerReleasesWhenCorrectTicketIssued(Size size) {
        int numberOfLots = 10;
        LotTracker lotTracker = LotTracker.Factory.INSTANCE.createLotTracker(numberOfLots, size);
        List<Optional<Ticket>> tickets = new ArrayList<>();

        //10 vehicles come in to Park
        for (int i=0; i<numberOfLots; i++) {
            Vehicle vehicle = mock(Vehicle.class);
            when(vehicle.getSize()).thenReturn(size);
            when(vehicle.id()).thenReturn(String.valueOf(i));
            tickets.add(lotTracker.reserveLot(vehicle));
        }

        //10 Vehicles unpark
        for (int i=0; i< tickets.size(); i++) {
            assertTrue(lotTracker.releaseLot(tickets.get(i).get()));
            assertEquals(lotTracker.getNumberOfLotsOccupied(), numberOfLots - i - 1);
        }

        //11th Vehicle Unpark fails as there were only 10 vehicles to begin with
        assertFalse(lotTracker.releaseLot(tickets.get(0).get()));
        // count of lots occupied remains 0(does not become negative) even if 11th vehicle tries to unpark
        assertEquals(lotTracker.getNumberOfLotsOccupied(), 0);
    }

    @Test
    public void testVehicleIsNotReleasedIfSameTicketUsedMultipleTimes() {
        LotTracker tracker = spy(LotTracker.Factory.INSTANCE.createLotTracker(10, Size.SMALL));
        when(tracker.getNumberOfLotsOccupied()).thenReturn(5);

        Vehicle vehicle = new Bike("Vehicle1");

        Ticket ticket = tracker.reserveLot(vehicle).get();
        assertTrue(tracker.releaseLot(ticket));
        assertFalse(ticket.isValid());
        assertFalse(tracker.releaseLot(ticket));
    }

}
