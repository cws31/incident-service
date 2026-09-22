package com.sonu.kafka;

import com.sonu.entity.Incident;

public interface EventPublisher {

    void publishIncidentCreated(Incident incident);

    void publishIncidentStatusChanged(
            Incident incident,
            String previousStatus,
            String newStatus
    );
}