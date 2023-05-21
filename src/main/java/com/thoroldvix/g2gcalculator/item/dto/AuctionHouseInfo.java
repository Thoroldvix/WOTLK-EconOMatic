package com.thoroldvix.g2gcalculator.item.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuctionHouseInfo(
        int itemId,
        String server,
        long minBuyout,
        long marketValue,
        int quantity,
        int numAuctions,
        LocalDateTime lastUpdated
) {

}
