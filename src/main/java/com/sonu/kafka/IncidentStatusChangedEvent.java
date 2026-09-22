package com.sonu.kafka;

import java.time.Instant;
import java.util.UUID;

public record IncidentStatusChangedEvent(
        UUID eventId,
        String eventType,
        Long incidentId,
        Instant timestamp,
        IncidentStatusChangedData data) {
}