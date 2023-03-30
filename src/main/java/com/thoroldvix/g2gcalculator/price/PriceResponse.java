package com.thoroldvix.g2gcalculator.price;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PriceResponse(
        BigDecimal value,

        String serverName,

        LocalDateTime updatedAt,

        String currency
) {
}