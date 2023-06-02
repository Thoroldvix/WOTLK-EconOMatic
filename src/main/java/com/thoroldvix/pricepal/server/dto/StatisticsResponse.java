package com.thoroldvix.pricepal.server.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record StatisticsResponse(
        BigDecimal average,
        BigDecimal median,
        ServerPriceResponse min,
        ServerPriceResponse max
) {
}
