package dev.nitratotri.supportdesk;

import dev.nitratotri.supportdesk.domain.Ticket;
import dev.nitratotri.supportdesk.domain.TicketPriority;
import dev.nitratotri.supportdesk.domain.TicketStatus;
import dev.nitratotri.supportdesk.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TicketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void createsTicketAndReturnsLocation() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Cannot sign in",
                                  "description": "The reset link has expired.",
                                  "requesterEmail": "alex@example.com",
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/tickets/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void rejectsInvalidTicketData() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": " ",
                                  "description": "A valid description",
                                  "requesterEmail": "not-an-email",
                                  "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/tickets"));
    }

    @Test
    void filtersTicketsByStatusAndPriority() throws Exception {
        repository.save(ticket("Billing issue", TicketPriority.HIGH));
        repository.save(ticket("Password reset", TicketPriority.LOW));

        mockMvc.perform(get("/api/tickets")
                        .param("status", "OPEN")
                        .param("priority", "HIGH")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Billing issue"));
    }

    @Test
    void changesTicketStatus() throws Exception {
        Ticket ticket = repository.save(ticket("Payment failed", TicketPriority.URGENT));

        mockMvc.perform(patch("/api/tickets/{id}/status", ticket.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void returnsNotFoundForUnknownTicket() throws Exception {
        mockMvc.perform(get("/api/tickets/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Ticket 99999 was not found"));
    }

    @Test
    void deletesTicketAndThenReturnsNotFound() throws Exception {
        Ticket ticket = repository.save(ticket("Duplicate request", TicketPriority.MEDIUM));

        mockMvc.perform(delete("/api/tickets/{id}", ticket.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tickets/{id}", ticket.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsOutOfRangePageSize() throws Exception {
        mockMvc.perform(get("/api/tickets").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("size must be between 1 and 100"));
    }

    private Ticket ticket(String title, TicketPriority priority) {
        return new Ticket(
                title,
                "Description for " + title,
                "requester@example.com",
                priority,
                TicketStatus.OPEN
        );
    }
}
