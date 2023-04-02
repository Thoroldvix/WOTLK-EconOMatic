package com.thoroldvix.g2gcalculator.server;

import com.thoroldvix.g2gcalculator.price.PriceResponse;
import lombok.Builder;

@Builder
public record ServerResponse(
        int id,
        String name,
        Faction faction,
        String region,
        PriceResponse price
) {
}