package dev.nitratotri.supportdesk.service;

import dev.nitratotri.supportdesk.domain.Ticket;
import dev.nitratotri.supportdesk.domain.TicketPriority;
import dev.nitratotri.supportdesk.domain.TicketStatus;
import dev.nitratotri.supportdesk.dto.CreateTicketRequest;
import dev.nitratotri.supportdesk.dto.TicketResponse;
import dev.nitratotri.supportdesk.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class TicketService {

    private final TicketRepository repository;

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    public TicketResponse create(CreateTicketRequest request) {
        Ticket ticket = new Ticket(
                request.title().trim(),
                request.description().trim(),
                request.requesterEmail().trim(),
                request.priority(),
                TicketStatus.OPEN
        );
        return TicketResponse.from(repository.save(ticket));
    }

    @Transactional(readOnly = true)
    public TicketResponse getById(Long id) {
        return TicketResponse.from(findTicket(id));
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> search(
            TicketStatus status, TicketPriority priority, Pageable pageable) {
        return repository.search(status, priority, pageable).map(TicketResponse::from);
    }

    public TicketResponse changeStatus(Long id, TicketStatus status) {
        Ticket ticket = findTicket(id);
        ticket.changeStatus(status);
        return TicketResponse.from(repository.save(ticket));
    }

    public void delete(Long id) {
        repository.delete(findTicket(id));
    }

    private Ticket findTicket(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ticket " + id + " was not found"));
    }
}
