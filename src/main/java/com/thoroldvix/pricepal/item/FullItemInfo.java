package com.thoroldvix.pricepal.item;

import com.thoroldvix.pricepal.auctionhouse.ItemPrice;
import lombok.Builder;

@Builder
public record FullItemInfo(
        ItemInfo itemInfo,
        ItemPrice auctionInfo
) {
}
