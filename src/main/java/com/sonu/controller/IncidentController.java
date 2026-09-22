package com.sonu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sonu.dto.CreateIncidentRequest;
import com.sonu.dto.IncidentResponse;
import com.sonu.dto.UpdateIncidentRequest;
import com.sonu.dto.UpdateIncidentStatusRequest;
import com.sonu.service.IncidentService;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(
            @Valid @RequestBody CreateIncidentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(incidentService.createIncident(request));
    }

    @GetMapping
    public ResponseEntity<List<IncidentResponse>> getAllIncidents() {

        return ResponseEntity.ok(
                incidentService.getAllIncidents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                incidentService.getIncidentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentResponse> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIncidentRequest request) {

        return ResponseEntity.ok(
                incidentService.updateIncident(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentResponse> updateIncidentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIncidentStatusRequest request) {

        return ResponseEntity.ok(
                incidentService.updateIncidentStatus(id, request));
    }
}