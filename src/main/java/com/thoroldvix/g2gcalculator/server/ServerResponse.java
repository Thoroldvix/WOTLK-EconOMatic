package com.thoroldvix.g2gcalculator.server;

import com.thoroldvix.g2gcalculator.price.PriceResponse;
import lombok.Builder;

@Builder
public record ServerResponse (
        int id,
        String name,
        Faction faction,
        Region region,
        PriceResponse price,
        String type,
        PopulationResponse population
) {

    public String getFullServerName() {
        return name.replaceAll(" ", "-").toLowerCase()
               + "-" + faction.name().toLowerCase();
    }
}