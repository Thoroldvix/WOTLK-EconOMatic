package com.thoroldvix.economatic.population.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PopulationResponse(
        String server,
        int value,
        LocalDateTime updatedAt
)  {

}
