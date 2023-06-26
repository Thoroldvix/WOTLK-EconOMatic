package com.thoroldvix.economatic.itemprice;

import java.util.List;


record NexusHubResponse(
        String slug,
        List<NexusHubResponse.NexusHubPrice> data
) {
    record NexusHubPrice(
            int itemId,
            long minBuyout,
            long historicalValue,
            long marketValue,
            int quantity,
            int numAuctions
            ){}
}
