package com.thoroldvix.g2gcalculator.item.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ItemPrice(
        int itemId,
        String server,
        long minBuyout,
        long marketValue,
        int quantity,
        int numAuctions,
        LocalDateTime lastUpdated
) {

}
