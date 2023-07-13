package com.thoroldvix.economatic.itemprice;

import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record ItemPriceResponse(
        Integer itemId,
        String itemName,
        String server,
        long minBuyout,
        long historicalValue,
        long marketValue,
        int quantity,
        int numAuctions,
        LocalDateTime updatedAt
) {
}
