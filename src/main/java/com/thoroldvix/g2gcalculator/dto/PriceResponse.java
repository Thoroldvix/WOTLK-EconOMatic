package com.thoroldvix.g2gcalculator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PriceResponse(
        BigDecimal value,

        String realmName,

        LocalDateTime updatedAt,

        String currency
) {
}