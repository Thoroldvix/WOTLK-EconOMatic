package com.thoroldvix.g2gcalculator.server;

import java.time.LocalDateTime;

public record PopulationResponse(
        LocalDateTime lastUpdate,
        String name,
        int popAlliance,
        String popDesc,
        int popHorde,
        Region region,
        ServerType type,
        String status,
        String slug
) {
}
