package com.thoroldvix.economatic.stats.population;

import com.thoroldvix.economatic.population.PopulationResponse;
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
