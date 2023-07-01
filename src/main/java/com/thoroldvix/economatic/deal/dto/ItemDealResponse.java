package com.thoroldvix.economatic.deal.dto;

import lombok.Builder;

import java.math.BigDecimal;


@Builder
public record ItemDealResponse(
    int itemId,
    long marketValue,
    long minBuyout,
    long dealDiff,
    String server,
    BigDecimal discountPercentage,
    String itemName
) {
}
