package com.thoroldvix.pricepal.item.dto;

import java.util.List;

public record FullAuctionHouseInfo(
        String slug,
        List<FullItemInfo> items
) {
}
