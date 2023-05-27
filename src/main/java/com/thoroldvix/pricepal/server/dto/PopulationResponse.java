package com.thoroldvix.pricepal.server.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PopulationResponse(
        String serverName,
        int population,
        LocalDateTime updatedAt
) {
}
