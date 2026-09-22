package com.sonu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonu.dto.CreateIncidentRequest;
import com.sonu.dto.IncidentResponse;
import com.sonu.dto.UpdateIncidentRequest;
import com.sonu.dto.UpdateIncidentStatusRequest;
import com.sonu.entity.Incident;
import com.sonu.enums.IncidentCategory;
import com.sonu.enums.IncidentSeverity;
import com.sonu.enums.IncidentStatus;
import com.sonu.exceptions.IncidentNotFoundException;
import com.sonu.exceptions.InvalidIncidentStatusTransitionException;
import com.sonu.repository.IncidentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    @Override
    public IncidentResponse createIncident(CreateIncidentRequest request) {

        Incident incident = new Incident();

        incident.setTitle(request.title());
        incident.setDescription(request.description());
        incident.setLatitude(request.latitude());
        incident.setLongitude(request.longitude());

        incident.setCategory(IncidentCategory.OTHER);
        incident.setSeverity(IncidentSeverity.LOW);

        incident.setStatus(
                IncidentStatus.REPORTED);

        Incident savedIncident = incidentRepository.save(incident);

        return mapToResponse(savedIncident);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidentResponse> getAllIncidents() {

        return incidentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentResponse getIncidentById(Long id) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        return mapToResponse(incident);
    }

    @Override
    public IncidentResponse updateIncident(
            Long id,
            UpdateIncidentRequest request) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        incident.setTitle(request.title());
        incident.setDescription(request.description());
        incident.setLatitude(request.latitude());
        incident.setLongitude(request.longitude());

        Incident updatedIncident = incidentRepository.save(incident);

        return mapToResponse(updatedIncident);
    }

    @Override
    public IncidentResponse updateIncidentStatus(
            Long id,
            UpdateIncidentStatusRequest request) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));

        IncidentStatus currentStatus = incident.getStatus();
        IncidentStatus requestedStatus = request.status();

        validateStatusTransition(currentStatus, requestedStatus);

        incident.setStatus(requestedStatus);

        Incident updatedIncident = incidentRepository.save(incident);

        return mapToResponse(updatedIncident);
    }

    private void validateStatusTransition(
            IncidentStatus currentStatus,
            IncidentStatus requestedStatus) {

        if (currentStatus == requestedStatus) {
            return;
        }

        boolean validTransition = switch (currentStatus) {

            case REPORTED ->
                requestedStatus == IncidentStatus.ANALYZING
                        || requestedStatus == IncidentStatus.CANCELLED;

            case ANALYZING ->
                requestedStatus == IncidentStatus.ANALYZED
                        || requestedStatus == IncidentStatus.CANCELLED;

            case ANALYZED ->
                requestedStatus == IncidentStatus.ASSIGNED
                        || requestedStatus == IncidentStatus.CANCELLED;

            case ASSIGNED ->
                requestedStatus == IncidentStatus.IN_PROGRESS
                        || requestedStatus == IncidentStatus.CANCELLED;

            case IN_PROGRESS ->
                requestedStatus == IncidentStatus.RESOLVED
                        || requestedStatus == IncidentStatus.CANCELLED;

            case RESOLVED, CANCELLED ->
                false;
        };

        if (!validTransition) {
            throw new InvalidIncidentStatusTransitionException(
                    currentStatus,
                    requestedStatus);
        }
    }

    private IncidentResponse mapToResponse(Incident incident) {

        return new IncidentResponse(
                incident.getId(),
                incident.getTitle(),
                incident.getDescription(),
                incident.getCategory(),
                incident.getSeverity(),
                incident.getLatitude(),
                incident.getLongitude(),
                incident.getStatus(),
                incident.getCreatedAt(),
                incident.getUpdatedAt());
    }
}