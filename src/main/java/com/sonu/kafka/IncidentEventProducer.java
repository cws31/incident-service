package com.sonu.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.sonu.entity.Incident;

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
                Instant.now(),
                data);

        kafkaTemplate.send(
                INCIDENT_CREATED_TOPIC,
                String.valueOf(incident.getId()),
                event);
    }
}