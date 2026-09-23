package com.sonu.dto;

import java.time.LocalDateTime;

public record IncidentHistoryResponse(
        String previousStatus,
        String newStatus,
        LocalDateTime changedAt) {
}