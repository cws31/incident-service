package com.sonu.dto;

import java.time.LocalDateTime;

public record IncidentStatusResponse(
        Long id,
        String status,
        LocalDateTime updatedAt) {
}