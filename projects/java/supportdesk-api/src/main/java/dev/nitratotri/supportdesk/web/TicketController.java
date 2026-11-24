package dev.nitratotri.supportdesk.web;

import dev.nitratotri.supportdesk.domain.TicketPriority;
import dev.nitratotri.supportdesk.domain.TicketStatus;
import dev.nitratotri.supportdesk.dto.CreateTicketRequest;
import dev.nitratotri.supportdesk.dto.TicketResponse;
import dev.nitratotri.supportdesk.dto.UpdateTicketStatusRequest;
import dev.nitratotri.supportdesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(
            @Valid @RequestBody CreateTicketRequest request) {
        TicketResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public Page<TicketResponse> list(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "page must be zero or greater");
        }
        if (size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "size must be between 1 and 100");
        }
        PageRequest pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return service.search(status, priority, pageable);
    }

    @GetMapping("/{id}")
    public TicketResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PatchMapping("/{id}/status")
    public TicketResponse changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        return service.changeStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
