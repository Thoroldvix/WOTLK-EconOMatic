package com.thoroldvix.g2gcalculator.server;

import lombok.Builder;

@Builder
public record ServerResponse(
        int id,
        String name,
        Faction faction,
        String region

) {
}