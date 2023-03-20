package com.example.g2gcalculator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder

public record RealmResponse(
        Integer id,
        String name,
        PriceResponse price,
        List<AuctionHouseResponse> auctionHouses
) {
}