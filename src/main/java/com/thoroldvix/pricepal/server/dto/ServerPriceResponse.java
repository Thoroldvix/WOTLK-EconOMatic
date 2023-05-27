package com.thoroldvix.pricepal.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thoroldvix.pricepal.server.entity.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ServerPriceResponse(
        BigDecimal value,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String serverName,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDateTime updatedAt,

        Currency currency
) {
}