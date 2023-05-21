package com.thoroldvix.g2gcalculator.server.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ServerPrice(
        BigDecimal value,

        String serverName,

        LocalDateTime updatedAt,

        String currency
) {
}