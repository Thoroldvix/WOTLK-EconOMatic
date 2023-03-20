package com.example.g2gcalculator.dto;

import lombok.Builder;

@Builder
public record AuctionHouseResponse(
        Integer id,
        String type
) {
}