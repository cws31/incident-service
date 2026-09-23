package com.sonu.kafka;

import java.time.Instant;
import java.util.UUID;

public record IncidentAnalyzedEvent(
        UUID eventId,
        String eventType,
        Long incidentId,
        Instant timestamp,
        IncidentAnalyzedData data) {
}