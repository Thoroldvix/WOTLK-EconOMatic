package com.thoroldvix.pricepal.itemprice;

import lombok.Builder;

import java.util.List;

@Builder
public record AuctionHouseInfo(
        String server,
        int itemId,
        String itemName,
        List<ItemPriceResponse> items
) {

}
