package com.sonu.service;

import java.util.List;

import com.sonu.dto.CreateIncidentRequest;
import com.sonu.dto.IncidentResponse;
import com.sonu.dto.UpdateIncidentRequest;
import com.sonu.dto.UpdateIncidentStatusRequest;

public interface IncidentService {

    IncidentResponse createIncident(CreateIncidentRequest request);

    List<IncidentResponse> getAllIncidents();

    IncidentResponse getIncidentById(Long id);

    IncidentResponse updateIncident(Long id, UpdateIncidentRequest request);

    IncidentResponse updateIncidentStatus(
            Long id,
            UpdateIncidentStatusRequest request);
}