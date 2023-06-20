package com.thoroldvix.pricepal.itemprice;

import lombok.Builder;

import java.util.List;

@Builder
public record ItemDealsResponse(
        String server,
        String region,
        String faction,
        List<ItemDealResponse> deals
) {
}
