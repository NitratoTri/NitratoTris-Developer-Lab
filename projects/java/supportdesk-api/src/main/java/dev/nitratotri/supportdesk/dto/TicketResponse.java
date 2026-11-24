package dev.nitratotri.supportdesk.dto;

import dev.nitratotri.supportdesk.domain.Ticket;
import dev.nitratotri.supportdesk.domain.TicketPriority;
import dev.nitratotri.supportdesk.domain.TicketStatus;
import java.time.Instant;

public record TicketResponse(
        Long id,
        String title,
        String description,
        String requesterEmail,
        TicketPriority priority,
        TicketStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(), ticket.getTitle(), ticket.getDescription(),
                ticket.getRequesterEmail(), ticket.getPriority(), ticket.getStatus(),
                ticket.getCreatedAt(), ticket.getUpdatedAt()
        );
    }
}
