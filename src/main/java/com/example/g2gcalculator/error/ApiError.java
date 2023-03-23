package com.example.g2gcalculator.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Data
public class ApiError {
    private int statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    private Instant timestamp;


    public ApiError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = Instant.now();
    }
    public ApiError(int statusCode) {
        this.statusCode = statusCode;
        this.timestamp = Instant.now();
    }
}