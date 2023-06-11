package com.thoroldvix.pricepal.server.dto;

import lombok.Builder;

@Builder
public record TotalPopResponse(
        int hordePop,
        int alliancePop,
        int totalPop,
        String serverName
) {
}
