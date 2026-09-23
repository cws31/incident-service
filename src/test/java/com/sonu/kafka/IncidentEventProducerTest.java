package com.sonu.kafka;

import com.sonu.entity.Incident;
import com.sonu.enums.IncidentStatus;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private IncidentEventProducer incidentEventProducer;

    @Test
    void publishIncidentCreated_shouldNotThrowWhenKafkaSendFails() {

        Incident incident = createIncident();

        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();

        failedFuture.completeExceptionally(
                new RuntimeException("Kafka unavailable"));

        when(kafkaTemplate.send(
                eq("incident.created"),
                eq("101"),
                any(IncidentCreatedEvent.class))).thenReturn(failedFuture);

        assertDoesNotThrow(() -> incidentEventProducer.publishIncidentCreated(incident));

        verify(kafkaTemplate).send(
                eq("incident.created"),
                eq("101"),
                any(IncidentCreatedEvent.class));
    }

    @Test
    void publishIncidentStatusChanged_shouldNotThrowWhenKafkaSendFails() {

        Incident incident = createIncident();

        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();

        failedFuture.completeExceptionally(
                new RuntimeException("Kafka unavailable"));

        when(kafkaTemplate.send(
                eq("incident.status.changed"),
                eq("101"),
                any(IncidentStatusChangedEvent.class))).thenReturn(failedFuture);

        assertDoesNotThrow(() -> incidentEventProducer.publishIncidentStatusChanged(
                incident,
                "REPORTED",
                "ANALYZING"));

        verify(kafkaTemplate).send(
                eq("incident.status.changed"),
                eq("101"),
                any(IncidentStatusChangedEvent.class));
    }

    private Incident createIncident() {

        Incident incident = new Incident();

        incident.setId(101L);
        incident.setTitle("Test incident");
        incident.setDescription("Testing Kafka failure handling");
        incident.setLatitude(16.3);
        incident.setLongitude(80.43);
        incident.setStatus(IncidentStatus.REPORTED);

        return incident;
    }
}