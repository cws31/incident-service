package com.sonu.kafka;

import java.time.Instant;
import java.util.UUID;

public record IncidentCreatedEvent(
        UUID eventId,
        String eventType,
        Long incidentId,
        Instant timestamp,
        IncidentEventData data) {
}