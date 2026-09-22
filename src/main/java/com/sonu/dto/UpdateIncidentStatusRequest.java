package com.sonu.dto;

import com.sonu.enums.IncidentStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateIncidentStatusRequest(

        @NotNull(message = "Status is required") IncidentStatus status) {
}