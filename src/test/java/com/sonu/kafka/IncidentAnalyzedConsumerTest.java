package com.sonu.kafka;

import com.sonu.entity.Incident;
import com.sonu.enums.IncidentCategory;
import com.sonu.enums.IncidentSeverity;
import com.sonu.enums.IncidentStatus;
import com.sonu.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentAnalyzedConsumerTest {

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private IncidentAnalyzedConsumer incidentAnalyzedConsumer;

    @Test
    void consume_shouldUpdateIncidentWithAiAnalysis() {

        Incident incident = new Incident();
        incident.setId(9L);
        incident.setTitle("Test building fire");
        incident.setDescription("Fire reported in a residential building.");
        incident.setLatitude(16.3);
        incident.setLongitude(80.43);
        incident.setCategory(IncidentCategory.OTHER);
        incident.setSeverity(IncidentSeverity.LOW);
        incident.setStatus(IncidentStatus.ANALYZING);

        IncidentAnalyzedEvent event = new IncidentAnalyzedEvent(
                UUID.randomUUID(),
                "INCIDENT_ANALYZED",
                9L,
                Instant.parse("2026-09-23T05:45:00Z"),
                new IncidentAnalyzedData(
                        "FIRE",
                        "CRITICAL",
                        "Major building fire",
                        List.of("FIRE", "MEDICAL"),
                        0.94));

        when(incidentRepository.findById(9L))
                .thenReturn(Optional.of(incident));

        incidentAnalyzedConsumer.consume(event);

        assertEquals(IncidentCategory.FIRE, incident.getCategory());
        assertEquals(IncidentSeverity.CRITICAL, incident.getSeverity());
        assertEquals(IncidentStatus.ANALYZED, incident.getStatus());

        verify(incidentRepository).findById(9L);
        verify(incidentRepository).save(incident);
    }
}