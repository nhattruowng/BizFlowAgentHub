package com.bizflow.shared.contracts;

import java.time.Instant;

public class ErrorResponse {
    private String error;
    private String message;
    private String correlationId;
    private Instant timestamp;

    public ErrorResponse() {}

    public ErrorResponse(String error, String message, String correlationId, Instant timestamp) {
        this.error = error;
        this.message = message;
        this.correlationId = correlationId;
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
