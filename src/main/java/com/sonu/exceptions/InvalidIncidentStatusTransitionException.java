package com.sonu.exceptions;

import com.sonu.enums.IncidentStatus;

public class InvalidIncidentStatusTransitionException extends RuntimeException {

    public InvalidIncidentStatusTransitionException(
            IncidentStatus currentStatus,
            IncidentStatus requestedStatus) {
        super(
                "Invalid status transition from "
                        + currentStatus
                        + " to "
                        + requestedStatus);
    }
}