package com.thoroldvix.g2gcalculator.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoroldvix.g2gcalculator.price.PriceResponse;
import lombok.Builder;

@Builder
public record ServerResponse (
        int id,
        String name,
        Faction faction,
        Region region,
        PriceResponse price
) {

    public String getFormattedServername() {
        return name.replaceAll(" ", "-").toLowerCase()
               + "-" + faction.name().toLowerCase();
    }
}