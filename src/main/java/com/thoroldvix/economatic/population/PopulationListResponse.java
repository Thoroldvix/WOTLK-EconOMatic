package com.thoroldvix.economatic.population;

import lombok.Builder;

import java.util.List;

@Builder
public record PopulationListResponse(
        List<PopulationResponse> populations
) {

}
