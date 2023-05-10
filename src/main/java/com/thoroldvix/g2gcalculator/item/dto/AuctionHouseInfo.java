package com.thoroldvix.g2gcalculator.item.dto;

import lombok.Builder;

@Builder
public record AuctionHouseInfo(
        int itemId,
        long minBuyout,
        long marketValue,
        int quantity,
        int numAuctions
) {

}
