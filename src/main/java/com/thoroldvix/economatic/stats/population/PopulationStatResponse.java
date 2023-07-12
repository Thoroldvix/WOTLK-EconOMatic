package com.thoroldvix.economatic.stats.population;

import com.thoroldvix.economatic.population.dto.PopulationResponse;
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
