package com.thoroldvix.economatic.population;

import lombok.Builder;

@Builder
public record TotalPopResponse(
        int popAlliance,
        int popHorde,
        int popTotal,
        String serverName
) {
}
