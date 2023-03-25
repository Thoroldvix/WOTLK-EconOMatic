package com.example.g2gcalculator.dto;

import lombok.Builder;

@Builder
public record ItemResponse(
        Integer auctionHouseId,
        Integer itemId,
        Integer minBuyout,
        Integer quantity,
        Integer marketValue,
        Integer historical,
        Integer numAuctions
) {
}