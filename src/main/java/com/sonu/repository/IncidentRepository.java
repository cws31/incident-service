package com.sonu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sonu.entity.Incident;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
}