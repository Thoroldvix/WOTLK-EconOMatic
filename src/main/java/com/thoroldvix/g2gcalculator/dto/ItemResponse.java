package com.thoroldvix.g2gcalculator.dto;

import lombok.Builder;

@Builder
public record ItemResponse(
        Integer auctionHouseId,
        Integer itemId,
        Long minBuyout,
        Integer quantity,
        Long marketValue,
        Long historical,
        Integer numAuctions
) {
}