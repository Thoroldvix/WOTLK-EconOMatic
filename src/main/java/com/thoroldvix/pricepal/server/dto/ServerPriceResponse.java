package com.thoroldvix.pricepal.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ServerPriceResponse(
        BigDecimal value,

        @JsonIgnore
        String serverName,

        LocalDateTime lastUpdated,

        String currency
) {
}