package com.sonu.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.sonu.entity.Incident;
import org.springframework.kafka.support.SendResult;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IncidentEventProducer implements EventPublisher {

    private static final String INCIDENT_CREATED_TOPIC = "incident.created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishIncidentCreated(Incident incident) {

        IncidentEventData data = new IncidentEventData(
                incident.getTitle(),
                incident.getDescription(),
                incident.getLatitude(),
                incident.getLongitude());

        IncidentCreatedEvent event = new IncidentCreatedEvent(
                UUID.randomUUID(),
                "INCIDENT_CREATED",
                incident.getId(),
                Instant.now().toString(),
                data);

        kafkaTemplate.send(
                INCIDENT_CREATED_TOPIC,
                String.valueOf(incident.getId()),
                event).whenComplete((result, exception) -> {
                    if (exception != null) {
                        System.err.println(
                                "Failed to publish incident.created event for incident "
                                        + incident.getId()
                                        + ": "
                                        + exception.getMessage());
                    }
                });
    }

    @Override
    public void publishIncidentStatusChanged(
            Incident incident,
            String previousStatus,
            String newStatus) {
        IncidentStatusChangedData data = new IncidentStatusChangedData(
                previousStatus,
                newStatus);

        IncidentStatusChangedEvent event = new IncidentStatusChangedEvent(
                UUID.randomUUID(),
                "INCIDENT_STATUS_CHANGED",
                incident.getId(),
                Instant.now().toString(),
                data);
        kafkaTemplate.send(
                "incident.status.changed",
                String.valueOf(incident.getId()),
                event).whenComplete((result, exception) -> {
                    if (exception != null) {
                        System.err.println(
                                "Failed to publish incident.status.changed event for incident "
                                        + incident.getId()
                                        + ": "
                                        + exception.getMessage());
                    }
                });
    }
}