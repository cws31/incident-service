package com.sonu.service;

import com.sonu.dto.CreateIncidentRequest;
import com.sonu.dto.IncidentHistoryResponse;
import com.sonu.dto.IncidentResponse;
import com.sonu.dto.IncidentStatusResponse;
import com.sonu.dto.UpdateIncidentStatusRequest;
import com.sonu.entity.Incident;
import com.sonu.entity.IncidentHistory;
import com.sonu.enums.IncidentStatus;
import com.sonu.exceptions.IncidentNotFoundException;
import com.sonu.exceptions.InvalidIncidentStatusTransitionException;
import com.sonu.kafka.EventPublisher;
import com.sonu.repository.IncidentHistoryRepository;
import com.sonu.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceImplTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentHistoryRepository incidentHistoryRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private IncidentServiceImpl incidentService;

    private Incident incident;

    @BeforeEach
    void setUp() {
        incident = new Incident();

        incident.setId(1L);
        incident.setTitle("Test Incident");
        incident.setDescription("Test description");
        incident.setLatitude(16.3);
        incident.setLongitude(80.43);
        incident.setStatus(IncidentStatus.REPORTED);
    }

    @Test
    void createIncident_shouldCreateIncidentSuccessfully() {

        CreateIncidentRequest request = new CreateIncidentRequest(
                "Test Incident",
                "Test description",
                16.3,
                80.43);

        when(incidentRepository.save(any(Incident.class)))
                .thenReturn(incident);

        IncidentResponse response = incidentService.createIncident(request);

        assertEquals(1L, response.id());
        assertEquals("Test Incident", response.title());
        assertEquals(IncidentStatus.REPORTED, response.status());

        verify(incidentRepository).save(any(Incident.class));
        verify(eventPublisher).publishIncidentCreated(incident);
    }

    @Test
    void getIncidentById_shouldReturnIncident() {

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        IncidentResponse response = incidentService.getIncidentById(1L);

        assertEquals(1L, response.id());
        assertEquals("Test Incident", response.title());

        verify(incidentRepository).findById(1L);
    }

    @Test
    void getIncidentById_shouldThrowExceptionWhenNotFound() {

        when(incidentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IncidentNotFoundException.class,
                () -> incidentService.getIncidentById(99L));
    }

    @Test
    void updateIncidentStatus_shouldUpdateStatusAndCreateHistory() {

        UpdateIncidentStatusRequest request = new UpdateIncidentStatusRequest(
                IncidentStatus.ANALYZING);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(incidentRepository.save(any(Incident.class)))
                .thenReturn(incident);

        IncidentStatusResponse response = incidentService.updateIncidentStatus(1L, request);

        assertEquals(IncidentStatus.ANALYZING.name(), response.status());
        assertEquals(
                IncidentStatus.ANALYZING,
                incident.getStatus());

        verify(incidentRepository).save(incident);

        verify(incidentHistoryRepository).save(
                argThat(history -> history.getIncidentId().equals(1L)
                        && history.getPreviousStatus()
                                .equals("REPORTED")
                        && history.getNewStatus()
                                .equals("ANALYZING")));

        verify(eventPublisher).publishIncidentStatusChanged(
                incident,
                "REPORTED",
                "ANALYZING");
    }

    @Test
    void updateIncidentStatus_shouldRejectInvalidTransition() {

        incident.setStatus(IncidentStatus.REPORTED);

        UpdateIncidentStatusRequest request = new UpdateIncidentStatusRequest(
                IncidentStatus.RESOLVED);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        assertThrows(
                InvalidIncidentStatusTransitionException.class,
                () -> incidentService.updateIncidentStatus(1L, request));

        verify(incidentRepository, never())
                .save(any());

        verify(incidentHistoryRepository, never())
                .save(any());

        verify(eventPublisher, never())
                .publishIncidentStatusChanged(
                        any(),
                        anyString(),
                        anyString());
    }

    @Test
    void getIncidentHistory_shouldReturnHistory() {

        IncidentHistory history = new IncidentHistory();

        history.setIncidentId(1L);
        history.setPreviousStatus("REPORTED");
        history.setNewStatus("ANALYZING");

        LocalDateTime changedAt = LocalDateTime.of(2026, 9, 23, 6, 0);

        history.setChangedAt(changedAt);

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(
                incidentHistoryRepository
                        .findByIncidentIdOrderByChangedAtAsc(1L))
                .thenReturn(List.of(history));

        List<IncidentHistoryResponse> response = incidentService.getIncidentHistory(1L);

        assertEquals(1, response.size());

        assertEquals(
                "REPORTED",
                response.get(0).previousStatus());

        assertEquals(
                "ANALYZING",
                response.get(0).newStatus());

        assertEquals(
                changedAt,
                response.get(0).changedAt());
    }

    @Test
    void getIncidentHistory_shouldThrowExceptionWhenIncidentNotFound() {

        when(incidentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IncidentNotFoundException.class,
                () -> incidentService.getIncidentHistory(99L));

        verify(
                incidentHistoryRepository,
                never()).findByIncidentIdOrderByChangedAtAsc(99L);
    }
}