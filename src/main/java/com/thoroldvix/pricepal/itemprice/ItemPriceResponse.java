package com.thoroldvix.pricepal.itemprice;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.thoroldvix.pricepal.item.ItemResponse;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ItemPriceResponse(
        long minBuyout,
        long historicalValue,
        long marketValue,
        int quantity,
        int numAuctions,
        LocalDateTime updatedAt
) {
}
