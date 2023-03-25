package com.example.g2gcalculator.dto;

import java.time.Instant;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Instant issuedAt,
        Long expiresIn
) {

}