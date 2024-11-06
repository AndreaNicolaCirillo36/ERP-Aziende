package com.azienda.erp.erp_backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
    private List<String> details;

    public ErrorResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now(ZoneId.of("Europe/Rome"));
    }

    public ErrorResponse(String message, int statusCode, List<String> details) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now(ZoneId.of("Europe/Rome"));
        this.details = details;
    }
}
