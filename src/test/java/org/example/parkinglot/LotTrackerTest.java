package org.example.parkinglot.bo;

import org.example.LotTracker;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LotTrackerTest {

    private LotTracker smallLotTracker = new LotTracker(10, Size.SMALL);
    private LotTracker mediumLotTracker = new LotTracker(10, Size.MEDIUM);
    private LotTracker largeLotTracker = new LotTracker(10, Size.LARGE);

    @ParameterizedTest
    @ValueSource(ints = {-2, -1, Integer.MIN_VALUE})
    public void lotTrackerWithNegativeCountOfLotsShouldThrow(int numberOfLots) {
        assertThrows(RuntimeException.class, () -> new LotTracker(numberOfLots, Size.SMALL));
        assertThrows(RuntimeException.class, () -> new LotTracker(numberOfLots, Size.MEDIUM));
        assertThrows(RuntimeException.class, () -> new LotTracker(numberOfLots, Size.LARGE));
    }
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, Integer.MAX_VALUE})
    public void lotTrackerWithNullLotSizeShouldThrow(int numberOfLots) {
        assertThrows(RuntimeException.class, () -> new LotTracker(numberOfLots, null));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10})
    public void lotTrackerWithPositiveCountAndSizeShouldNotThrow(int numberOfLots) {
        assertDoesNotThrow(() -> new LotTracker(numberOfLots, Size.SMALL));
        assertDoesNotThrow(() -> new LotTracker(numberOfLots, Size.MEDIUM));
        assertDoesNotThrow(() -> new LotTracker(numberOfLots, Size.LARGE));
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotTrackerDoesNotIssueTicketIfVehicleNull(Size size) {
        LotTracker tracker = new LotTracker(10, size);
        assertFalse(tracker.reserveLot(null).isPresent());
    }

    @ParameterizedTest
    @MethodSource(value = "incompatibleSizes")
    public void testLotTrackerDoesNotIssueTicketIfVehicleSizeIncompatible(Size lotSize, Size vehicleSize) {
        Vehicle vehicle = mock(Vehicle.class);
        LotTracker tracker = new LotTracker(10, lotSize);

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
        LotTracker lotTracker = spy(new LotTracker(numberOfLots, lotSize));
        when(lotTracker.getNumberOfLotsOccupied()).thenReturn(numberOfLots);

        Vehicle vehicle = mock(Vehicle.class);
        assertFalse(lotTracker.reserveLot(vehicle).isPresent());
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotTrackerIssuesTicket(Size lotSize) {
        int numberOfLots = 10;
        LotTracker lotTracker = new LotTracker(numberOfLots, lotSize);
        Vehicle vehicle = mock(Vehicle.class);

        when(vehicle.getSize()).thenReturn(lotSize);

        Optional<Ticket> ticketOptional = lotTracker.reserveLot(vehicle);
        assertTrue(ticketOptional.isPresent());
    }

    @Test
    public void testLotTrackerIssuesTicketForMultipleVehicle() {
        int numberOfLots = 10;
        LotTracker lotTracker = new LotTracker(numberOfLots, Size.SMALL);
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getSize()).thenReturn(Size.SMALL);

        for (int i=0; i<numberOfLots; i++) {
            Optional<Ticket> ticket = lotTracker.reserveLot(vehicle);
            assertTrue(ticket.isPresent());
            assertEquals(lotTracker.getNumberOfLotsOccupied(), i+1);
        }
    }

    @Test
    public void testLotTrackerDoesNotIssueTicketWhenVehicleCountExceedsLotCount() {
        int numberOfLots = 10;
        LotTracker lotTracker = new LotTracker(numberOfLots, Size.SMALL);
        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.getSize()).thenReturn(Size.SMALL);

        for (int i=0; i<numberOfLots; i++) {
            Optional<Ticket> ticket = lotTracker.reserveLot(vehicle);
            assertTrue(ticket.isPresent());
            assertEquals(lotTracker.getNumberOfLotsOccupied(), i+1);
        }

        Optional<Ticket> ticket = lotTracker.reserveLot(vehicle);
        assertFalse(ticket.isPresent());
        assertEquals(lotTracker.getNumberOfLotsOccupied(), numberOfLots);
    }


}
