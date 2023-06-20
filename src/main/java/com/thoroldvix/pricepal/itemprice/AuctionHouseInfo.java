package com.thoroldvix.pricepal.itemprice;

import lombok.Builder;

import java.util.List;

@Builder
public record AuctionHouseInfo(
        String faction,
        String region,
        String server,
        Integer itemId,
        String itemName,
        List<ItemPriceResponse> prices
) {
}
