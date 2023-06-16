package com.thoroldvix.pricepal.itemprice;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ItemDealsResponse(
    int itemId,
    long marketValue,
    long minBuyout,
    long dealDiff,
    BigDecimal dealPercentage,
    String name,
    String uniqueName
) {
}
