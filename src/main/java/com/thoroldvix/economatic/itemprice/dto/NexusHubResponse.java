package com.thoroldvix.economatic.itemprice.dto;

import java.util.List;


public record NexusHubResponse(
        String slug,
        List<NexusHubResponse.NexusHubPrice> data
) {
    public record NexusHubPrice(
            int itemId,
            long minBuyout,
            long historicalValue,
            long marketValue,
            int quantity,
            int numAuctions
            ){}
}
