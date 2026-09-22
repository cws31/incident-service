package com.sonu.dto;

import java.time.LocalDateTime;

import com.sonu.enums.IncidentCategory;
import com.sonu.enums.IncidentSeverity;
import com.sonu.enums.IncidentStatus;

public record IncidentResponse(

        Long id,
        String title,
        String description,
        IncidentCategory category,
        IncidentSeverity severity,
        Double latitude,
        Double longitude,
        IncidentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}