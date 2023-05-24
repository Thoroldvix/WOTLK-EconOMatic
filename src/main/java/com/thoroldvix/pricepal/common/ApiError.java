package com.thoroldvix.pricepal.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;

@Data
public class ApiError {
    private int statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reason;
    private Instant timestamp;

    public ApiError(int statusCode, String reason) {
        this.statusCode = statusCode;
        this.reason = reason;
        this.timestamp = Instant.now();
    }
    public ApiError(int statusCode) {
        this.statusCode = statusCode;
        this.timestamp = Instant.now();
    }
}