package com.thoroldvix.pricepal.itemprice;

import java.util.List;

public record NexusHubResponse(
        String slug,
        List<NexusHubResponse.NexusHubPrice> data
) {
    protected record NexusHubPrice(
            int itemId,
            long minBuyout,
            long historicalValue,
            long marketValue,
            int quantity,
            int numAuctions
            ){}
}
