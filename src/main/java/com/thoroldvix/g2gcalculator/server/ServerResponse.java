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
        ServerType type,
        PopulationResponse population
) {

    public String getFormattedServername() {
        return name.replaceAll(" ", "-").toLowerCase()
               + "-" + faction.name().toLowerCase();
    }
}