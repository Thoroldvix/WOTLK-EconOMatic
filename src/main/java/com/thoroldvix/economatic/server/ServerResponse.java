package com.thoroldvix.economatic.server;

import lombok.Builder;

@Builder
public record ServerResponse(
        int id,
        String name,
        Faction faction,
        Region region,
        ServerType type,
        String uniqueName
) {

}