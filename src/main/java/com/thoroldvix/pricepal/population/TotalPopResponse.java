package com.thoroldvix.pricepal.population;

import lombok.Builder;

@Builder
public record TotalPopResponse(
        int hordePop,
        int alliancePop,
        int totalPop,
        String serverName
) {
}
