package com.thoroldvix.pricepal.item.dto;

import lombok.Builder;

@Builder
public record FullItemInfo(
        ItemInfo itemInfo,

        ItemPrice auctionInfo

) {
}
