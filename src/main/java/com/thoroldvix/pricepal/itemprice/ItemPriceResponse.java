package com.thoroldvix.pricepal.itemprice;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ItemPriceResponse(
        int itemId,
        long minBuyout,
        long historicalValue,
        long marketValue,
        int quantity,
        int numAuctions,
        LocalDateTime updatedAt
) {
}
