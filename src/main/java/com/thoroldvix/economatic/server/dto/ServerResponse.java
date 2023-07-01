package com.thoroldvix.economatic.server.dto;

import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import lombok.Builder;

@Builder
public record ServerResponse(
        int id,
        String name,
        Faction faction,
        Region region,
        String type,
        String uniqueName
) {

}