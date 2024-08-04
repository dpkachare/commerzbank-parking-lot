package org.example.parkinglot.bo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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
        Lot lot = spy(new Lot(1, size));

        when(lot.isAvailable()).thenReturn(false);
        when(vehicle.getSize()).thenReturn(size);

        assertFalse(lot.reserve(vehicle));
        assertFalse(lot.isAvailable());
    }

    @Test
    public void lotCannotBeReservedIfVehicleSizeMismatch() {
        Vehicle vehicle = mock(Vehicle.class);
        Lot lot = spy(new Lot(1, Size.SMALL));

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

//        assertTrue(ticket.isPresent());
//        assertEquals(ticket.get().getLotId(), lot.getId());

    }
}
