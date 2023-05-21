package com.thoroldvix.g2gcalculator.server.dto;

import com.thoroldvix.g2gcalculator.server.entity.Region;

import java.time.LocalDateTime;

public record PopulationResponse(
        LocalDateTime lastUpdate,
        String name,
        int popAlliance,
        String popDesc,
        int popHorde,
        Region region,
        String type,
        String status,
        String slug
) {
}
