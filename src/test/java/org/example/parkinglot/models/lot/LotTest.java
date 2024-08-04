package org.example.parkinglot.models.lot;

import org.example.parkinglot.models.Size;
import org.example.parkinglot.models.vehicle.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LotTest {

    @Test
    public void creatingLotWithoutSizeShouldThrow() {
        assertThrows(RuntimeException.class, ()->new Lot(1, null));
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testDefaultLotStatusIsAvailable(Size size) {
        Lot lot = new Lot(1, size);
        assertTrue(lot.isAvailable());
        assertFalse(lot.getVehicle().isPresent());
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void lotCannotBeReservedIfVehicleNull(Size size) {
        Lot lot = new Lot(1, size);
        assertFalse(lot.reserve(null));
        assertTrue(lot.isAvailable());
        assertFalse(lot.getVehicle().isPresent());
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void lotCannotBeReservedIfAlreadyOccupied(Size size) {
        Vehicle vehicle = mock(Vehicle.class);
        Lot lot = Mockito.spy(new Lot(1, size));

        when(lot.isAvailable()).thenReturn(false);
        when(vehicle.getSize()).thenReturn(size);

        assertFalse(lot.reserve(vehicle));
        assertFalse(lot.isAvailable());
    }

    @Test
    public void lotCannotBeReservedIfVehicleSizeMismatch() {
        Vehicle vehicle = mock(Vehicle.class);
        Lot lot = Mockito.spy(new Lot(1, Size.SMALL));

        when(vehicle.getSize()).thenReturn(Size.MEDIUM);
        assertFalse(lot.reserve(vehicle));
        assertTrue(lot.isAvailable());
        assertFalse(lot.getVehicle().isPresent());
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotReservation(Size size) {
        Vehicle vehicle = mock(Vehicle.class);
        Lot lot = new Lot(1, size);

        when(vehicle.getSize()).thenReturn(size);

        assertTrue(lot.reserve(vehicle));
        assertFalse(lot.isAvailable());
        assertTrue(lot.getVehicle().isPresent());
        assertEquals(lot.getVehicle().get(), vehicle);
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotCannotReleaseVehicleWithoutTicket(Size size) {
        Lot lot = new Lot(1, size);
        assertFalse(lot.release(null));
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotCannotReleaseIfItWasNotOccupied(Size size) {
        Lot lot = new Lot(1, size);
        Ticket ticket = mock(Ticket.class);
        assertFalse(lot.release(ticket));
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotCannotReleaseIfTicketDoesNotBelongToIt(Size size) {
        Lot lot = spy(new Lot(0, size));
        Ticket ticket = mock(Ticket.class);
        when(ticket.getLotId()).thenReturn(1);
        when(lot.isAvailable()).thenReturn(false);
        when(lot.getVehicle()).thenReturn(Optional.of(mock(Vehicle.class)));
        assertFalse(lot.release(ticket));
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotCannotReleaseIfTicketOfAnotherVehicleIsPresented(Size size) {
        Lot lot = spy(new Lot(0, size));
        Ticket ticket = mock(Ticket.class);
        when(ticket.getLotId()).thenReturn(0);
        when(lot.isAvailable()).thenReturn(false);
        when(ticket.getVehicleId()).thenReturn("Vehicle2");

        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.id()).thenReturn("Vehicle1");
        when(lot.getVehicle()).thenReturn(Optional.of(vehicle));

        assertFalse(lot.release(ticket));
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testLotDoesReleaseIfCorrectTicketIsPresented(Size size) {
        Lot lot = spy(new Lot(0, size));
        Ticket ticket = mock(Ticket.class);
        when(ticket.getLotId()).thenReturn(0);
        when(lot.isAvailable()).thenReturn(false);
        when(ticket.getVehicleId()).thenReturn("Vehicle1");

        Vehicle vehicle = mock(Vehicle.class);
        when(vehicle.id()).thenReturn("Vehicle1");
        when(vehicle.getSize()).thenReturn(size);

        when(lot.getVehicle()).thenReturn(Optional.of(vehicle));

        assertTrue(lot.release(ticket));
    }
}
