package com.sonu.exceptions;

public class IncidentNotFoundException extends RuntimeException {

    public IncidentNotFoundException(Long id) {
        super("Incident with id " + id + " was not found");
    }
}