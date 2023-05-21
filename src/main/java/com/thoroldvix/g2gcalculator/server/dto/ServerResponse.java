package com.thoroldvix.g2gcalculator.server.dto;

import com.thoroldvix.g2gcalculator.server.entity.Faction;
import com.thoroldvix.g2gcalculator.server.entity.Region;
import lombok.Builder;

@Builder
public record ServerResponse (
        int id,
        String name,
        Faction faction,
        Region region,
        ServerPrice price,
        String type,
        PopulationResponse population
) {

    public String getFullServerName() {
        return name.replaceAll(" ", "-").toLowerCase()
               + "-" + faction.name().toLowerCase();
    }
}