package com.thoroldvix.pricepal.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ServerPriceResponse(
        long id,
        BigDecimal price,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String serverName,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDateTime updatedAt

) {
}