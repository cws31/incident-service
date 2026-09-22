package com.sonu.kafka;

public record IncidentStatusChangedData(
        String previousStatus,
        String newStatus) {
}