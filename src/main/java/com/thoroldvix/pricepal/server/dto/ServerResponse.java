package com.thoroldvix.pricepal.server.dto;

import com.thoroldvix.pricepal.server.entity.Faction;
import com.thoroldvix.pricepal.server.entity.Region;
import lombok.Builder;

@Builder
public record ServerResponse(
        int id,
        String name,
        Faction faction,
        Region region,
        ServerPriceResponse price,
        String type,
        PopulationResponse population,
        String uniqueName
) {

}