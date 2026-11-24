package dev.nitratotri.supportdesk.repository;

import dev.nitratotri.supportdesk.domain.Ticket;
import dev.nitratotri.supportdesk.domain.TicketPriority;
import dev.nitratotri.supportdesk.domain.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("""
            select ticket from Ticket ticket
            where (:status is null or ticket.status = :status)
              and (:priority is null or ticket.priority = :priority)
            """)
    Page<Ticket> search(
            @Param("status") TicketStatus status,
            @Param("priority") TicketPriority priority,
            Pageable pageable
    );
}
