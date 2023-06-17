package com.thoroldvix.pricepal.population;

import lombok.Builder;

@Builder
public record TotalPopResponse(
        int popAlliance,
        int popHorde,
        int popTotal,
        String name
) {
}
