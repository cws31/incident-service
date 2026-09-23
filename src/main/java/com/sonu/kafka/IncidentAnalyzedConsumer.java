package com.sonu.kafka;

import com.sonu.entity.Incident;
import com.sonu.entity.IncidentHistory;
import com.sonu.enums.IncidentCategory;
import com.sonu.enums.IncidentSeverity;
import com.sonu.enums.IncidentStatus;
import com.sonu.repository.IncidentHistoryRepository;
import com.sonu.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class IncidentAnalyzedConsumer {

    private final IncidentRepository incidentRepository;
    private final IncidentHistoryRepository incidentHistoryRepository;

    @KafkaListener(topics = "incident.analyzed", groupId = "incident-service")
    @Transactional
    public void consume(IncidentAnalyzedEvent event) {

        System.out.println(
                ">>> INCIDENT ANALYZED EVENT RECEIVED: " + event);

        if (incidentHistoryRepository.existsByEventId(event.eventId())) {
            System.out.println(
                    ">>> DUPLICATE INCIDENT ANALYZED EVENT IGNORED: "
                            + event.eventId());
            return;
        }

        Incident incident = incidentRepository.findById(event.incidentId())
                .orElseThrow(() -> new RuntimeException(
                        "Incident not found: " + event.incidentId()));

        IncidentStatus previousStatus = incident.getStatus();

        incident.setCategory(
                IncidentCategory.valueOf(event.data().category()));

        incident.setSeverity(
                IncidentSeverity.valueOf(event.data().severity()));

        incident.setStatus(IncidentStatus.ANALYZED);

        Incident updatedIncident = incidentRepository.save(incident);

        if (previousStatus != IncidentStatus.ANALYZED) {

            IncidentHistory history = new IncidentHistory();

            history.setIncidentId(updatedIncident.getId());
            history.setEventId(event.eventId());
            history.setPreviousStatus(previousStatus.name());
            history.setNewStatus(IncidentStatus.ANALYZED.name());

            incidentHistoryRepository.save(history);
        }

        System.out.println(
                ">>> INCIDENT UPDATED: id=" + updatedIncident.getId()
                        + ", category=" + updatedIncident.getCategory()
                        + ", severity=" + updatedIncident.getSeverity()
                        + ", status=" + updatedIncident.getStatus());
    }
}