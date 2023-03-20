package com.example.g2gcalculator.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Builder

public record PriceResponse(
        BigDecimal value
) {
}