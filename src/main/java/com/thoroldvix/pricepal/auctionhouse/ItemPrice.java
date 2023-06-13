package com.thoroldvix.pricepal.auctionhouse;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ItemPrice(
        int id,
        long minBuyout,
        long historicalValue,
        long marketValue,
        int quantity,
        int numAuctions,
        LocalDateTime lastUpdated
) {

}
