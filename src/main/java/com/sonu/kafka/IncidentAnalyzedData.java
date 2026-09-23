package com.sonu.kafka;

import java.util.List;

public record IncidentAnalyzedData(
        String category,
        String severity,
        String summary,
        List<String> requiredTeams,
        Double confidence) {
}