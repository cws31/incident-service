package com.sonu.kafka;

public record IncidentEventData(
                String title,
                String description,
                Double latitude,
                Double longitude) {
}
