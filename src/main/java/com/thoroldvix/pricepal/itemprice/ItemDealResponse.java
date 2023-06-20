package com.thoroldvix.pricepal.itemprice;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
record ItemDealResponse(
    int itemId,
    long marketValue,
    long minBuyout,
    long dealDiff,
    String server,
    BigDecimal discountPercentage,
    String itemName
) {
}
