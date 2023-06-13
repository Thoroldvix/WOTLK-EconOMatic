package com.thoroldvix.pricepal.auctionhouse;

import com.thoroldvix.pricepal.item.FullItemInfo;

import java.util.List;

public record FullAuctionHouseInfo(
        String slug,
        List<FullItemInfo> items
) {
}
