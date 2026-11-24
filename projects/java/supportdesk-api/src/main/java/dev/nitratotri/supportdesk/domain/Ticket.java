package dev.nitratotri.supportdesk.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tickets", indexes = {
        @Index(name = "idx_tickets_status_priority", columnList = "status, priority"),
        @Index(name = "idx_tickets_created_at", columnList = "created_at")
})
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(name = "requester_email", nullable = false, length = 254)
    private String requesterEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TicketStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Ticket() { }

    public Ticket(String title, String description, String requesterEmail,
                  TicketPriority priority, TicketStatus status) {
        this.title = title;
        this.description = description;
        this.requesterEmail = requesterEmail;
        this.priority = priority;
        this.status = status;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void changeStatus(TicketStatus status) { this.status = status; }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRequesterEmail() { return requesterEmail; }
    public TicketPriority getPriority() { return priority; }
    public TicketStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
