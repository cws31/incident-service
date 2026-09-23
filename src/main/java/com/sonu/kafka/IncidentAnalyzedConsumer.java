package com.sonu.kafka;

import com.sonu.entity.Incident;
import com.sonu.enums.IncidentCategory;
import com.sonu.enums.IncidentSeverity;
import com.sonu.enums.IncidentStatus;
import com.sonu.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class IncidentAnalyzedConsumer {

    private final IncidentRepository incidentRepository;

    @KafkaListener(topics = "incident.analyzed", groupId = "incident-service")
    @Transactional
    public void consume(IncidentAnalyzedEvent event) {

        System.out.println(
                ">>> INCIDENT ANALYZED EVENT RECEIVED: " + event);

        Incident incident = incidentRepository.findById(event.incidentId())
                .orElseThrow(() -> new RuntimeException(
                        "Incident not found: " + event.incidentId()));

        incident.setCategory(
                IncidentCategory.valueOf(event.data().category()));

        incident.setSeverity(
                IncidentSeverity.valueOf(event.data().severity()));

        incident.setStatus(IncidentStatus.ANALYZED);

        incidentRepository.save(incident);

        System.out.println(
                ">>> INCIDENT UPDATED: id=" + incident.getId()
                        + ", category=" + incident.getCategory()
                        + ", severity=" + incident.getSeverity()
                        + ", status=" + incident.getStatus());
    }
}