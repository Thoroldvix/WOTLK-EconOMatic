package com.thoroldvix.economatic.goldprice;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record GoldPriceResponse(
        BigDecimal price,
        String server,
        LocalDateTime updatedAt
) {

}