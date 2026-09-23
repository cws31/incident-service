package com.sonu.kafka;

import com.sonu.entity.Incident;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IncidentEventProducer implements EventPublisher {

    private static final String INCIDENT_CREATED_TOPIC = "incident.created";
    private static final String INCIDENT_STATUS_CHANGED_TOPIC =
            "incident.status.changed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishIncidentCreated(Incident incident) {

        IncidentEventData data = new IncidentEventData(
                incident.getTitle(),
                incident.getDescription(),
                incident.getLatitude(),
                incident.getLongitude()
        );

        IncidentCreatedEvent event = new IncidentCreatedEvent(
                UUID.randomUUID(),
                "INCIDENT_CREATED",
                incident.getId(),
                Instant.now().toString(),
                data
        );

        publishSafely(
                INCIDENT_CREATED_TOPIC,
                String.valueOf(incident.getId()),
                event,
                incident.getId()
        );
    }

    @Override
    public void publishIncidentStatusChanged(
            Incident incident,
            String previousStatus,
            String newStatus) {

        IncidentStatusChangedData data =
                new IncidentStatusChangedData(
                        previousStatus,
                        newStatus
                );

        IncidentStatusChangedEvent event =
                new IncidentStatusChangedEvent(
                        UUID.randomUUID(),
                        "INCIDENT_STATUS_CHANGED",
                        incident.getId(),
                        Instant.now().toString(),
                        data
                );

        publishSafely(
                INCIDENT_STATUS_CHANGED_TOPIC,
                String.valueOf(incident.getId()),
                event,
                incident.getId()
        );
    }

    private void publishSafely(
            String topic,
            String key,
            Object event,
            Long incidentId) {

        try {

            kafkaTemplate.send(topic, key, event)
                    .whenComplete((result, exception) -> {

                        if (exception != null) {
                            System.err.println(
                                    "Failed to publish Kafka event. "
                                            + "topic=" + topic
                                            + ", incidentId=" + incidentId
                                            + ", error="
                                            + exception.getMessage()
                            );
                        }
                    });

        } catch (RuntimeException exception) {

            System.err.println(
                    "Kafka unavailable. Event could not be published. "
                            + "topic=" + topic
                            + ", incidentId=" + incidentId
                            + ", error="
                            + exception.getMessage()
            );
        }
    }
}