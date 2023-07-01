package com.thoroldvix.economatic.population.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PopulationListResponse(
        List<PopulationResponse> populations
) {
}
