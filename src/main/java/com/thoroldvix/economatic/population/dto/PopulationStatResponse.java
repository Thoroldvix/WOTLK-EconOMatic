package com.thoroldvix.economatic.population.dto;

import lombok.Builder;

@Builder
public record PopulationStatResponse(
        int mean,
        int median,
        PopulationResponse minimum,
        PopulationResponse maximum,
        long count
) {
}
