package com.sonu.repository;

import com.sonu.entity.IncidentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IncidentHistoryRepository
        extends JpaRepository<IncidentHistory, Long> {

    List<IncidentHistory> findByIncidentIdOrderByChangedAtAsc(Long incidentId);

    boolean existsByEventId(UUID eventId);
}