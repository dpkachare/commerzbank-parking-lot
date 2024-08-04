package org.example.parkinglot.models.lot;

import org.example.parkinglot.exception.CannotIssueTicketException;
import org.example.parkinglot.models.Size;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TicketTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    public void ticketCreationWithIncorrectVehicleIdShouldThrow(String id) {
        assertThrows(CannotIssueTicketException.class, () -> new Ticket(1, id, Size.SMALL));
        assertThrows(CannotIssueTicketException.class, () -> new Ticket(1, id, Size.MEDIUM));
        assertThrows(CannotIssueTicketException.class, () -> new Ticket(1, id, Size.LARGE));
    }

    @Test
    public void ticketCreationWithIncorrectVehicleSizeShouldThrow() {
        assertThrows(CannotIssueTicketException.class, () -> new Ticket(1, "1", null));
    }

    @ParameterizedTest
    @EnumSource(Size.class)
    public void testTicket(Size size)  {
        int lotId = 1;
        String vehicleId = "Vehicle1";
        Ticket ticket = new Ticket(lotId, vehicleId, size);
        assertEquals(ticket.getLotId(), lotId);
        assertEquals(ticket.getVehicleId(), vehicleId);
        assertEquals(ticket.getVehicleSize(), size);
    }

    @Test
    public void testTicketInvalidate() {
        Ticket ticket = new Ticket(1, "Vehicle1", Size.LARGE);
        assertTrue(ticket.isValid());
        assertTrue(ticket.invalidateTicket());
        assertFalse(ticket.isValid());
    }
}
