package com.sonu.kafka;

import com.sonu.entity.Incident;
import com.sonu.entity.IncidentHistory;
import com.sonu.enums.IncidentCategory;
import com.sonu.enums.IncidentSeverity;
import com.sonu.enums.IncidentStatus;
import com.sonu.repository.IncidentHistoryRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class IncidentAnalyzedConsumerTest {

        @Mock
        private IncidentRepository incidentRepository;

        @Mock
        private IncidentHistoryRepository incidentHistoryRepository;

        @InjectMocks
        private IncidentAnalyzedConsumer incidentAnalyzedConsumer;

        @Test
        void consume_shouldUpdateIncidentWithAiAnalysis() {

                UUID eventId = UUID.randomUUID();

                Incident incident = createIncident();

                IncidentAnalyzedEvent event = createEvent(eventId);

                when(incidentHistoryRepository.existsByEventId(eventId))
                                .thenReturn(false);

                when(incidentRepository.findById(9L))
                                .thenReturn(Optional.of(incident));

                when(incidentRepository.save(incident))
                                .thenReturn(incident);

                incidentAnalyzedConsumer.consume(event);

                assertEquals(
                                IncidentCategory.FIRE,
                                incident.getCategory());

                assertEquals(
                                IncidentSeverity.CRITICAL,
                                incident.getSeverity());

                assertEquals(
                                IncidentStatus.ANALYZED,
                                incident.getStatus());

                verify(incidentRepository).findById(9L);
                verify(incidentRepository).save(incident);

                verify(incidentHistoryRepository)
                                .save(any(IncidentHistory.class));
        }

        @Test
        void consume_shouldIgnoreDuplicateEvent() {

                UUID eventId = UUID.randomUUID();

                IncidentAnalyzedEvent event = createEvent(eventId);

                when(incidentHistoryRepository.existsByEventId(eventId))
                                .thenReturn(true);

                incidentAnalyzedConsumer.consume(event);

                verify(incidentHistoryRepository)
                                .existsByEventId(eventId);

                verify(incidentRepository, never())
                                .findById(9L);

                verify(incidentRepository, never())
                                .save(any(Incident.class));

                verify(incidentHistoryRepository, never())
                                .save(any(IncidentHistory.class));
        }

        private Incident createIncident() {

                Incident incident = new Incident();

                incident.setId(9L);
                incident.setTitle("Test building fire");
                incident.setDescription(
                                "Fire reported in a residential building.");
                incident.setLatitude(16.3);
                incident.setLongitude(80.43);
                incident.setCategory(IncidentCategory.OTHER);
                incident.setSeverity(IncidentSeverity.LOW);
                incident.setStatus(IncidentStatus.ANALYZING);

                return incident;
        }

        private IncidentAnalyzedEvent createEvent(UUID eventId) {

                return new IncidentAnalyzedEvent(
                                eventId,
                                "INCIDENT_ANALYZED",
                                9L,
                                Instant.parse("2026-09-23T05:45:00Z"),
                                new IncidentAnalyzedData(
                                                "FIRE",
                                                "CRITICAL",
                                                "Major building fire",
                                                List.of("FIRE", "MEDICAL"),
                                                0.94));
        }
}