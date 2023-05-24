package com.thoroldvix.pricepal.server.dto;

import lombok.Builder;

@Builder
public record PopulationResponse(
        int popAlliance,
        int popHorde
) {
}
