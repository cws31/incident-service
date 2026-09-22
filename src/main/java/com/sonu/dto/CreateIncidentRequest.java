package com.sonu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateIncidentRequest(

        @NotBlank(message = "Title is required") String title,

        @NotBlank(message = "Description is required") String description,

        @NotNull(message = "Latitude is required") Double latitude,

        @NotNull(message = "Longitude is required") Double longitude) {
}