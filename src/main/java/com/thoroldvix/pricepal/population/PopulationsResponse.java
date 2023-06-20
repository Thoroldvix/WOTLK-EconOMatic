package com.thoroldvix.pricepal.population;

import lombok.Builder;

import java.util.List;

@Builder
public record PopulationsResponse(
        String server,
        String region,
        String faction,
        List<PopulationResponse> populations
) {
}
