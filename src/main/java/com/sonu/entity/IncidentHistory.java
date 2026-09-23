package com.sonu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incident_history", uniqueConstraints = {
        @UniqueConstraint(name = "uk_incident_history_event_id", columnNames = "event_id")
})
@Getter
@Setter
@NoArgsConstructor
public class IncidentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long incidentId;

    @Column(name = "event_id", unique = true)
    private UUID eventId;

    private String previousStatus;

    private String newStatus;

    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}