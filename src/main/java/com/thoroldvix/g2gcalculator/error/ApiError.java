package com.thoroldvix.g2gcalculator.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ApiError {
    private int statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> message;

    private Instant timestamp;


    public ApiError(int statusCode, String error, String... messages) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = List.of(messages);
        this.timestamp = Instant.now();
    }
    public ApiError(int statusCode) {
        this.statusCode = statusCode;
        this.timestamp = Instant.now();
    }
}