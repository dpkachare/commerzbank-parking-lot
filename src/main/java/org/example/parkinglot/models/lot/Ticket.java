package org.example.parkinglot.models.lot;

import org.example.parkinglot.exception.CannotIssueTicketException;
import org.example.parkinglot.models.Size;

public class Ticket {
    private final int lotId;
    private final String vehicleId;
    private final Size vehicleSize;

    private TicketStatus status = TicketStatus.VALID;

    public Ticket(int lotId, String vehicleId, Size vehicleSize) throws CannotIssueTicketException {
        if (vehicleId == null || vehicleId.trim().isEmpty() || vehicleSize == null) {
            throw new CannotIssueTicketException();
        }
        this.lotId = lotId;
        this.vehicleId = vehicleId;
        this.vehicleSize = vehicleSize;
    }

    public int getLotId() {
        return lotId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public Size getVehicleSize() {
        return vehicleSize;
    }

    boolean invalidateTicket() {
        status = TicketStatus.INVALID;
        return true;
    }

    public boolean isValid() {
        return status==TicketStatus.VALID;
    }
}
