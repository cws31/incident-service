package com.sonu.kafka;

import java.util.UUID;

public record IncidentStatusChangedEvent(
        UUID eventId,
        String eventType,
        Long incidentId,
        String timestamp,
        IncidentStatusChangedData data) {
}